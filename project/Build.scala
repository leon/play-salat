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
    libraryDependencies += "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",

    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>http://jsuereth.com/scala-arm</url>
      <licenses>
        <license>
          <name>Apache 2.0</name>
          <url>http://www.opensource.org/licenses/Apache-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:leon/play-mongodb.git</url>
        <connection>scm:git:git@github.com:leon/play-mongodb.git</connection>
      </scm>
      <developers>
        <developer>
          <id>leon</id>
          <name>Leon Radley</name>
          <url>http://leon.radley.se</url>
        </developer>
      </developers>
    ),
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  )
}
