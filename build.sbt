import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

name := "scaffeine"

organization := "com.github.blemale"

licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")

description := "Thin Scala wrapper for Caffeine."

startYear := Some(2016)

homepage := Some(url("https://github.com/blemale/scaffeine"))

scalaVersion := "2.11.7"

libraryDependencies ++=
  Seq(
    "com.github.ben-manes.caffeine" % "caffeine" % "2.2.2",
    "org.scala-lang.modules" %% "scala-java8-compat" % "0.7.0",
    "com.google.code.findbugs" % "jsr305" % "3.0.1" % "provided",
    "org.scalactic" %% "scalactic" % "2.2.6" % "test",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )

SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(DanglingCloseParenthesis, Force)

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))
