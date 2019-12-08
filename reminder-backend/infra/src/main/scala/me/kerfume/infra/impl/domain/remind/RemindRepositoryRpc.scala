package me.kerfume.infra.impl.domain.remind

import me.kerfume.reminder.domain.remind.RemindRepository
import cats.effect.IO
import me.kerfume.reminder.domain.remind.Remind
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import com.kerfume.remind.protos.ReminderService.RemindListServiceGrpc
import me.kerfume.reminder.domain.seqid.SeqID
import me.kerfume.rpc.{ClientSupport, GrpcDataType, RpcEndpoint}

class RemindRepositoryRpc(
    endpoint: RpcEndpoint
) extends RemindRepository[IO] {
  private val rpcStub = RemindListServiceGrpc.blockingStub(
    ClientSupport.getChannel(endpoint.host, endpoint.port)
  )
  private val fmt = DateTimeFormatter.ofPattern("uuuu-MM-dd")

  def store(remind: Remind): IO[Unit] = IO {
    val willStore = remind.asInstanceOf[Remind.OfDate]
    val data = com.kerfume.remind.protos.ReminderService.RemindOfDate(
      willStore.seqID.num,
      willStore.title,
      fmt.format(willStore.trigger)
    )

    rpcStub.add(data)
  }
  import cats.syntax.all._
  def findAll(): IO[List[Remind]] =
    IO {
      println(endpoint)
      rpcStub
        .list(GrpcDataType.empty)
        .reminds
        .map { rorg =>
          Remind.OfDate(
            SeqID(rorg.seqNum),
            rorg.title,
            None,
            LocalDate.parse(rorg.trigger, fmt)
          )
        }
        .toList
    }.onError {
      case e: Throwable =>
        e.printStackTrace()
        println(e)
        IO.unit
    }
  def findByTriggerIsTime(
      now: LocalDateTime
  ): IO[List[Remind with Remind.TriggerIsTime]] = {
    findAll().map { reminds =>
      reminds.collect {
        case r: Remind.OfDate if {
              val dt = r.trigger.atTime(0, 0)
              dt.isBefore(now) || dt.isEqual(now)
            } =>
          r
        case r: Remind.OfDateTime
            if r.trigger.isBefore(now) || r.trigger.isEqual(now) =>
          r
      }: List[Remind with Remind.TriggerIsTime]
    }
  }
}
