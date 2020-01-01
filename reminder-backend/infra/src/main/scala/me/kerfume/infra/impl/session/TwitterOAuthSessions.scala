package me.kerfume.infra.impl.session

import TwitterOAuthSessions._
import cats.effect.IO
import me.kerfume.twitter.oauth.OAuthClient.OAuthToken

class TwitterOAuthSessions {
  private var sessions: List[Session] = Nil
  private var preSessions: List[OAuthToken] = Nil

  def getSession(sessionKey: String): IO[Option[Session]] = IO {
    sessions.find(_.sessionKey == sessionKey)
  }
  def setSession(session: Session): IO[Unit] = IO {
    sessions = session :: sessions
  }
  def setPreSession(preSessionK: OAuthToken): IO[Unit] = IO {
    preSessions = preSessionK :: preSessions
  }

  private def genKey(): IO[String] = ???
}

object TwitterOAuthSessions {
  case class Session(
      sessionKey: String,
      twitterAccountId: String,
      oAuthToken: OAuthToken
  )
}
