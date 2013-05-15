import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-jpa-reloadable"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    "org.hibernate" % "hibernate-entitymanager" % "4.1.7.Final",
    "tyrex" % "tyrex" % "1.0.1",
    "com.jolbox" % "bonecp" % "0.7.1.RELEASE"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}
