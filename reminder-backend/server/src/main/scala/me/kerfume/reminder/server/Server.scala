package me.kerfume.reminder.server
import org.http4s.server.blaze.BlazeServerBuilder
import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import cats.SemigroupK.ops._
import org.http4s.implicits._
import me.kerfume.reminder.server.controller.RegistController
import org.http4s.server.middleware.{CORS, CORSConfig}

object ReminderServer extends IOApp {
  import sttp.tapir._
  import sttp.tapir.server.http4s._
  import org.http4s.implicits._

  import EndPoints._

  def registRoute(registCtr: RegistController[IO]): HttpRoutes[IO] =
    regist.toRoutes { p =>
      registCtr.registByDate(p)
    }

  def resolveRoute(registCtr: RegistController[IO]): HttpRoutes[IO] =
    resolve.toRoutes { id =>
      registCtr.resolve(id)
    }

  def listRoute(registCtr: RegistController[IO]): HttpRoutes[IO] =
    list.toRoutes { _ =>
      registCtr.list()
    }

  def reminderApp(
      registCtr: RegistController[IO]
  ): HttpApp[IO] =
    (registRoute(registCtr) <+> listRoute(registCtr) <+> resolveRoute(
      registCtr
    )).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    val env =
      if (args.headOption.contains("-prod")) AppConfig.Env.Prod
      else AppConfig.Env.Local

    println(s"start with ${env}")
    val config = AppConfig.getConfig(env)

    val app = new Application(config)

    import scala.concurrent.duration._
    val originConfig = CORSConfig(
      anyOrigin = true,
      allowCredentials = true,
      maxAge = 1.day.toSeconds
    )
    // With Middlewares in place
    val finalHttpApp = CORS(reminderApp(app.registController), originConfig)
    BlazeServerBuilder[IO]
      .bindHttp(config.launchPort, "0.0.0.0")
      .withHttpApp(finalHttpApp)
      .serve
      .compile
      .drain
      .map(_ => ExitCode.Success)
  }
}
