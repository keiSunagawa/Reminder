package me.kerfume.reminder.batch

import cats.effect.IO
import me.kerfume.infra.impl.domain.consumer.ConsumerSlackBulk
import me.kerfume.infra.impl.domain.remind.RemindRepositoryRpc
import me.kerfume.infra.impl.domain.seqid.SeqIDRepositoryRpc
import me.kerfume.reminder.domain.consumer.{Consumer, ConsumerInMemory}
import me.kerfume.reminder.domain.remind.{Remind, RemindService}
import me.kerfume.reminder.domain.seqid.{
  SeqID,
  SeqIDRepository,
  SeqIDRepositoryInMemory
}

class Application(config: AppConfig) {
  val remindRepository = new RemindRepositoryRpc(config.rpcEndpoint)
  val seqIDRepository = new SeqIDRepositoryRpc(remindRepository)
  val consumer = new ConsumerSlackBulk(config.slackConsumerConfig)
  val remindService = new RemindService(
    remindRepository,
    seqIDRepository,
    consumer
  )
}

object IOWrapper {
  class SeqIDRepositoryIOWrapper extends SeqIDRepository[IO] {
    private val internal = new SeqIDRepositoryInMemory
    override def generate(): IO[SeqID] = IO { internal.generate() }
  }

  class ConsumerIOWrapper extends Consumer[IO] {
    private val internal = new ConsumerInMemory
    override def tell(remind: Remind): IO[Unit] = IO { internal.tell(remind) }
  }
}
