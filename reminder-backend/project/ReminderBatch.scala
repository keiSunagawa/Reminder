import sbt._
import Keys._
import Dependencies._
import sbtdocker.DockerPlugin

object ReminderBatch {
  val `reminder-batch` = (project in file("./batch"))
    .enablePlugins(DockerPlugin)
    .settings(Base.baseSettings)
    .settings(
      libraryDependencies ++= typesafeConfig
    )
    .settings(Docker.dockerSettings)
}
