name                         := "bakery"
organization in ThisBuild    := "com.github.apoloval"
version in ThisBuild         := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild    := "2.11.8"

libraryDependencies in ThisBuild ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.7"
)

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

lazy val bakery = (project in file("."))
lazy val example = (project in file("example")).dependsOn(bakery)
