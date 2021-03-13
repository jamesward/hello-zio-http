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
./sbt docker:publishLocal
```

Run container:
```
docker run -p8080:8080 hello-zio-http-jvm
```