import sbt._
import Keys._
import Dependencies._
import sbtdocker.DockerPlugin

object ReminderServer {
  val `reminder-server` = (project in file("./server"))
    .enablePlugins(DockerPlugin)
    .settings(Base.baseSettings)
    .settings(
      libraryDependencies ++= typesafeConfig ++ tapir ++ circe ++ scalaTag ++ logback
    )
    .settings(Docker.dockerSettings)
}
