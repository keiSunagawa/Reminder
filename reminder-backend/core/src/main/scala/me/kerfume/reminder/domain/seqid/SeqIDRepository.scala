package me.kerfume.reminder.domain.seqid

trait SeqIDRepository[F[_]] {
  def generate(): F[SeqID]
}

import cats.Id

class SeqIDRepositoryInMemory extends SeqIDRepository[Id] {
  var latest = SeqID(1)

  def generate(): Id[SeqID] = {
    val res = latest
    latest = latest.next
    res
  }
}
