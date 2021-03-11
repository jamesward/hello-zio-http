import zhttp.http._
import zhttp.service.Server
import zio.{App, ExitCode, URIO}

object WebApp extends App {

  val app = Http.text("hello, world")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = Server.start(8080, app).exitCode

}