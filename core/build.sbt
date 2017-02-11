name := "bakery-core"

libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % "2.11.7"
)

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
