package me.kerfume.reminder.server

import cats.effect.IO
import java.time.LocalDate

object EndPoints {
  import sttp.tapir._
  import sttp.tapir.json.circe._
  import io.circe.generic.auto._

  import controller.RegistController._

  type ErrorMessage = String

  val regist: Endpoint[RegistByDateParam, ErrorMessage, String, Nothing] =
    endpoint.post
      .in("regist" / "date")
      .in(jsonBody[RegistByDateParam])
      .out(stringBody)
      .errorOut(stringBody)

  val list: Endpoint[Unit, Unit, String, Nothing] =
    endpoint.get
      .in("list")
      .out(htmlBodyUtf8)
}
