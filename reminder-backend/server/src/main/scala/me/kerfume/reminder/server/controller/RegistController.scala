package me.kerfume.reminder.server.controller
import me.kerfume.reminder.domain.remind.RemindService
import cats.Monad
import java.time.LocalDate
import Monad.ops._

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
}

object RegistController {
  case class RegistByDateParam(title: String, trigger: LocalDate)
}
