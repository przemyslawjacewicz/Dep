import Common.*
import Common.Library.implicits
import sbt.project

name    := "Dep"
version := "0.1"

updateSbtClassifiers / useCoursier := true

lazy val root = (project in file("."))
  .settings(Common.settings(ProjectVersion(0, 1)))
  .settings(Common.assemblyConf)
  .settings(
    libraryDependencies ++= Library.logging, // todo: fix me
    libraryDependencies ++= Library.scalaTest % Test,
    libraryDependencies ++= Library.scalaMock % Test
  )
  .settings(
    Test / packageBin / publishArtifact := true
  )
