package me.kerfume.reminder.domain.consumer
import me.kerfume.reminder.domain.remind.Remind

trait Consumer[F[_]] {
  def tell(remind: Remind): F[Unit]
}

import cats.Id

class ConsumerInMemory extends Consumer[Id] {
  var mailBox: List[Remind] = Nil

  def tell(remind: Remind): Id[Unit] = {
    mailBox = remind :: mailBox
  }
}
