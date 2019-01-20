import Dependencies.CaffeineVersion

import scala.util.control.NonFatal
import scala.util.matching.Regex.Match

autoAPIMappings := true

apiMappings ++= {
  val cp = (fullClasspath in Compile).value
  val bootClasspath = System.getProperty("sun.boot.class.path").split(":").map(file(_))

  def findManagedDependency(organization: String, name: String): File =
    (for {
      entry <- cp
      module <- entry.get(moduleID.key)
      if module.organization == organization
      if module.name.startsWith(name)
      jarFile = entry.data
    } yield jarFile
      ).head

  Map(
    bootClasspath.find(_.getPath.endsWith("rt.jar")).get -> url("http://docs.oracle.com/javase/8/docs/api/"),
    findManagedDependency("com.github.ben-manes.caffeine", "caffeine") -> url(s"http://static.javadoc.io/com.github.ben-manes.caffeine/caffeine/$CaffeineVersion/")
  )
}

lazy val fixJavaLinksTask = taskKey[Unit](
  "Fix Java links in scaladoc"
)

val jdkApiLink = """\"(http://docs\.oracle\.com/javase/8/docs/api/index\.html)#([^"]*)\"""".r
val caffeineApiLink = ("""\"(http://static\.javadoc\.io/com\.github\.ben-manes\.caffeine/caffeine/""" + CaffeineVersion.replace(".", "\\.") + """/)index\.html#([^"]*)\"""").r

def hasJavadocLink(f: File): Boolean = {
  val content = IO.read(f)
  (jdkApiLink findFirstIn content).nonEmpty || (caffeineApiLink findFirstIn content).nonEmpty
}

val fixJdkLinks: Match => String = m => m.group(1) + "?" + m.group(2).replace(".", "/") + ".html"
val replaceJdkLinks: String => String = jdkApiLink.replaceAllIn(_, fixJdkLinks)

val fixCaffeineLinks: Match => String = m => m.group(1) + m.group(2).replace(".", "/") + ".html"
val replaceCaffeineLinks: String => String = caffeineApiLink.replaceAllIn(_, fixCaffeineLinks)

fixJavaLinksTask := {
  val log = streams.value.log
  log.info("Fixing Java links")
  val t = (target in(Compile, doc)).value
  (t ** "*.html").get.filter(hasJavadocLink).foreach { f =>
    try {
      log.info("Fixing " + f)
      val fixedContent = replaceCaffeineLinks.andThen(replaceJdkLinks).apply(IO.read(f))
      IO.write(f, fixedContent)
    } catch {
      case NonFatal(e) => log.error(s"Failed to replace links in $f")
    }
  }
}

fixJavaLinksTask := (fixJavaLinksTask triggeredBy (doc in Compile)).value
