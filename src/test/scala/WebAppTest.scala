import com.dimafeng.testcontainers.GenericContainer
import zio.*
import zio.config.typesafe.*
import zio.http.*
import sage.*
import sage.backend.*

object WebAppTest extends ZIOAppDefault:

  // Same as WebApp: read application.conf (incl. zio.http.server.binding-port) instead of ZIO's default env/props provider.
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath())

  // Spin up a real Valkey via Testcontainers, tie its lifecycle to the scope, and point sage at the mapped port.
  val sageClientLayer: ZLayer[Any, Throwable, SageClient] =
    ZLayer.scoped:
      for
        valkey <- ZIO.acquireRelease(
                    ZIO.attemptBlocking:
                      val container = GenericContainer("valkey/valkey:8", exposedPorts = Seq(6379))
                      container.start()
                      container
                  )(container => ZIO.attemptBlocking(container.stop()).orDie)
        client <- SageClient.scoped(
                    SageConfig(topology = Topology.Standalone(Endpoint(valkey.host, valkey.mappedPort(6379))))
                  )
      yield client

  def run = Server.serve(WebApp.routes).provide(
    Server.configured(),
    sageClientLayer,
  )
