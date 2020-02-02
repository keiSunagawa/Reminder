package me.kerfume.reminder.server

import me.kerfume.reminder.domain.remind.Remind
import sttp.model.StatusCode

object EndPoints {
  import sttp.tapir._
  import sttp.tapir.json.circe._
  import io.circe.generic.auto._

  import controller.RegistController._

  type ErrorMessage = String
  private val sessionCookie = "REMINDER_SESSION"

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
        // TODO ErrorInfoで実装
        oneOf(statusMapping(StatusCode.NotFound, stringBody))
      }

  val list: Endpoint[WithSessionKey[Unit], ErrorInfo, ListResponse, Nothing] =
    endpoint.get
      .in("list")
      .in(
        cookie[Option[String]](sessionCookie)
          .map(WithSessionKey(_, ()))(_.sessionKey)
      )
      .out(jsonBody[ListResponse])
      .errorOut(ErrorInfo.errorInfoOutput)

  def twitterAuth(
      origin: String
  ): Endpoint[(String, String), ErrorInfo, String, Nothing] =
    endpoint.get
      .in("auth")
      .in(query[String]("oauth_token"))
      .in(query[String]("oauth_verifier"))
      .out(
        oneOf(
          statusMapping(
            StatusCode.TemporaryRedirect,
            //stringBody
            header("Cache-Control", "no-cache") and
              header("Location", s"${origin}/list") and
              header[String]("Set-Cookie")
          )
        )
      )
      .errorOut(ErrorInfo.errorInfoOutput)

}
