package me.kerfume.reminder.domain.remind

import me.kerfume.reminder.domain.consumer.ConsumerInMemory
import me.kerfume.reminder.domain.seqid.SeqIDRepository
import org.scalatest.FreeSpec
import cats.Id
import me.kerfume.reminder.domain.seqid.SeqIDRepositoryInMemory
import java.time.LocalDate
import java.time.LocalDateTime
import me.kerfume.reminder.domain.seqid.SeqID
import org.scalatest.DiagrammedAssertions

class RemindServiceSpec extends FreeSpec with DiagrammedAssertions {
  val consumer = new ConsumerInMemory
  val repository = new RemindRepositoryInMemory
  val seqIDRepository = new SeqIDRepositoryInMemory

  val service = new RemindService[Id](repository, seqIDRepository, consumer)

  "all running" - {
    val expect = List(
      Remind.OfDate(
        SeqID(1),
        "need buy sweet cake.",
        None,
        LocalDate.of(2019, 12, 25)
      ),
      Remind.OfDateTime(
        SeqID(2),
        "go home",
        None,
        LocalDateTime.of(2019, 12, 25, 18, 30)
      )
    )
    "regist" - {
      "of Date" - {
        service.simpleReminderOfDate(
          "need buy sweet cake.",
          LocalDate.of(2019, 12, 25)
        )
      }
      "of DateTime" - {
        service.simpleReminderOfDateTime(
          "go home",
          LocalDateTime.of(2019, 12, 25, 18, 30)
        )
      }
    }
    "get list" in {
      assert(service.list().sortBy(_.seqID.num) === expect)
    }
    "remindMe" in {
      service.remindMe(LocalDateTime.of(2019, 12, 25, 18, 30))

      assert(consumer.mailBox.sortBy(_.seqID.num) === expect)
    }
  }
}
