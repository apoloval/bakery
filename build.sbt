lazy val commonSettings = Seq(
  organization    := "com.github.apoloval"
  version         := "0.1.0"
  scalaVersion    := "2.11.8"
)

lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(name := "bakery-core")
