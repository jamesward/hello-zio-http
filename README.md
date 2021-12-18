Hello ZIO HTTP
--------------

Run Locally:
```
./sbt run
```

Run on Cloud Run:

[![Run on Google Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run)

Containerize Locally as a GraalVM native image:
```
docker build -t hello-zio-http .
```

Run container:
```
docker run -it -p8080:8080 hello-zio-http
```