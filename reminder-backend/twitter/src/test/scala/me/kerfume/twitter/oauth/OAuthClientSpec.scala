package me.kerfume.twitter.oauth

import me.kerfume.random.RandomProviderDefault
import me.kerfume.time.TimeProviderDefault
import org.scalatest.freespec.AnyFreeSpec

class OAuthClientSpec extends AnyFreeSpec {
  val client = new OAuthClient(
    OAuthClient.Config(
      consumerKey = "",
      consumerSecret = ""
    ),
    RandomProviderDefault,
    TimeProviderDefault
  )
  val testSkip = true

  if (!testSkip) {
    "getAccessToken" - {
      //"can get request token" - {
      import sttp.client.quick._
      val requestToken = client
        .getRequestToken(uri"https://reminder.kerfume.me:30080/list")
        .unsafeRunSync()
      println(requestToken)

      val redirectUrl = client.redirectUrl(requestToken)
      println(redirectUrl)
      //}

      //"can get access token" - {
      print("please verifier: ")
      val verifier = scala.io.StdIn.readLine()
      println(verifier)
      import sttp.client.quick._
      val accessToken = client
        .getAccessToken(
          requestToken,
          verifier
        )
        .unsafeRunSync()
      //}

      "can get profile" - {
        import sttp.client.quick._
        val res = client
          .getProfile(accessToken)
          .unsafeRunSync()
        println(res)
      }
    }
  }
}
