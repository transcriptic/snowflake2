import sbt._
import Keys._
import com.typesafe.sbt.SbtStartScript

object BuildSettings {
  val buildOrganization = "com.transcriptic"
  val buildVersion      = "1.0.0"
  val buildScalaVersion = "2.10.0"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

object Resolvers {
  val typesafe = "Typesafe" at "http://repo.typesafe.com/typesafe/releases/"
  val twitter = "Twitter" at "http://maven.twttr.com/"
}

object SnowflakeBuild extends Build {
  import Resolvers._
  import BuildSettings._

  lazy val root = Project(
    "snowflake",
    file("."),
    settings =
      buildSettings ++ Seq (libraryDependencies ++= (Seq(
        "com.twitter"          %% "util-zk"                % "6.3.6",
        "org.scalatra"         %% "scalatra"               % "2.2.1",
        "org.scalatra"         %% "scalatra-json"          % "2.2.1",
        "org.scalatra"         %% "scalatra-auth"          % "2.2.1",
        "org.json4s"           %% "json4s-native"          % "3.2.4",
        "org.mortbay.jetty"    %  "jetty"                  % "6.1.22" % "test",
        "org.eclipse.jetty"    %  "jetty-webapp"           % "8.1.8.v20121106"
      )), resolvers ++= Seq(twitter, typesafe)) ++ Defaults.defaultSettings ++ 
        SbtStartScript.startScriptForClassesSettings
  ) settings (
    fork in run := true
  )
}
