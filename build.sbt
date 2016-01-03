lazy val commonSettings = Seq(
  organization := "de.mirkokoester",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "luna-core",
    resolvers += Resolver.bintrayRepo("ijabz", "maven"), //jaudiotagger repo
    libraryDependencies ++= Seq(
      "net.jthink"              % "jaudiotagger"        % "2.2.5"
    )
  )

lazy val player = (project in file("player")).
  settings(commonSettings: _*).
  settings(
    name := "luna-player",
    libraryDependencies ++= Seq(
    )
  ).
  dependsOn(core)

lazy val mediaLibrary = (project in file("media-library")).
  settings(commonSettings: _*).
  settings(
    name := "luna-media-library",
    libraryDependencies ++= Seq(
      "com.typesafe.akka"       %% "akka-actor"         % "2.4.1",
      "com.typesafe.slick"      %% "slick"              % "3.1.1",
      "ch.qos.logback"          %  "logback-classic"    % "1.1.3"
    )
  ).
  dependsOn(core)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "luna",
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(player)