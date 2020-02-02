package me.kerfume.reminder.server

import me.kerfume.reminder.server.AppConfig.Env
import me.kerfume.rpc.RpcEndpoint

trait AppConfig {
  def env: Env
  def rpcEndpoint: RpcEndpoint
  def launchPort: Int
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
}

class ProdAppConfig extends TypeSafeAppConfig {
  val env: Env = Env.Prod
  val configFileEnv = "prod"
  val launchPort: Int = 8080
}

class LocalAppConfig extends TypeSafeAppConfig {
  val env: Env = Env.Local
  val configFileEnv = "local"
  val launchPort: Int = 9999
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
