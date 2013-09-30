import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "sample"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "se.radley" %% "play-plugins-salat" % "1.4.0"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId",
      resolvers += Resolver.sonatypeRepo("snapshots")
    )

}
