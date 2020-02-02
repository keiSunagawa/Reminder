package me.kerfume.reminder.server.controller

import cats.effect.IO
import me.kerfume.infra.impl.session.AuthenticationService
import me.kerfume.infra.impl.session.AuthenticationService.{
  NeedGetTwitterOAuthToken,
  SessionExists
}
import me.kerfume.reminder.server.ErrorInfo
import sttp.model.Uri

class AuthenticationController(
    service: AuthenticationService
) {
  def withCheckAuthentication[A](
      sessionKey: Option[String],
      origin: String
  )(f: => IO[Either[ErrorInfo, A]]): IO[Either[ErrorInfo, A]] = {
    for {
      ares <- service.twitterAuthenticationInit(
        sessionKey,
        Uri.parse(s"${origin}/auth").toOption.get
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
