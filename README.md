Hello ZIO HTTP
--------------

Run with embedded Redis:
```
./sbt ~reStartTest
```

Run for production:
```
export REDIS_URL=YOUR_REDIS_URL
./sbt ~reStart
```
