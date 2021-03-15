import com.typesafe.sbt.packager.docker.{Cmd, DockerPermissionStrategy, Dockerfile, ExecCmd}

import java.io.ByteArrayInputStream

enablePlugins(GraalVMNativeImagePlugin, DockerPlugin)

name := "hello-zio-http-graalvm"

scalaVersion := "2.13.5"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-explaintypes",
  "-feature",
  "-Wconf:any:error",
  "-Wunused",
  "-Wvalue-discard",
)

libraryDependencies ++= Seq(
  "io.d11" % "zhttp" % "1.0.0-RC1",
  "org.scalameta" %% "svm-subs"     % "20.2.0",
)

Global / sources in (Compile,doc) := Seq.empty
Global / publishArtifact in (Compile, packageDoc) := false

val genBaseImage = TaskKey[Option[String]]("genBaseImage")

// from: https://github.com/sbt/sbt-native-packager/blob/master/src/main/scala/com/typesafe/sbt/packager/graalvmnativeimage/GraalVMNativeImagePlugin.scala
genBaseImage := Def.task {
  val dockerCommand = (DockerPlugin.autoImport.dockerExecCommand in GraalVMNativeImage).value
  val streams = Keys.streams.value

  val baseName = "ghcr.io/graalvm/graalvm-ce"
  val tag = "21.0.0"

  val imageName = s"${baseName.replace('/', '-')}-native-image:$tag"
  import sys.process._
  if ((dockerCommand ++ Seq("image", "ls", imageName, "--quiet")).!!.trim.isEmpty) {
    streams.log.info(s"Generating new GraalVM native-image image based on $baseName:$tag = $imageName")

    val dockerContent = Dockerfile(
      Cmd("FROM", s"$baseName:$tag"),
      Cmd("WORKDIR", "/opt/graalvm"),
      ExecCmd("RUN", "gu", "install", "native-image"),
      Cmd("ARG", """RESULT_LIB="/staticlibs""""),
      Cmd("RUN",
        """mkdir ${RESULT_LIB} && \
          |curl -L -o musl.tar.gz https://musl.libc.org/releases/musl-1.2.1.tar.gz && \
          |mkdir musl && tar -xvzf musl.tar.gz -C musl --strip-components 1 && cd musl && \
          |./configure --disable-shared --prefix=${RESULT_LIB} && \
          |make && make install && \
          |cp /usr/lib/gcc/x86_64-redhat-linux/8/libstdc++.a ${RESULT_LIB}/lib/""".stripMargin),
      Cmd("ENV", """PATH="$PATH:${RESULT_LIB}/bin""""),
      Cmd("ENV", """CC="musl-gcc""""),
      Cmd("RUN",
        """curl -L -o zlib.tar.gz https://zlib.net/zlib-1.2.11.tar.gz && \
          |mkdir zlib && tar -xvzf zlib.tar.gz -C zlib --strip-components 1 && cd zlib && \
          |./configure --static --prefix=${RESULT_LIB} && \
          |make && make install""".stripMargin),
      ExecCmd("ENTRYPOINT", "native-image")
    ).makeContent

    val command = dockerCommand ++ Seq("build", "-t", imageName, "-")

    val ret = sys.process.Process(command) #<
      new ByteArrayInputStream(dockerContent.getBytes()) !
      new sys.process.ProcessLogger {
        override def out(s: => String): Unit = streams.log.info(s)
        override def err(s: => String): Unit = streams.log.err(s)
        override def buffer[T](f: => T) = f
      }

    if (ret != 0)
      throw new RuntimeException("Nonzero exit value when generating GraalVM container build image: " + ret)

  } else
    streams.log.info(s"Using existing GraalVM native-image image: $imageName")

  Some(imageName)
}.value

GraalVMNativeImage / containerBuildImage := genBaseImage.value

graalVMNativeImageOptions ++= Seq(
  "--verbose",
  "--no-server",
  "--allow-incomplete-classpath",
  "--no-fallback",
  "--static",
  "--install-exit-handlers",
  "--libc=musl",
  "-H:+ReportExceptionStackTraces",
  "-H:+RemoveSaturatedTypeFlows",
  "--initialize-at-run-time=io.netty.channel.epoll.Epoll",
  "--initialize-at-run-time=io.netty.channel.epoll.Native",
  "--initialize-at-run-time=io.netty.channel.epoll.EpollEventLoop",
  "--initialize-at-run-time=io.netty.channel.epoll.EpollEventArray",
  "--initialize-at-run-time=io.netty.channel.DefaultFileRegion",
  "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventArray",
  "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventLoop",
  "--initialize-at-run-time=io.netty.channel.kqueue.Native",
  "--initialize-at-run-time=io.netty.channel.unix.Errors",
  "--initialize-at-run-time=io.netty.channel.unix.IovArray",
  "--initialize-at-run-time=io.netty.channel.unix.Limits",
  "--initialize-at-run-time=io.netty.util.internal.logging.Log4JLogger",
)

Docker / mappings := Seq(
  ((GraalVMNativeImage / packageBin).value, dockerEntrypoint.value.head)
)

dockerUpdateLatest := true
dockerBaseImage := "scratch"
daemonUserUid in Docker := None
dockerPermissionStrategy := DockerPermissionStrategy.None
dockerCmd := Seq.empty

dockerCommands := dockerCommands.value.filterNot {
  case Cmd("USER", _) => true
  case _              => false
}

val maybeDockerSettings = sys.props.get("dockerImageUrl").flatMap { imageUrl =>
  val parts = imageUrl.split("/")
  if (parts.size == 3) {
    Some((parts(0), parts(1), parts(2)))
  }
  else {
    None
  }
}

dockerRepository := maybeDockerSettings.map(_._1)
dockerUsername := maybeDockerSettings.map(_._2)
packageName in Docker := maybeDockerSettings.map(_._3).getOrElse(name.value)