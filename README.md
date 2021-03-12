Hello ZIO HTTP
--------------

Run Locally:
```
./sbt run
```

Containerize Locally as a GraalVM native image:
```
docker build -t hello-zio-http .
```

Run container:
```
docker run -p8080:8080 hello-zio-http
```

Run on Cloud Run:

[![Run on Google Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run)