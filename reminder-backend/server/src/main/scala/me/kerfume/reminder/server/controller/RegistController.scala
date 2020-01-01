package me.kerfume.reminder.server.controller
import me.kerfume.reminder.domain.remind.RemindService
import cats.{Applicative, Monad}
import java.time.LocalDate

import Monad.ops._
import me.kerfume.reminder.domain.remind.Remind
import cats.syntax.either._
import me.kerfume.reminder.domain.seqid.SeqID
import cats.Bifunctor.ops._
import cats.instances.either._
import sttp.model.Uri

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

  def list(): F[String] = {
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
              li(
                s"${r.seqID.num} ${r.title} ${r.trigger}",
                a(href := s"/resolve/${r.seqID.num}")(b("resolve"))
              )
            }: _*
          )
        )
      ).toString
    }
  }

  def resolve(id: Long): F[Either[String, String]] = {
    val seqID = SeqID(id)
    service.resolve(seqID).map {
      _.bimap({
        case RemindService.RemindNotFound(id) => s"id: ${id.num}, not found"
      }, _ => "resolve ok")
    }
  }
}

object RegistController {
  case class RegistByDateParam(title: String, trigger: LocalDate)
  case class WithSessionKey[A](sessionKey: Option[String], params: A)
}
