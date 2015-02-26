lazy val commonSettings = Seq(
  organization := "de.mirkokoester",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.5"
)

lazy val player = (project in file("player")).
  settings(commonSettings: _*).
  settings(
    name := "luna-player",
    libraryDependencies ++= Seq(
      "com.googlecode.soundlibs" % "mp3spi" % "1.9.5-1",
      "com.typesafe.akka" %% "akka-actor" % "2.3.9"
    )
  )


lazy val root = (project in file(".")).enablePlugins(PlayScala).
  settings(commonSettings: _*).
  settings(
    name := "luna",
    libraryDependencies ++= Seq(
      jdbc,
      anorm,
      cache,
      ws
    )
  )
  .dependsOn(player)
