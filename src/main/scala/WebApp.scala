import zio.*
import zio.config.typesafe.*
import zio.http.*
import zio.http.codec.PathCodec
import zio.direct.*
import sage.*
import sage.backend.*

import java.net.URI

object WebApp extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath())

  val routes: Routes[SageClient, Nothing] =
    Routes:
      Method.GET / PathCodec.empty ->
        handler:
          defer:
            val client = ZIO.service[SageClient].run
            val visits = client.incr("visits").orDie.run // todo: nice error handling cause currently this is just swallowed
            Response.text(visits.toString)

  val redisUri: ZIO[Any, Throwable, URI] =
    ZIO.systemWith:
      system =>
        system.env("REDIS_URL")
          .someOrFail(new RuntimeException("REDIS_URL env var not set"))
          .map:
            redisUrl =>
              URI(redisUrl)

  val sageConfig: ZIO[Any, Throwable, SageConfig] =
    redisUri.map:
      uri =>
        val password = uri.getUserInfo.drop(1)
        SageConfig(
          topology = Topology.Standalone(Endpoint(uri.getHost, uri.getPort)),
          auth = Some(AuthConfig(password)),
          tls = Some(TlsConfig(TrustSource.Insecure)),
        )

  val sageClientLayer: ZLayer[Any, Throwable, SageClient] =
    ZLayer.scoped:
      sageConfig.flatMap(SageClient.scoped)

  def run = Server.serve(routes).provide(
    Server.configured(),
    sageClientLayer,
  )
