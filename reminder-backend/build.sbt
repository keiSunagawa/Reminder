ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.kerfume"

val catsVersion = "2.1.0"
val catsEffectVersion = "2.0.0"

val commonLibs = Seq(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

val monocleLib = Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % "2.0.0",
  "com.github.julien-truffaut"  %%  "monocle-generic" % "2.0.0",
  "com.github.julien-truffaut"  %%  "monocle-macro"   % "2.0.0",
  "com.github.julien-truffaut"  %%  "monocle-state"   % "2.0.0",
  "com.github.julien-truffaut"  %%  "monocle-refined" % "2.0.0",
  "com.github.julien-truffaut"  %%  "monocle-unsafe"  % "2.0.0",
  "com.github.julien-truffaut"  %%  "monocle-law"     % "2.0.0" % "test"
)

val baseOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  // "-Ypartial-unification", remove by scala 2.13
  "-Xfatal-warnings",
)

def baseSettings = Seq(
  scalafmtOnCompile := true,
  scalacOptions ++= baseOptions,
  libraryDependencies ++= (commonLibs ++ monocleLib)
)

lazy val core = (project in file("./core")).settings(baseSettings)

lazy val rpc = (project in file("./rpc")).settings(baseSettings).settings(
  PB.targets in Compile := Seq(
     scalapb.gen() -> (sourceManaged in Compile).value
  ),

  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
    "io.grpc" % "grpc-all" % scalapb.compiler.Version.grpcJavaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  )
)

lazy val infra = (project in file("./infra")).settings(baseSettings).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC5",
  )
).dependsOn(core, rpc)

def dockerSettings = Seq(
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
  ),
  assemblyMergeStrategy in assembly := {
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
    case "application.conf"                            => MergeStrategy.concat
    case "unwanted.txt"                                => MergeStrategy.discard
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)
lazy val `reminder-server` = (project in file("./server"))
  .enablePlugins(DockerPlugin)
  .settings(baseSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.0",
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.12.7",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.12.7",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.12.7",
      "io.circe" %% "circe-generic" % "v0.12.3",
      "io.circe" %% "circe-generic" % "v0.12.3",
      "io.circe" %% "circe-parser" % "v0.12.3",
      "org.http4s" %% "http4s-circe" % "0.21.0-M5",
      "com.lihaoyi" %% "scalatags" % "0.7.0"
    ),
    scalacOptions ++= baseOptions,
  ).settings(dockerSettings).dependsOn(core, infra)

lazy val `reminder-batch` = (project in file("./batch"))
  .enablePlugins(DockerPlugin)
  .settings(baseSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.0",
    )
  ).settings(dockerSettings).dependsOn(core, infra)

lazy val root = (project in file(".")).aggregate(
  core,
  infra,
  `reminder-server`,
  `reminder-batch`,
  rpc
)
