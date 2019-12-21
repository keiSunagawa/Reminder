package me.kerfume.reminder.batch.send_reminder

import java.time.OffsetDateTime

import cats.effect.IO
import me.kerfume.reminder.batch.Application
import cats.Monad.ops._

class Executor(
    app: Application
) {
  def exec(): IO[Unit] = {
    val now = OffsetDateTime.now(
      java.time.ZoneOffset.ofHours(9)
    )
    app.remindService.remindMe(now.toLocalDateTime) *>
      app.consumer.commit()
  }
}
