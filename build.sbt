organization in ThisBuild    := "com.github.apoloval"
version in ThisBuild         := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild    := "2.11.8"

lazy val core = (project in file("core"))
lazy val example = (project in file("example")).dependsOn(core)
