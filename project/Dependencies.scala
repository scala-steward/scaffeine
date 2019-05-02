import sbt._

object Dependencies {
  val CaffeineVersion = "2.7.0"

  val Caffeine = "com.github.ben-manes.caffeine" % "caffeine" % CaffeineVersion
  val Java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"
  val Jsr305 = "com.google.code.findbugs" % "jsr305" % "3.0.2"
  val Scalactic = "org.scalactic" %% "scalactic" % "3.1.0-SNAP9"
  val Scalatest = "org.scalatest" %% "scalatest" % "3.1.0-SNAP9"
}
