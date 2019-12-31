import Dependencies._
import sbt._
import Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._

object Base {
  private val baseLib = kerfumeUtil ++ cats ++ scalaTest
  private val baseScalaOptions = Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-language:higherKinds",
    "-language:postfixOps",
    "-feature",
    "-Xfatal-warnings"
  )
  val baseSettings = Def.settings(
    scalafmtOnCompile := true,
    scalacOptions ++= baseScalaOptions,
    libraryDependencies ++= baseLib
  )
}
