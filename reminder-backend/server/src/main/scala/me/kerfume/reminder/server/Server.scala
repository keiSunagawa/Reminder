package me.kerfume.reminder.server
import org.http4s.server.blaze.BlazeServerBuilder
import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import cats.SemigroupK.ops._
import me.kerfume.infra.impl.session.AuthenticationService
import me.kerfume.infra.impl.session.AuthenticationService.{
  NeedGetTwitterOAuthToken,
  SessionExists
}
import org.http4s.implicits._
import me.kerfume.reminder.server.controller.{
  AuthenticationController,
  RegistController
}

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

  def listRoute(
      registCtr: RegistController[IO],
      aCtr: AuthenticationController
  ): HttpRoutes[IO] =
    list.toRoutes { s =>
      aCtr.withCheckAuthentication(s.sessionKey) {
        registCtr.list().map(Right(_))
      }
    }

  def authRoute(
      aCtr: AuthenticationController
  ): HttpRoutes[IO] =
    twitterAuth.toRoutes {
      case (key, verify) =>
        import cats.syntax.applicativeError._
        println(verify)
        aCtr.authentication(key, verify).onError { e =>
          println(e.getMessage)
          e.printStackTrace()
          IO.unit
        }
    }

  def reminderApp(
      registCtr: RegistController[IO],
      aCtr: AuthenticationController
  ): HttpApp[IO] =
    (registRoute(registCtr) <+> listRoute(registCtr, aCtr) <+> resolveRoute(
      registCtr
    ) <+> authRoute(aCtr)).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    val env =
      if (args.headOption.contains("-prod")) AppConfig.Env.Prod
      else AppConfig.Env.Local

    println(s"start with ${env}")
    val config = AppConfig.getConfig(env)

    val app = new Application(config)

    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(
        reminderApp(app.registController, app.authenticationController)
      )
      .serve
      .compile
      .drain
      .map(_ => ExitCode.Success)
  }
}
