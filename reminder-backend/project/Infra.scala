import sbt._
import Keys._
import Dependencies._

object Infra {
  val infra = (project in file("./infra"))
    .settings(Base.baseSettings)
    .settings(
      libraryDependencies ++= catsEffect ++ sttp
    )
}
