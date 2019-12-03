package me.kerfume.reminder.domain.remind
import java.time.LocalDateTime
import me.kerfume.reminder.domain.remind.Remind.TriggerIsTime

trait RemindRepository[F[_]] {
  def store(remind: Remind): F[Unit]
  def findByTriggerIsTime(
      now: LocalDateTime
  ): F[List[Remind with TriggerIsTime]]
  def findAll(): F[List[Remind]]
}

import cats.Id

class RemindRepositoryInMemory extends RemindRepository[Id] {
  var reminds: List[Remind] = Nil

  def store(remind: Remind): Id[Unit] = {
    reminds = remind :: reminds
  }

  def findByTriggerIsTime(
      now: LocalDateTime
  ): Id[List[Remind with TriggerIsTime]] = {
    reminds.collect {
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

  def findAll(): Id[List[Remind]] = reminds
}
