package me.kerfume.infra.impl.domain.consumer

import cats.effect.IO
import me.kerfume.reminder.domain.consumer.Consumer
import me.kerfume.reminder.domain.remind.Remind
import sttp.client.quick._
import sttp.model.Uri
import cats.instances.list._
import cats.syntax.traverse._

class ConsumerSlackBulk(
    config: ConsumerSlack.Config
) extends Consumer[IO] {
  var buffer: List[Remind] = Nil
  override def tell(remind: Remind): IO[Unit] = IO {
    println("tell me!")
    buffer = remind :: buffer
    println(buffer)
  }

  def commit(): IO[Unit] = IO.suspend {
    for {
      url <- IO.fromEither(
        Uri.parse(config.url).left.map(new RuntimeException(_))
      )
      _ <- buffer.reverse.grouped(10).toList.traverse { xs =>
        IO {
          val text = createText(xs)

          // TODO use IO backend
          quickRequest
            .post(url)
            .body(s"""{ "text": "${text}\nfrom: Reminder system" }""")
            .send()
        }
      }
    } yield ()
  }

  private def createText(remiders: List[Remind]): String = {
    remiders
      .map { rm =>
        s"${rm.title} as remind. id: ${rm.seqID.num}"
      }
      .mkString("\n")
  }
}

object ConsumerSlack {
  case class Config(
      url: String
  )
}
