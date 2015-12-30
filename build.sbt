lazy val commonSettings = Seq(
  organization := "de.mirkokoester",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val player = (project in file("player")).
  settings(commonSettings: _*).
  settings(
    name := "luna-player",
    libraryDependencies ++= Seq(
    )
  )


lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "luna",
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(player)