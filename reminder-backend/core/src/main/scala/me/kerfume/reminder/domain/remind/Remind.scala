package me.kerfume.reminder.domain.remind

import java.time.LocalDateTime
import java.time.LocalDate
import me.kerfume.reminder.domain.seqid.SeqID

sealed trait Remind {
  val seqID: SeqID
  val title: String
  val content: Option[String]
}

object Remind {
  sealed trait TriggerIsTime { self: TriggerIsTime =>
  }

  case class OfDate(
      seqID: SeqID,
      title: String,
      content: Option[String], // 初期段階では実装しない
      trigger: LocalDate
  ) extends Remind
      with TriggerIsTime

  case class OfDateTime(
      seqID: SeqID,
      title: String,
      content: Option[String], // 初期段階では実装しない
      trigger: LocalDateTime
  ) extends Remind
      with TriggerIsTime
}
