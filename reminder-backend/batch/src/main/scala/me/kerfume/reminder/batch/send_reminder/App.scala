package me.kerfume.reminder.batch.send_reminder

import java.time.OffsetDateTime

import cats.effect.IO
import me.kerfume.reminder.domain.remind.RemindService

class App(
    service: RemindService[IO]
) {
  def exec(): IO[Unit] = {
    val now = OffsetDateTime.now(
      java.time.ZoneOffset.ofHours(9)
    )
    service.remindMe(now.toLocalDateTime)
  }
}
