import Dependencies._

name := "scaffeine"

organization := "com.github.blemale"

licenses += "Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")

description := "Thin Scala wrapper for Caffeine."

startYear := Some(2016)

homepage := Some(url("https://github.com/blemale/scaffeine"))

scalaVersion := "2.11.8"

libraryDependencies ++=
  Seq(
    Caffeine,
    Java8Compat,
    Jsr305 % "provided",
    Scalactic % "test",
    Scalatest % "test"
  )
