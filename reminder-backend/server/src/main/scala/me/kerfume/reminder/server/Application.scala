package me.kerfume.reminder.server
import cats.effect.IO
import me.kerfume.infra.impl.domain.remind.RemindRepositoryRpc
import me.kerfume.reminder.server.controller.RegistController
import me.kerfume.reminder.domain.remind.{Remind, RemindService}
import me.kerfume.reminder.domain.consumer.{Consumer, ConsumerInMemory}
import me.kerfume.reminder.domain.seqid.{SeqID, SeqIDRepository, SeqIDRepositoryInMemory}

object Application {
  val remindRepository = new RemindRepositoryRpc
  val seqIDRepository = new IOWrapper.SeqIDRepositoryIOWrapper
  val consumer = new IOWrapper.ConsumerIOWrapper
  val remindService = new RemindService(
    remindRepository,
    seqIDRepository,
    consumer
  )

  val registController = new RegistController(remindService)
}

object IOWrapper {
  class SeqIDRepositoryIOWrapper extends SeqIDRepository[IO] {
    private val internal = new SeqIDRepositoryInMemory
    override def generate(): IO[SeqID] = IO { internal.generate()}
  }

  class ConsumerIOWrapper extends Consumer[IO] {
    private val internal = new ConsumerInMemory
    override def tell(remind: Remind): IO[Unit] = IO { internal.tell(remind) }
  }
}