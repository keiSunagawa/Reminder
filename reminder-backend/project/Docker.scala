import sbt._
import Keys._
import sbtdocker.DockerPlugin.autoImport._
import sbtassembly.AssemblyPlugin.autoImport._

object Docker {
  val dockerSettings = Def.settings(
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
      ImageName(s"keisunagawa/${name.value}:test-impl-auth")
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".properties" =>
        MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".xml"   => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
      case "application.conf"                             => MergeStrategy.concat
      case "unwanted.txt"                                 => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
}
