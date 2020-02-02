package me.kerfume.reminder.server
import cats.effect.IO
import me.kerfume.infra.impl.domain.remind.RemindRepositoryRpc
import me.kerfume.infra.impl.domain.seqid.SeqIDRepositoryRpc
import me.kerfume.infra.impl.session.TwitterOAuthSessions.SessionKey
import me.kerfume.infra.impl.session.{
  AuthenticationService,
  TwitterOAuthSessions
}
import me.kerfume.random.RandomProviderDefault
import me.kerfume.reminder.server.controller.{
  AuthenticationController,
  RegistController
}
import me.kerfume.reminder.domain.remind.{Remind, RemindService}
import me.kerfume.reminder.server.controller.RegistController
import me.kerfume.reminder.domain.remind.{
  Remind,
  RemindRepository,
  RemindRepositoryInMemory,
  RemindService
}
import me.kerfume.reminder.domain.consumer.{Consumer, ConsumerInMemory}
import me.kerfume.reminder.domain.seqid.{
  SeqID,
  SeqIDRepository,
  SeqIDRepositoryInMemory
}
import me.kerfume.time.TimeProviderDefault
import me.kerfume.twitter.oauth.OAuthClient
import me.kerfume.reminder.server.AppConfig.Env
import sttp.model.Uri

class Application(config: AppConfig) {
  val remindRepository = config.env match {
    case Env.Prod => new RemindRepositoryRpc(config.rpcEndpoint)
    case _        => new IOWrapper.RemindRepositoryIOWrapper
  }
  val seqIDRepository = config.env match {
    case Env.Prod => new SeqIDRepositoryRpc(remindRepository)
    case _        => new IOWrapper.SeqIDRepositoryIOWrapper
  }

  val consumer = new IOWrapper.ConsumerIOWrapper
  val remindService = new RemindService(
    remindRepository,
    seqIDRepository,
    consumer
  )

  val registController = new RegistController(remindService)

  val sessions =
    new TwitterOAuthSessions(TimeProviderDefault, RandomProviderDefault)

  val oAuthClient = config.env match {
    case Env.Prod =>
      new OAuthClient(
        config.twitterKeys,
        RandomProviderDefault,
        TimeProviderDefault
      )
    case _ => new IOWrapper.DummyOAuthClient
  }

  val authenticationService = new AuthenticationService(
    sessions,
    oAuthClient
  )

  val authenticationController = new AuthenticationController(
    authenticationService
  )
}

object IOWrapper {
  class RemindRepositoryIOWrapper extends RemindRepository[IO] {
    private val internal = new RemindRepositoryInMemory

    override def findAll(): IO[List[Remind]] = IO { internal.findAll() }

    override def store(remind: Remind): IO[Unit] = IO { internal.store(remind) }
  }

  class SeqIDRepositoryIOWrapper extends SeqIDRepository[IO] {
    private val internal = new SeqIDRepositoryInMemory
    override def generate(): IO[SeqID] = IO { internal.generate() }
  }

  class ConsumerIOWrapper extends Consumer[IO] {
    private val internal = new ConsumerInMemory
    override def tell(remind: Remind): IO[Unit] = IO { internal.tell(remind) }
  }

  class DummyOAuthClient
      extends OAuthClient(
        OAuthClient.Config("", ""),
        RandomProviderDefault,
        TimeProviderDefault
      ) {
    import sttp.client.quick._

    override def getRequestToken(callbackUrl: Uri): IO[OAuthClient.OAuthToken] =
      IO {
        OAuthClient.OAuthToken(
          RandomProviderDefault.alphanumeric(10),
          ""
        )
      }
    override def redirectUrl(accessToken: OAuthClient.OAuthToken): Uri =
      uri"http://localhost:9999/auth?oauth_token=${accessToken.token}&oauth_verifier=x"

    override def getAccessToken(
        requestToken: OAuthClient.OAuthToken,
        verifier: String
    ): IO[OAuthClient.OAuthToken] = IO { requestToken }

    override def getProfile(
        accessToken: OAuthClient.OAuthToken
    ): IO[OAuthClient.TwitterProfile] = IO {
      OAuthClient.TwitterProfile(1, "kerfume")
    }
  }
}
