import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

import scalariform.formatter.preferences._

name := "scaffeine"

organization := "com.github.blemale"

licenses += "Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")

description := "Thin Scala wrapper for Caffeine."

startYear := Some(2016)

homepage := Some(url("https://github.com/blemale/scaffeine"))

scalaVersion := "2.11.7"

libraryDependencies ++=
  Seq(
    "com.github.ben-manes.caffeine" % "caffeine" % "2.2.6",
    "org.scala-lang.modules" %% "scala-java8-compat" % "0.7.0",
    "com.google.code.findbugs" % "jsr305" % "3.0.1" % "provided",
    "org.scalactic" %% "scalactic" % "2.2.6" % "test",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )

SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(DanglingCloseParenthesis, Force)

releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    ReleaseStep(action = Command.process("publishSigned", _)),
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges
)

useGpg := true
