import zio.*
import zio.config.typesafe.*
import zio.http.*
import zio.http.codec.PathCodec

object WebApp extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath())

  val routes =
    Routes(
      Method.GET / PathCodec.empty -> handler(Response.text("hello, world"))
    )

  def run = Server.serve(routes).provide(Server.configured())
