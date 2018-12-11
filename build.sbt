import sbt.Keys.{ artifactPath, resolvers }

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
    resolvers += "jitpack" at "https://jitpack.io",
    description := "TypeScript importer for Scala.js",
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "blog.codeninja" % "scala-js-vue" % "2.4.2",
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.1",
      "com.github.scopt" %%% "scopt" % "3.7.0",
      "org.scalatest" %%% "scalatest" % "3.0.4" % Test
    ),
    scalaJSUseMainModuleInitializer := false,
    emitSourceMaps := false,
    Seq(fastOptJS, fullOptJS) map { packageJSKey =>
      artifactPath in (Compile, packageJSKey) := ((crossTarget in (Compile, packageJSKey)).value / (moduleName.value + "-opt.js"))
    }
  )
  .enablePlugins(ScalaJSPlugin)

