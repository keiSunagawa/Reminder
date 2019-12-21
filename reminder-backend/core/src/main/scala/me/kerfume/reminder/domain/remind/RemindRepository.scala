package me.kerfume.reminder.domain.remind
import java.time.LocalDateTime
import me.kerfume.reminder.domain.remind.Remind.TriggerIsTime
import cats.Functor
import Functor.ops._
import me.kerfume.reminder.domain.remind.Remind.RemindStatus
import me.kerfume.reminder.domain.seqid.SeqID

abstract class RemindRepository[F[_]: Functor] {
  def store(remind: Remind): F[Unit]
  def findByTriggerIsTime(
      now: LocalDateTime
  ): F[List[Remind with TriggerIsTime]]
  def findAll(): F[List[Remind]]
  def findByLived(): F[List[Remind]] = findAll().map {
    _.filter(_.status != RemindStatus.Resolved)
  }
  def findByID(id: SeqID): F[Option[Remind]] = findAll().map {
    _.find(_.seqID == id)
  }
}

import cats.Id

class RemindRepositoryInMemory extends RemindRepository[Id] {
  var reminds: Map[SeqID, Remind] = Map.empty

  def store(remind: Remind): Id[Unit] = {
    reminds = reminds + (remind.seqID -> remind)
  }

  def findByTriggerIsTime(
      now: LocalDateTime
  ): Id[List[Remind with TriggerIsTime]] = {
    reminds.values.toList.collect {
      case r: Remind.OfDate if {
            val dt = r.trigger.atTime(0, 0)
            dt.isBefore(now) || dt.isEqual(now)
          } =>
        r
      case r: Remind.OfDateTime
          if r.trigger.isBefore(now) || r.trigger.isEqual(now) =>
        r
    }: List[Remind with TriggerIsTime]
  }

  def findAll(): Id[List[Remind]] = reminds.values.toList
}
