import sbt._
import Keys._
import Dependencies._

object Twitter {
  val twitter = (project in file("./twitter"))
    .settings(Base.baseSettings)
    .settings(
      libraryDependencies ++= catsEffect ++ sttp ++ circe
    )
}
