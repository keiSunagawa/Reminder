package me.kerfume.infra.impl.domain.remind

import me.kerfume.reminder.domain.remind.RemindRepository
import cats.effect.IO
import me.kerfume.reminder.domain.remind.Remind
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.kerfume.remind.protos.ReminderService.RemindListServiceGrpc
import me.kerfume.reminder.domain.seqid.SeqID
import me.kerfume.rpc.{ClientSupport, GrpcDataType, RpcEndpoint}
import me.kerfume.reminder.domain.remind.RemindBase
import com.kerfume.remind.protos.ReminderService.{
  RemindStatus => RpcRemindStatus
}
import me.kerfume.reminder.domain.remind.Remind.RemindStatus

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
      fmt.format(willStore.trigger),
      toRpcModelStatus(remind.status)
    )

    rpcStub.add(data)
  }
  import cats.syntax.all._
  def findAll(): IO[List[Remind]] = {
    IO {
      println(endpoint)
      rpcStub
        .list(GrpcDataType.empty)
        .reminds
        .map { rorg =>
          Remind.OfDate(
            RemindBase(
              SeqID(rorg.seqNum),
              rorg.title,
              None,
              toDomeinModelStatus(rorg.status)
            ),
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
  }

  private def toDomeinModelStatus: RpcRemindStatus => RemindStatus = {
    case RpcRemindStatus.TODO       => RemindStatus.Todo
    case RpcRemindStatus.UNRESOLVED => RemindStatus.Unresolved
    case RpcRemindStatus.RESOLVED   => RemindStatus.Resolved
    case RpcRemindStatus.Unrecognized(x) =>
      throw new RuntimeException(s"invalid status: $x") // FIXME return Either[Error, ?]
  }
  private def toRpcModelStatus: RemindStatus => RpcRemindStatus = {
    case RemindStatus.Todo       => RpcRemindStatus.TODO
    case RemindStatus.Unresolved => RpcRemindStatus.UNRESOLVED
    case RemindStatus.Resolved   => RpcRemindStatus.RESOLVED
  }
}
