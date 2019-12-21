package me.kerfume.reminder.domain.remind

import java.time.LocalDateTime
import java.time.LocalDate
import me.kerfume.reminder.domain.seqid.SeqID
import Remind.RemindStatus
import monocle.Lens
import monocle.macros.GenLens

case class RemindBase(
    seqID: SeqID,
    title: String,
    content: Option[String], // 初期段階では実装しない
    status: RemindStatus
)

sealed trait Remind {
  val base: RemindBase

  def seqID: SeqID = base.seqID
  def title: String = base.title
  def content: Option[String] = base.content
  def status: RemindStatus = base.status

  def resolve: Remind
  def triggered: Remind
}

object Remind {
  sealed trait TriggerIsTime { self: TriggerIsTime =>
  }

  case class OfDate(
      base: RemindBase,
      trigger: LocalDate
  ) extends Remind
      with TriggerIsTime {
    def statusL: Lens[OfDate, RemindStatus] = GenLens[OfDate](_.base.status)
    def resolve: Remind = statusL.set(RemindStatus.Resolved)(this)
    def triggered: Remind = statusL.set(RemindStatus.Unresolved)(this)
  }

  case class OfDateTime(
      base: RemindBase,
      trigger: LocalDateTime
  ) extends Remind
      with TriggerIsTime {
    def statusL: Lens[OfDateTime, RemindStatus] =
      GenLens[OfDateTime](_.base.status)
    def resolve: Remind = statusL.set(RemindStatus.Resolved)(this)
    def triggered: Remind = statusL.set(RemindStatus.Unresolved)(this)
  }

  // Todo(init status) --limit over--> Unresolved ----> Resolved
  sealed trait RemindStatus
  object RemindStatus {
    case object Todo extends RemindStatus
    case object Unresolved extends RemindStatus
    case object Resolved extends RemindStatus
  }
}
