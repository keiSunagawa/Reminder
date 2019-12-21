package me.kerfume.reminder.batch

import cats.effect.{ExitCode, IO, IOApp}
import cats.Monad.ops._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val application = setUp(args.lift(1))
    args.head match {
      case "send-remind" =>
        new send_reminder.App(application.remindService).exec() *>
          application.consumer.commit() *>
          IO(ExitCode.Success)
    }
  }

  def setUp(envStr: Option[String]): Application = {
    val env =
      if (envStr.contains("-prod")) AppConfig.Env.Prod
      else AppConfig.Env.Local

    val config = AppConfig.getConfig(env)
    new Application(config)
  }
}
