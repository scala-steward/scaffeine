import sbt._

object Dependencies {
  val CaffeineVersion = "2.5.2"

  val Caffeine = "com.github.ben-manes.caffeine" % "caffeine" % CaffeineVersion
  val Java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"
  val Jsr305 = "com.google.code.findbugs" % "jsr305" % "3.0.1"
  val Scalactic = "org.scalactic" %% "scalactic" % "3.0.1"
  val Scalatest = "org.scalatest" %% "scalatest" % "3.0.1"
}
