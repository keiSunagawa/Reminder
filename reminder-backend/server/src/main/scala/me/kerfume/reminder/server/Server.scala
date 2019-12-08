package me.kerfume.reminder.server
import org.http4s.server.blaze.BlazeServerBuilder
import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import cats.SemigroupK.ops._
import org.http4s.implicits._
import me.kerfume.reminder.server.controller.RegistController

object ReminderServer extends IOApp {
  import sttp.tapir._
  import sttp.tapir.server.http4s._
  import org.http4s.implicits._

  import EndPoints._

  def registRoute(registCtr: RegistController[IO]): HttpRoutes[IO] =
    regist.toRoutes { p =>
      registCtr.registByDate(p)
    }

  def listRoute(registCtr: RegistController[IO]): HttpRoutes[IO] =
    list.toRoutes { _ =>
      registCtr.list()
    }

  def reminderApp(
      registCtr: RegistController[IO]
  ): HttpApp[IO] =
    (registRoute(registCtr) <+> listRoute(registCtr)).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    val env =
      if (args.headOption.contains("-prod")) AppConfig.Env.Prod
      else AppConfig.Env.Local

    println(s"start with ${env}")
    val config = AppConfig.getConfig(env)

    val app = new Application(config)

    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(reminderApp(app.registController))
      .serve
      .compile
      .drain
      .map(_ => ExitCode.Success)
  }
}
