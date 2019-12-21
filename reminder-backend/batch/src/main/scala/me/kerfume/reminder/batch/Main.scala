package me.kerfume.reminder.batch

import cats.effect.{ExitCode, IO, IOApp}
import cats.Monad.ops._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val application = setUp(args)
    args.last match {
      case "send-remind" =>
        new send_reminder.Executor(application).exec() *> IO(ExitCode.Success)
    }
  }

  def setUp(args: List[String]): Application = {
    val env =
      if (args.contains("-prod")) AppConfig.Env.Prod
      else AppConfig.Env.Local

    val config = AppConfig.getConfig(env)
    new Application(config)
  }
}
