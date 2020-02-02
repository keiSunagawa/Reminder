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
      sessionOpt <- sessionKey.flatTraverse(sessions.getSession)
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

  def createSession(
      tokenKey: String,
      verifier: String
  ): IO[Either[String, String]] = {
    for {
      token <- sessions.getPreSession(tokenKey)
      res <- token match {
        case Some(t) =>
          for {
            accessToken <- twitterOAuthClient.getAccessToken(t, verifier)
            profile <- twitterOAuthClient.getProfile(accessToken)
            key <- sessions.setSession(profile.id.toString, accessToken)
          } yield Right(s"REMINDER_SESSION=$key")
        case None => IO { Left("token not found.") }
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
