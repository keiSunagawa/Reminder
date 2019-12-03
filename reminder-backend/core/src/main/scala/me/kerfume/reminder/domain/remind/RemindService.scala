package me.kerfume.reminder.domain.remind

import java.time.LocalDate
import java.time.LocalDateTime
import me.kerfume.reminder.domain.seqid.SeqIDRepository
import cats.Monad
import Monad.ops._
import cats.syntax.traverse._
import cats.instances.list._
import me.kerfume.reminder.domain.consumer.Consumer

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
        id,
        title,
        None,
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
        id,
        title,
        None,
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

  def list(): F[List[Remind]] = repository.findAll()
}
