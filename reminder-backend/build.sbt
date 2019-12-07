ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.kerfume"

val catsVersion = "2.0.0"

lazy val core = (project in file("./core")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )
)

lazy val `reminder-server` = (project in file("./server"))
  .enablePlugins(DockerPlugin)
  .settings(
  libraryDependencies ++= Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.12.7",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.12.7",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.12.7",
    "io.circe" %% "circe-generic" % "v0.12.3",
    "io.circe" %% "circe-generic" % "v0.12.3",
    "io.circe" %% "circe-parser" % "v0.12.3",
    "org.http4s" %% "http4s-circe" % "0.21.0-M5",
    "com.lihaoyi" %% "scalatags" % "0.7.0",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  ),
  scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
  ),
  dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("openjdk:8-jre")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}
).dependsOn(core)

lazy val root = (project in file(".")).aggregate(core, `reminder-server`)
