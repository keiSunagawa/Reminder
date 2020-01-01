package me.kerfume.reminder.server.controller

import cats.effect.IO
import me.kerfume.infra.impl.session.AuthenticationService
import me.kerfume.infra.impl.session.AuthenticationService.{
  NeedGetTwitterOAuthToken,
  SessionExists
}
import me.kerfume.reminder.server.ErrorInfo

class AuthenticationController(
    service: AuthenticationService
) {
  import sttp.model.Uri._

  def withCheckAuthentication[A](
      sessionKey: Option[String]
  )(f: => IO[Either[ErrorInfo, A]]): IO[Either[ErrorInfo, A]] = {
    for {
      ares <- service.twitterAuthenticationInit(
        sessionKey,
        uri"https://reminder.kerfume.me:30080/auth" // TODO input from application
      )
      res <- ares match {
        case SessionExists(_) => f
        case NeedGetTwitterOAuthToken(rd, _) =>
          IO { Left(ErrorInfo.Redirect(rd)) }
      }
    } yield res
  }

  def authentication(
      tokenKey: String,
      verifier: String
  ): IO[Either[ErrorInfo, String]] =
    service.createSession(tokenKey, verifier).map {
      _.left.map(ErrorInfo.BadRequest(_))
    }
}
