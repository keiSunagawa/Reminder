package me.kerfume.reminder.domain.seqid

case class SeqID(num: Long) {
  def next: SeqID = SeqID(num + 1)
}
