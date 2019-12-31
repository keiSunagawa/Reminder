import sbt._
import Keys._
import sbtprotoc.ProtocPlugin.autoImport.PB
import Dependencies._

object Rpc {
  val rpc = (project in file("./rpc"))
    .settings(Base.baseSettings)
    .settings(
      PB.targets in Compile := Seq(
        scalapb.gen() -> (sourceManaged in Compile).value
      ),
      libraryDependencies ++= catsEffect ++ grpc
    )
}
