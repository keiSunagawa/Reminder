package me.kerfume.reminder.server
import org.http4s.server.blaze.BlazeServerBuilder
import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import cats.SemigroupK.ops._
import org.http4s.implicits._

object ReminderServer extends IOApp {
  import sttp.tapir._
  import sttp.tapir.server.http4s._
  import org.http4s.implicits._

  import EndPoints._

  val registRoute: HttpRoutes[IO] =
    regist.toRoutes { p =>
      IO(Application.registController.registByDate(p))
    }

  val listRoute: HttpRoutes[IO] =
    list.toRoutes { p =>
      IO(Application.registController.list())
    }

  val reminderApp: HttpApp[IO] =
    (registRoute <+> listRoute).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(reminderApp)
      .serve
      .compile
      .drain
      .map(_ => ExitCode.Success)
  }
}
