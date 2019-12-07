ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val catsVersion = "2.0.0"

lazy val core = (project in file("./core")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )
)

lazy val server = (project in file("./server")).settings(
  libraryDependencies ++= Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.12.7",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.12.7",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.12.7",
    "io.circe" %% "circe-generic" % "v0.12.3",
    "io.circe" %% "circe-generic" % "v0.12.3",
    "io.circe" %% "circe-parser" % "v0.12.3",
        "org.http4s" %% "http4s-circe" % "0.21.0-M5",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )
).dependsOn(core)

lazy val root = (project in file(".")).aggregate(core)

// Uncomment the following for publishing to Sonatype.
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for more detail.

// ThisBuild / description := "Some descripiton about your project."
// ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
// ThisBuild / homepage    := Some(url("https://github.com/example/project"))
// ThisBuild / scmInfo := Some(
//   ScmInfo(
//     url("https://github.com/your-account/your-project"),
//     "scm:git@github.com:your-account/your-project.git"
//   )
// )
// ThisBuild / developers := List(
//   Developer(
//     id    = "Your identifier",
//     name  = "Your Name",
//     email = "your@email",
//     url   = url("http://your.url")
//   )
// )
// ThisBuild / pomIncludeRepository := { _ => false }
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// ThisBuild / publishMavenStyle := true