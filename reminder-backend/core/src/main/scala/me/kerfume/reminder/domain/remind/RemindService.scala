package me.kerfume.reminder.domain.remind

import java.time.LocalDate
import java.time.LocalDateTime
import me.kerfume.reminder.domain.seqid.SeqIDRepository
import cats.Monad
import Monad.ops._
import cats.syntax.traverse._
import cats.instances.list._
import me.kerfume.reminder.domain.consumer.Consumer
import Remind.RemindStatus
import me.kerfume.reminder.domain.seqid.SeqID
import cats.Applicative
import RemindService._

class RemindService[F[_]: Monad](
    repository: RemindRepository[F],
    seqIDRepository: SeqIDRepository[F],
    consumer: Consumer[F]
) {

  def simpleReminderOfDate(
      title: String,
      trigger: LocalDate
  ): F[Unit] = {
    for {
      id <- seqIDRepository.generate()
      remind = Remind.OfDate(
        RemindBase(id, title, None, RemindStatus.Todo),
        trigger
      )
      _ <- repository.store(remind)
    } yield ()
  }

  def simpleReminderOfDateTime(
      title: String,
      trigger: LocalDateTime
  ): F[Unit] = {
    for {
      id <- seqIDRepository.generate()
      remind = Remind.OfDateTime(
        RemindBase(id, title, None, RemindStatus.Todo),
        trigger
      )
      _ <- repository.store(remind)
    } yield ()
  }

  def remindMe(now: LocalDateTime): F[Unit] = {
    for {
      reminds <- repository.findByTriggerIsTime(now)
      // TODO change status PEND?
      _ <- reminds.traverse(consumer.tell)
    } yield ()
  }

  def resolve(id: SeqID): F[Either[ServiceError, Unit]] = {
    for {
      remindOps <- repository.findByID(id)
      res <- remindOps match {
        case Some(r) =>
          val resolved = r.resolve
          println(resolved)
          repository.store(resolved).map(Right(_))
        case None =>
          Applicative[F].pure(Left(RemindNotFound(id)))
      }
    } yield res
  }

  def list(): F[List[Remind]] = repository.findByLived()
}
object RemindService {
  sealed trait ServiceError
  case class RemindNotFound(id: SeqID) extends ServiceError
}
