import sbt._

object Versions {
  val cats = "2.1.0"
  val catsEffect = "2.0.0"
  //val monocle = "2.0.0"
  val kerfumeUtil = "0.1.0"
  val circe = "0.12.3"
  val circeExtra = "0.12.2"
  val sttp = "2.0.0-RC5"
  val tapir = "0.12.7"
  val http4s = "0.21.0-M5"
  val scalaTag = "0.7.0"
  val typesafeConfig = "1.4.0"
  val scalaTest = "3.0.8"
  val logback = "1.2.3"
}
object Dependencies {
  val cats = Seq("org.typelevel" %% "cats-core" % Versions.cats)
  val catsEffect = Seq("org.typelevel" %% "cats-effect" % Versions.catsEffect)
//  val monocle = Seq(
//    "com.github.julien-truffaut"  %%  "monocle-core"    % Versions.monocle,
//    "com.github.julien-truffaut"  %%  "monocle-generic" % Versions.monocle,
//    "com.github.julien-truffaut"  %%  "monocle-macro"   % Versions.monocle,
//    "com.github.julien-truffaut"  %%  "monocle-state"   % Versions.monocle,
//    "com.github.julien-truffaut"  %%  "monocle-refined" % Versions.monocle,
//    "com.github.julien-truffaut"  %%  "monocle-unsafe"  % Versions.monocle,
//  )
//  val monocleTest = Seq(
//    "com.github.julien-truffaut"  %%  "monocle-law" % Versions.monocle % Test
//  )
  val kerfumeUtil = Seq(
    "me.kerfume" %% "kerfume-scala-util-core" % Versions.kerfumeUtil
  )
  val circe = Seq(
    "io.circe" %% "circe-core" % Versions.circe,
    "io.circe" %% "circe-generic" % Versions.circe,
    "io.circe" %% "circe-parser" % Versions.circe,
    "io.circe" %% "circe-generic-extras" % Versions.circeExtra
  )
  val grpc = Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
    "io.grpc" % "grpc-all" % scalapb.compiler.Version.grpcJavaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  )
  val sttp = Seq(
    "com.softwaremill.sttp.client" %% "core" % Versions.sttp
  )
  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Versions.tapir
  )
  val http4s = Seq(
    "org.http4s" %% "http4s-circe" % Versions.http4s
  )
  val scalaTag = Seq(
    "com.lihaoyi" %% "scalatags" % Versions.scalaTag
  )
  val typesafeConfig = Seq(
    "com.typesafe" % "config" % Versions.typesafeConfig
  )
  val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % Versions.scalaTest % Test
  )
  val logback = Seq(
    "ch.qos.logback" % "logback-classic" % Versions.logback
  )
}
