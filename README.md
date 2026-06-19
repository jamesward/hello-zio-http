Hello ZIO HTTP
--------------

Run with a Valkey Testcontainer (requires Docker):
```
./sbt ~Test/runReload
```

Run for production:
```
export REDIS_URL=YOUR_REDIS_URL
./sbt run
```
