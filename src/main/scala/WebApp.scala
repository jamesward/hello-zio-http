import zio.*
import zio.config.typesafe.*
import zio.http.*
import zio.http.codec.PathCodec
import zio.direct.*
import zio.redis.{CodecSupplier, Redis, RedisConfig}
import zio.schema.Schema
import zio.schema.codec.{BinaryCodec, ProtobufCodec}

import java.net.URI

object WebApp extends ZIOAppDefault:

  object ProtobufCodecSupplier extends CodecSupplier:
    def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath())

  val routes: Routes[Redis, Nothing] =
    Routes:
      Method.GET / PathCodec.empty ->
        handler:
          defer:
            val redis = ZIO.service[Redis].run
            val visits = redis.incr("visits").orDie.run // todo: nice error handling cause currently this is just swallowed
            Response.text(visits.toString)

  val redisUri: ZIO[Any, Throwable, URI] =
    ZIO.systemWith:
      system =>
        system.env("REDIS_URL")
          .someOrFail(new RuntimeException("REDIS_URL env var not set"))
          .map:
            redisUrl =>
              URI(redisUrl)

  val redisConfigLayer: ZLayer[Any, Throwable, RedisConfig] =
    ZLayer.fromZIO:
     defer:
       val uri = redisUri.run
       RedisConfig(uri.getHost, uri.getPort, ssl = true, verifyCertificate = false)

  // may not work with reconnects
  val redisAuthLayer: ZLayer[CodecSupplier & RedisConfig, Throwable, Redis] =
    Redis.singleNode.flatMap:
      env =>
        ZLayer.fromZIO:
          defer:
            val uri = redisUri.run
            val redis = env.get[Redis]
            val password = uri.getUserInfo.drop(1) // REDIS_URL has an empty username
            redis.auth(password).as(redis).run

  def run = Server.serve(routes).provide(
    Server.configured(),
    redisConfigLayer,
    redisAuthLayer,
    ZLayer.succeed[CodecSupplier](ProtobufCodecSupplier),
  )
