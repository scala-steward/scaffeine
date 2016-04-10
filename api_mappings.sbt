autoAPIMappings := true

val bootClasspath = System.getProperty("sun.boot.class.path").split(":").map(file(_))

apiMappings ++= {
  val cp  = (fullClasspath in Compile).value

  def findManagedDependency(organization: String, name: String): File =
    ( for {
      entry <- cp
      module <- entry.get(moduleID.key)
      if module.organization == organization
      if module.name.startsWith(name)
        jarFile = entry.data
    } yield jarFile
  ).head

  Map(
    bootClasspath.find(_.getPath.endsWith("rt.jar")).get -> url("http://docs.oracle.com/javase/8/docs/api/"),
    findManagedDependency("com.github.ben-manes.caffeine", "caffeine") ->
      url("http://www.javadoc.io/doc/com.github.ben-manes.caffeine/caffeine/2.2.3")
  )
}
