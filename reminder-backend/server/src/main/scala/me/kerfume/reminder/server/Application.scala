package me.kerfume.reminder.server
import cats.effect.IO
import me.kerfume.infra.impl.domain.remind.RemindRepositoryRpc
import me.kerfume.infra.impl.domain.seqid.SeqIDRepositoryRpc
import me.kerfume.reminder.server.controller.RegistController
import me.kerfume.reminder.domain.remind.{
  Remind,
  RemindRepository,
  RemindRepositoryInMemory,
  RemindService
}
import me.kerfume.reminder.domain.consumer.{Consumer, ConsumerInMemory}
import me.kerfume.reminder.domain.seqid.{
  SeqID,
  SeqIDRepository,
  SeqIDRepositoryInMemory
}
import me.kerfume.reminder.server.AppConfig.Env

class Application(config: AppConfig) {
  val remindRepository = config.env match {
    case Env.Prod => new RemindRepositoryRpc(config.rpcEndpoint)
    case _        => new IOWrapper.RemindRepositoryIOWrapper
  }
  val seqIDRepository = config.env match {
    case Env.Prod => new SeqIDRepositoryRpc(remindRepository)
    case _        => new IOWrapper.SeqIDRepositoryIOWrapper
  }

  val consumer = new IOWrapper.ConsumerIOWrapper
  val remindService = new RemindService(
    remindRepository,
    seqIDRepository,
    consumer
  )

  val registController = new RegistController(remindService)
}

object IOWrapper {
  class RemindRepositoryIOWrapper extends RemindRepository[IO] {
    private val internal = new RemindRepositoryInMemory

    override def findAll(): IO[List[Remind]] = IO { internal.findAll() }

    override def store(remind: Remind): IO[Unit] = IO { internal.store(remind) }
  }

  class SeqIDRepositoryIOWrapper extends SeqIDRepository[IO] {
    private val internal = new SeqIDRepositoryInMemory
    override def generate(): IO[SeqID] = IO { internal.generate() }
  }

  class ConsumerIOWrapper extends Consumer[IO] {
    private val internal = new ConsumerInMemory
    override def tell(remind: Remind): IO[Unit] = IO { internal.tell(remind) }
  }
}
