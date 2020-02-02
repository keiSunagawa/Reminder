package me.kerfume.infra.impl.domain.seqid

import cats.effect.IO
import me.kerfume.reminder.domain.remind.RemindRepository
import me.kerfume.reminder.domain.seqid.{SeqID, SeqIDRepository}

class SeqIDRepositoryRpc(
    reminderRepository: RemindRepository[IO]
) extends SeqIDRepository[IO] {
  override def generate(): IO[SeqID] = {
    reminderRepository.findAll().map { rms =>
      val latest = rms.sortBy(_.seqID.num).reverse.headOption
      latest.map(_.seqID.next).getOrElse(SeqID(1))
    }
  }
}
