package me.kerfume.twitter.oauth

import cats.effect.IO
import me.kerfume.random.RandomProvider
import me.kerfume.time.TimeProvider
import me.kerfume.twitter.oauth.OAuthClient.{Config, OAuthToken, TwitterProfile}
import sttp.model.{Method, Uri}
import Signature.ParamKey
import sttp.client.quick._

import scala.util.chaining._

class OAuthClient(
    config: Config,
    randomProvider: RandomProvider,
    timeProvider: TimeProvider
) {
  private val requestTokenEndPoint =
    uri"https://api.twitter.com/oauth/request_token"
  private val accessTokenEndPoint =
    uri"https://api.twitter.com/oauth/access_token"
  private def redirectEndPoint(token: String) =
    uri"https://api.twitter.com/oauth/authenticate?oauth_token=${token}"

  def redirectUrl(accessToken: OAuthToken): Uri = {
    redirectEndPoint(accessToken.token)
  }

  def getRequestToken(callbackUrl: Uri): IO[OAuthToken] = IO {
    val res = genOAuthRequest(
      requestMethod = Method.POST,
      uri = requestTokenEndPoint,
      paramsModF = (_ + ("oauth_callback" -> EncodableString
        .pure(callbackUrl.toString)
        .encode))
    ).send()

    decodeOAuthToken(res.body)
  }

  def getAccessToken(
      requestToken: OAuthToken,
      verifier: String
  ): IO[OAuthToken] = IO {
    val res = genOAuthRequest(
      requestMethod = Method.POST,
      uri = accessTokenEndPoint,
      oAuthToken = Some(requestToken),
      externalParam = Map("oauth_verifier" -> EncodableString.pure(verifier))
    ).body("oauth_verifier" -> verifier)
      .send()

    decodeOAuthToken(res.body)
  }

  def getProfile(
      accessToken: OAuthToken
  ): IO[TwitterProfile] = IO {
    val profileEndPoint =
      uri"https://api.twitter.com/1.1/account/verify_credentials.json"
    val res = genOAuthRequest(
      requestMethod = Method.GET,
      uri = profileEndPoint,
      oAuthToken = Some(accessToken)
    ).send()

    io.circe.parser.parse(res.body).flatMap(_.as[TwitterProfile]) match {
      case Right(tp) => tp
      case Left(e)   => throw e
    }
  }

  private def genOAuthRequest(
      requestMethod: Method,
      uri: Uri,
      oAuthToken: Option[OAuthToken] = None,
      externalParam: Map[ParamKey, EncodableString] = Map.empty,
      paramsModF: Map[ParamKey, EncodableString] => Map[
        ParamKey,
        EncodableString
      ] = identity
  ) = {
    val params =
      (genCommonParams(config.consumerKey) ++
        oAuthToken
          .map(ot => "oauth_token" -> EncodableString.pure(ot.token))
          .toMap).pipe(paramsModF)

    val signature = Signature.createSignature(
      requestMethod,
      uri,
      params ++ externalParam,
      Signature.Secret(config.consumerSecret),
      oAuthToken.map(_.secret)
    )
    val compleatParams = (params + ("oauth_signature" -> signature.encode))
      .map { case (k, v) => s"""${k}="${v.value}"""" }

    quickRequest
      .method(requestMethod, uri)
      .header("Authorization", s"OAuth ${compleatParams.mkString(", ")}")
  }

  private def genCommonParams(
      consumerKey: String
  ): Map[ParamKey, EncodableString] = {
    import EncodableString.pure
    val nonce = randomProvider.alphanumeric(10)
    val timestamp = timeProvider.now().toEpochSecond

    Map(
      "oauth_consumer_key" -> pure(consumerKey),
      "oauth_nonce" -> pure(nonce),
      "oauth_signature_method" -> pure("HMAC-SHA1"),
      "oauth_timestamp" -> pure(timestamp.toString),
      "oauth_version" -> pure("1.0")
    )
  }

  private def decodeOAuthToken(planeText: String): OAuthToken = {
    val resMap = planeText
      .split("&")
      .map { s =>
        val k :: v :: Nil = s.split("=").toList
        k -> v
      }
      .toMap

    OAuthToken(
      token = resMap("oauth_token"),
      secret = resMap("oauth_token_secret")
    )
  }
}

object OAuthClient {
  import io.circe.Decoder
  import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

  case class Config(
      consumerKey: String,
      consumerSecret: String
  )
  case class OAuthToken(
      token: String,
      secret: String
  )

  case class TwitterProfile(
      id: Long,
      screenName: String
  )
  object TwitterProfile {
    private implicit val config =
      io.circe.generic.extras.Configuration.default.withSnakeCaseMemberNames
    implicit val decoder: Decoder[TwitterProfile] = deriveConfiguredDecoder
  }
}
