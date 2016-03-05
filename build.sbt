name := "scaffeine"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++=
  Seq(
    "com.github.ben-manes.caffeine" % "caffeine" % "2.2.2",
    "com.google.code.findbugs" % "jsr305" % "3.0.1" % "provided",
    "org.scala-lang.modules" %% "scala-java8-compat" % "0.7.0",
    "org.scalactic" %% "scalactic" % "2.2.6",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )

    