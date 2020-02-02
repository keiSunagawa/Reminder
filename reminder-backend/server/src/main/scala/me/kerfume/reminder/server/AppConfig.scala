package me.kerfume.reminder.server

import me.kerfume.reminder.server.AppConfig.Env
import me.kerfume.rpc.RpcEndpoint
import me.kerfume.twitter.oauth.OAuthClient

trait AppConfig {
  def env: Env
  def rpcEndpoint: RpcEndpoint
  def twitterKeys: OAuthClient.Config
  def launchPort: Int
  def origin: String
}

abstract class TypeSafeAppConfig extends AppConfig {
  import com.typesafe.config.ConfigFactory
  def configFileEnv: String
  private lazy val config = ConfigFactory.load(configFileEnv)

  lazy val rpcEndpoint =
    RpcEndpoint(
      config.getString("rpc.host"),
      config.getInt("rpc.port")
    )
  lazy val twitterKeys = OAuthClient.Config(
    config.getString("twitter.consumerKey"),
    config.getString("twitter.consumerSecret")
  )
}

class ProdAppConfig extends TypeSafeAppConfig {
  val env = Env.Prod
  val configFileEnv = "prod"
  val launchPort = 8080
  val origin = "https://reminder.kerfume.me:30080"
}

class LocalAppConfig extends TypeSafeAppConfig {
  val env: Env = Env.Local
  val configFileEnv = "local"
  val launchPort = 9999
  val origin = "http://localhost:9999"
}

object AppConfig {
  sealed trait Env
  object Env {
    case object Prod extends Env
    case object Local extends Env
    case object Test extends Env
  }

  def getConfig(env: Env): AppConfig = env match {
    case Env.Prod  => new ProdAppConfig
    case Env.Local => new LocalAppConfig
    case Env.Test  => ???
  }
}
