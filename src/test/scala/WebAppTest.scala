import zio.*
import zio.http.*
import zio.redis.embedded.EmbeddedRedis
import zio.redis.{CodecSupplier, Redis}

object WebAppTest extends ZIOAppDefault:

  def run = Server.serve(WebApp.routes).provide(
    Server.configured(),
    EmbeddedRedis.layer,
    Redis.singleNode,
    ZLayer.succeed[CodecSupplier](WebApp.ProtobufCodecSupplier),
  )
