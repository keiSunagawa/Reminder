ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.kerfume"

resolvers in ThisBuild += "kerfume repo" at "https://keisunagawa.github.io/kerfume-scala-util/repo/"

lazy val core = Core.core
lazy val rpc = Rpc.rpc
lazy val twitter = Twitter.twitter.dependsOn(core)
lazy val infra = Infra.infra.dependsOn(core, rpc, twitter)
lazy val `reminder-server` =
  ReminderServer.`reminder-server`.dependsOn(core, infra)
lazy val `reminder-batch` =
  ReminderBatch.`reminder-batch`.dependsOn(core, infra)

lazy val root = (project in file(".")).aggregate(
  core,
  infra,
  `reminder-server`,
  `reminder-batch`,
  rpc,
  twitter
)
