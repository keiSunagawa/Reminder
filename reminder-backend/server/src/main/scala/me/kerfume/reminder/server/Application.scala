package me.kerfume.reminder.server
import me.kerfume.reminder.server.controller.RegistController
import me.kerfume.reminder.domain.remind.RemindRepositoryInMemory
import me.kerfume.reminder.domain.consumer.ConsumerInMemory
import me.kerfume.reminder.domain.seqid.SeqIDRepositoryInMemory
import me.kerfume.reminder.domain.remind.RemindService

object Application {
  val remindRepository = new RemindRepositoryInMemory
  val seqIDRepository = new SeqIDRepositoryInMemory
  val consumer = new ConsumerInMemory
  val remindService = new RemindService(
    remindRepository,
    seqIDRepository,
    consumer
  )

  val registController = new RegistController(remindService)
}
