import sbt.Keys._
import sbt.{Def, _}

object TravisCredentials {

  def updateCredentials(): Def.Setting[Task[Seq[Credentials]]] =
    (for {
       username <- Option(System.getenv().get("SONATYPE_USERNAME"))
       password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
     } yield credentials += Credentials(
       "Sonatype Nexus Repository Manager",
       "oss.sonatype.org",
       username,
       password
     )).getOrElse(credentials ++= Seq())
}
