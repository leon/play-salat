import sbt._
import sbt.Keys._

object ProjectBuild extends Build {

  lazy val buildVersion =  "1.0-SNAPSHOT"

  lazy val root = Project(id = "play-plugins-mongodb", base = file("."), settings = Project.defaultSettings).settings(
    organization := "se.radley",
    version := buildVersion,
    scalaVersion := "2.9.1",
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    libraryDependencies += "play" %% "play" % "[2.0,)",
    libraryDependencies += "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
  )
}
