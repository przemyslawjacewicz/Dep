import sbt.*
import sbt.Keys.*
import sbtassembly.AssemblyKeys.*
import sbtassembly.AssemblyPlugin.autoImport.{MergeStrategy, PathList, assemblyMergeStrategy, assemblyOption}

object Common {

  val projectScalaVersion = "2.13.15"
  val projectOrganization = "pl.epsilondeltalimit"

  val assemblyConf = Seq(
    assembly / test           := {},
    assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x: Any                        => MergeStrategy.first
    }
  )

  /**
   * Get common basic settings for a module
   *
   * @param projectVersion version to set
   * @return basic settings for a module with provided version
   */
  def settings(projectVersion: ProjectVersion) = Seq(
    organization            := projectOrganization,
    scalaVersion            := projectScalaVersion,
    ThisBuild / useCoursier := false, // Disabling coursier fixes the problem with java.lang.NoClassDefFoundError: scala/xml while
    // publishing child modules: https://github.com/sbt/sbt/issues/4995

    run / fork               := true,
    Test / parallelExecution := false,
    Test / fork              := false,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
    javaOptions ++= Seq("-Dlog4j.debug=true", "-Dlog4j.configuration=log4j.properties"),
    outputStrategy := Some(StdoutOutput),
    isSnapshot     := projectVersion.snapshot,
    version        := projectVersion.fullVersion,
    resolvers += DefaultMavenRepository,
    resolvers += Resolver.mavenLocal
  ) ++ {
    if (projectVersion.snapshot)
      Seq(
        assembly / test      := ((): Unit),
        publishConfiguration := publishConfiguration.value.withOverwrite(true)
      )
    else
      Seq()
  }

  /**
   * Library for code dependencies
   */
  object Library {

    /**
     * The following implicits will enable the scoping of seq of dependencies rather than single dependency
     *
     * @param sq dependencies
     */
    implicit class implicits(sq: Seq[ModuleID]) {
      def %(conf: Configuration): Seq[ModuleID] = sq.map(_ % conf)

      def exclude(org: String, name: String): Seq[ModuleID] =
        sq.map(_.exclude(org, name))

      def excludeAll(rules: ExclusionRule*): Seq[ModuleID] =
        sq.map(_.excludeAll(rules *))

    }

    // todo: update me
    lazy val logging: Seq[ModuleID] = Seq(
      "log4j" % "log4j" % "1.2.17"
    )

    lazy val scalaTest: Seq[ModuleID] = Seq(
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalatest" %% "scalatest" % "3.2.19"
    )

    lazy val scalaMock: Seq[ModuleID] = Seq(
      "org.scalamock" %% "scalamock" % "6.0.0"
    )

  }
}
