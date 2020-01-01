package me.kerfume.infra.impl.session

import java.time.OffsetDateTime

import TwitterOAuthSessions._
import cats.effect.IO
import me.kerfume.random.RandomProvider
import me.kerfume.time.TimeProvider
import me.kerfume.twitter.oauth.OAuthClient.OAuthToken

class TwitterOAuthSessions(
    timeProvider: TimeProvider,
    randomProvider: RandomProvider
) {

  private var sessions: Map[SessionKey, Session] = Map.empty
  private var preSessions: Map[String, OAuthToken] = Map.empty

  def getSession(sessionKey: String): IO[Option[Session]] = IO {
    sessions.get(sessionKey).flatMap { r =>
      if (r.registerBy.plusDays(1).isAfter(timeProvider.now())) {
        Some(r)
      } else {
        sessions = (sessions - sessionKey)
        None
      }
    }
  }
  def setSession(
      twitterAccountId: String,
      oAuthToken: OAuthToken
  ): IO[SessionKey] =
    genKey().map { key =>
      val session = Session(
        key,
        twitterAccountId,
        timeProvider.now(),
        oAuthToken
      )
      sessions = sessions + (session.sessionKey -> session)
      session.sessionKey
    }
  def setPreSession(preSession: OAuthToken): IO[Unit] = IO {
    preSessions = preSessions + (preSession.token -> preSession)
  }
  def getPreSession(tokenKey: String): IO[Option[OAuthToken]] = IO {
    preSessions.get(tokenKey)
  }

  private def genKey(): IO[SessionKey] = IO { randomProvider.alphanumeric(20) }
}

object TwitterOAuthSessions {
  type SessionKey = String
  case class Session(
      sessionKey: SessionKey,
      twitterAccountId: String,
      registerBy: OffsetDateTime,
      oAuthToken: OAuthToken
  )
}
