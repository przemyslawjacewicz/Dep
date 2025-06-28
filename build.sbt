name         := "dep"
organization := "pl.epsilondeltalimit"
version      := "0.1"

scalaVersion := "2.13.15"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "org.scalactic"              %% "scalactic"     % "3.2.19",
  "org.scalatest"              %% "scalatest"     % "3.2.19" % Test
)