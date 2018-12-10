inThisBuild(Def.settings(
  organization := "org.scalajs.tools",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
  )
))

val `scala-js-ts-importer` = project.in(file("."))
  .settings(
    description := "TypeScript importer for Scala.js",
    mainClass := Some("org.scalajs.tools.tsimporter.Main"),
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
      "com.github.scopt" %% "scopt" % "3.7.0",
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )

val samples = project
  .enablePlugins(ScalaJSPlugin)
