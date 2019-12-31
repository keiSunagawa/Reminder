package me.kerfume.reminder.domain.remind

import java.time.LocalDateTime
import java.time.LocalDate

import me.kerfume.reminder.domain.seqid.SeqID
import Remind.{RemindStatus, RemindUpdateBase}
import monocle.Lens
import monocle.macros.GenLens

case class RemindBase(
    seqID: SeqID,
    title: String,
    content: Option[String], // 初期段階では実装しない
    status: RemindStatus
)

sealed trait Remind extends RemindUpdateBase[Remind] {
  val base: RemindBase

  def seqID: SeqID = base.seqID
  def title: String = base.title
  def content: Option[String] = base.content
  def status: RemindStatus = base.status

  def resolve: Remind = updateBase(
    base.copy(status = RemindStatus.Resolved)
  )
  def triggered: Remind = updateBase(
    base.copy(status = RemindStatus.Unresolved)
  )
}

object Remind {
  sealed trait RemindUpdateBase[+Repr] {
    def updateBase(newBase: RemindBase): Repr
  }

  sealed trait TriggerIsTime { self: TriggerIsTime =>
  }

  case class OfDate(
      base: RemindBase,
      trigger: LocalDate
  ) extends Remind
      with RemindUpdateBase[OfDate]
      with TriggerIsTime {
    override def updateBase(newBase: RemindBase): OfDate = copy(base = newBase)
  }

  case class OfDateTime(
      base: RemindBase,
      trigger: LocalDateTime
  ) extends Remind
      with RemindUpdateBase[OfDateTime]
      with TriggerIsTime {
    override def updateBase(newBase: RemindBase): OfDateTime =
      copy(base = newBase)
    def statusL: Lens[OfDateTime, RemindStatus] =
      GenLens[OfDateTime](_.base.status)
  }

  // Todo(init status) --limit over--> Unresolved ----> Resolved
  sealed trait RemindStatus
  object RemindStatus {
    case object Todo extends RemindStatus
    case object Unresolved extends RemindStatus
    case object Resolved extends RemindStatus
  }
}
