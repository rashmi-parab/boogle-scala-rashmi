name := """BoogleScalaRashmiP"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.apache.pdfbox" % "pdfbox" % "1.8.2",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.13.0-play25"
)

