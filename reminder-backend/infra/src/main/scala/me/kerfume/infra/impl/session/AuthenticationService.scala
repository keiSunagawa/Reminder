package me.kerfume.infra.impl.session

import cats.effect.IO
import me.kerfume.infra.impl.session.TwitterOAuthSessions.Session
import me.kerfume.twitter.oauth.OAuthClient
import cats.instances.option._
import cats.syntax.traverse._
import me.kerfume.twitter.oauth.OAuthClient.OAuthToken
import sttp.model.Uri

class AuthenticationService(
    sessions: TwitterOAuthSessions,
    twitterOAuthClient: OAuthClient
) {
  import AuthenticationService._

  def twitterAuthenticationInit(
      sessionKey: Option[String],
      callBack: Uri
  ): IO[TwitterSessionInitResult] = {
    for {
      sessionOpt <- sessionKey.traverse(sessions.getSession).map { _.flatten }
      res <- sessionOpt match {
        case Some(s) => IO { SessionExists(s) }
        case None =>
          for {
            reqToken <- twitterOAuthClient.getRequestToken(callBack)
            redirectUrl = twitterOAuthClient.redirectUrl(reqToken)
            _ <- sessions.setPreSession(reqToken)
          } yield {
            NeedGetTwitterOAuthToken(redirectUrl, reqToken)
          }
      }
    } yield res
  }
}

object AuthenticationService {
  sealed trait TwitterSessionInitResult
  case class SessionExists(session: Session) extends TwitterSessionInitResult
  case class NeedGetTwitterOAuthToken(redirectFor: Uri, authToken: OAuthToken)
      extends TwitterSessionInitResult
}
