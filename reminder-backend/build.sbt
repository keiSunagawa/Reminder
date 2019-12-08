ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.kerfume"

val catsVersion = "2.0.0"

val commonLibs = Seq(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

val baseOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)

lazy val core = (project in file("./core")).settings(
  scalacOptions ++= baseOptions,
  libraryDependencies ++= commonLibs
)

lazy val rpc = (project in file("./rpc")).settings(
  PB.targets in Compile := Seq(
     scalapb.gen() -> (sourceManaged in Compile).value
  ),

  scalacOptions ++= baseOptions,
  libraryDependencies ++= commonLibs ++ Seq(
    "org.typelevel" %% "cats-effect" % catsVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
    "io.grpc" % "grpc-all" % scalapb.compiler.Version.grpcJavaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  )
)

lazy val infra = (project in file("./infra")).settings(
  scalacOptions ++= baseOptions,
  libraryDependencies ++= commonLibs ++ Seq(
    "org.typelevel" %% "cats-effect" % catsVersion
  )
).dependsOn(core, rpc)

lazy val `reminder-server` = (project in file("./server"))
  .enablePlugins(DockerPlugin)
  .settings(
    libraryDependencies ++= commonLibs ++ Seq(
      "com.typesafe" % "config" % "1.4.0",
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
    scalacOptions ++= baseOptions,
    dockerfile in docker := {
      // The assembly task generates a fat JAR file
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"

      new Dockerfile {
        from("openjdk:8-jre")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-jar", artifactTargetPath, "-prod")
      }
    },
    imageNames in docker := Seq(
      // Sets the latest tag
      ImageName(s"keisunagawa/${name.value}:latest"),
    )
  ).dependsOn(core, infra)

lazy val root = (project in file(".")).aggregate(
  core,
  infra,
  `reminder-server`,
  rpc
)
