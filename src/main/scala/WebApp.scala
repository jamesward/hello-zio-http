import zhttp.http._
import zhttp.service.Server
import zio.{App, ExitCode, URIO, ZIO}

object WebApp extends App {

  val app = Http.collectM[Request] {
    case Method.GET -> !! =>
      ZIO.succeed(Response.text("hello, world"))
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = Server.start(8080, app).exitCode

}