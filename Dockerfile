FROM ghcr.io/graalvm/graalvm-ce:21.0.0 as builder

WORKDIR /app
COPY . /app

# Because zio-http is a git submodule
RUN curl -L -o git-core.rpm https://rpmfind.net/linux/centos/8.3.2011/AppStream/x86_64/os/Packages/git-core-2.27.0-1.el8.x86_64.rpm && \
    rpm -iv --nodeps git-core.rpm

RUN gu install native-image

# BEGIN PRE-REQUISITES FOR STATIC NATIVE IMAGES FOR GRAAL
# SEE: https://github.com/oracle/graal/blob/master/substratevm/StaticImages.md
ARG RESULT_LIB="/staticlibs"

RUN mkdir ${RESULT_LIB} && \
    curl -L -o musl.tar.gz https://musl.libc.org/releases/musl-1.2.1.tar.gz && \
    mkdir musl && tar -xvzf musl.tar.gz -C musl --strip-components 1 && cd musl && \
    ./configure --disable-shared --prefix=${RESULT_LIB} && \
    make && make install && \
    cp /usr/lib/gcc/x86_64-redhat-linux/8/libstdc++.a ${RESULT_LIB}/lib/

ENV PATH="$PATH:${RESULT_LIB}/bin"
ENV CC="musl-gcc"

RUN curl -L -o zlib.tar.gz https://zlib.net/zlib-1.2.11.tar.gz && \
   mkdir zlib && tar -xvzf zlib.tar.gz -C zlib --strip-components 1 && cd zlib && \
   ./configure --static --prefix=${RESULT_LIB} && \
    make && make install
#END PRE-REQUISITES FOR STATIC NATIVE IMAGES FOR GRAAL

RUN ./sbt graalvm-native-image:packageBin

FROM scratch

COPY --from=builder /app/target/graalvm-native-image/hello-zio-http /hello-zio-http

ENTRYPOINT ["/hello-zio-http"]