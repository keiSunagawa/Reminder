package me.kerfume.reminder.server.controller
import me.kerfume.reminder.domain.remind.RemindService
import cats.{Applicative, Monad}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Monad.ops._
import me.kerfume.reminder.domain.remind.Remind
import cats.syntax.either._
import me.kerfume.reminder.domain.seqid.SeqID
import cats.Bifunctor.ops._
import cats.instances.either._
import sttp.model.Uri
import scala.util.chaining._

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

  def list(): F[ListResponse] = {
    service.list().map {
      _.map {
        case x: Remind.OfDate =>
          RemindModelForView(
            x.base.seqID.num,
            x.base.title,
            dateFormatter.format(x.trigger)
          )
        case x: Remind.OfDateTime =>
          RemindModelForView(
            x.base.seqID.num,
            x.base.title,
            dateFormatter.format(x.trigger)
          )
      }.pipe(ListResponse)
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

  val dateFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd")
  case class RemindModelForView(
      id: Long,
      title: String,
      limit: String
  )
  case class ListResponse(
      values: List[RemindModelForView]
  )
}
