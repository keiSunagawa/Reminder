package me.kerfume.reminder.server

import sttp.model.StatusCode

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

  val resolve: Endpoint[Int, ErrorMessage, String, Nothing] =
    endpoint.get
      .in("resolve" / path[Int]("id"))
      .out(stringBody)
      .errorOut {
        // エラー型がひとつに定まっている場合でもoneOfは必要
        oneOf(statusMapping(StatusCode.NotFound, stringBody))
      }

  val list: Endpoint[Unit, Unit, String, Nothing] =
    endpoint.get
      .in("list")
      .out(htmlBodyUtf8)
}
