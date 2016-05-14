import Dependencies._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

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
    Caffeine,
    Java8Compat,
    Jsr305 % "provided",
    Scalactic % "test",
    Scalatest % "test"
  )

SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(DanglingCloseParenthesis, Force)
