package me.kerfume.reminder.server.controller
import me.kerfume.reminder.domain.remind.RemindService
import cats.Monad
import java.time.LocalDate
import Monad.ops._
import me.kerfume.reminder.domain.remind.Remind
import cats.syntax.either._

class RegistController[F[_]: Monad](
    service: RemindService[F]
) {
  import RegistController._

  def registByDate(params: RegistByDateParam): F[Either[String, String]] =
    service
      .simpleReminderOfDate(
        params.title,
        params.trigger
      )
      .map { _ =>
        Right("regist ok")
      }

  def list(): F[Either[Unit, String]] = {
    import scalatags.Text.all._
    service.list().map { xs =>
      val ofDates = xs.collect {
        case r: Remind.OfDate =>
          r
      }

      html(
        body(
          h1("Reminds"),
          ul(
            ofDates.map { r =>
              li(s"${r.seqID.num} ${r.title} ${r.trigger}")
            }: _*
          )
        )
      ).toString.asRight
    }
  }
}

object RegistController {
  case class RegistByDateParam(title: String, trigger: LocalDate)
}
