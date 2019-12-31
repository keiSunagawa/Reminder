import sbt._

object Core {
  val core = (project in file("./core"))
    .settings(Base.baseSettings)
}
