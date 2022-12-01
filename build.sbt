import sbt.Keys.resolvers
import ReleaseTransformations._

val pentahoVersion = "9.1.0.0-324"

ThisBuild / versionScheme := Some("semver-spec")

lazy val root = Project("kettle-test-framework", file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    organization := "uk.gov.nationalarchives.pdi",
    name := "kettle-test-framework",
    scalaVersion := "2.13.10",
    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/nationalarchives/kettle-test-framework")),
    startYear := Some(2021),
    description := "Kettle Test Framework",
    organizationName := "The National Archives",
    organizationHomepage := Some(url("http://nationalarchives.gov.uk")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/nationalarchives/kettle-test-framework"),
        "scm:git@github.com:nationalarchives/kettle-test-framework.git"
      )
    ),
    developers := List(
      Developer(
        id = "rwalpole",
        name = "Rob Walpole",
        email = "rob.walpole@devexe.co.uk",
        url = url("http://www.devexe.co.uk")
      )
    ),
    scalacOptions ++= Seq(
      "-release",
      "8",
      "-encoding",
      "utf-8",
      "-deprecation"
    ),
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "PentahoMaven" at "https://repo.orl.eng.hitachivantara.com/artifactory/pnt-mvn/",
      "Clojars" at "https://clojars.org/repo/"
    ),
    headerLicense := Some(HeaderLicense.MIT("2021", "The National Archives")),
    libraryDependencies ++= Seq(
      "pentaho-kettle"              % "kettle-core"           % pentahoVersion,
      "pentaho-kettle"              % "kettle-engine"         % pentahoVersion,
      "org.pentaho.di.plugins"      % "pdi-core-plugins-impl" % pentahoVersion,
      "org.apache.jena"             % "apache-jena-libs"      % "3.17.0",
      "org.slf4j"                   % "slf4j-simple"          % "1.7.32"       % Test,
      "com.h2database"              % "h2"                    % "1.4.200"      % Test,
      "uk.gov.nationalarchives.pdi" % "kettle-jena-plugins"   % "2.2.1"        % Test,
      "pentaho-kettle"              % "kettle-engine"         % pentahoVersion % Test classifier "tests",
      "pentaho-kettle"              % "kettle-core"           % pentahoVersion % Test classifier "tests",
      "org.scalatest"              %% "scalatest"             % "3.2.10"       % Test,
      "org.scalatestplus"          %% "mockito-3-4"           % "3.2.10.0"     % Test,
      "commons-io"                  % "commons-io"            % "2.11.0"       % Test
    ),
    dependencyCheckArchiveAnalyzerEnabled := Some(false),
    dependencyCheckAssemblyAnalyzerEnabled := Some(false),
    dependencyCheckAutoconfAnalyzerEnabled := Some(false),
    dependencyCheckCmakeAnalyzerEnabled := Some(false),
    dependencyCheckCocoapodsEnabled := Some(false),
    dependencyCheckNodeAnalyzerEnabled := Some(false),
    dependencyCheckNodeAuditAnalyzerEnabled := Some(false),
    dependencyCheckNexusAnalyzerEnabled := Some(false),
    dependencyCheckNuspecAnalyzerEnabled := Some(false),
    dependencyCheckNugetConfAnalyzerEnabled := Some(false),
    dependencyCheckPyDistributionAnalyzerEnabled := Some(false),
    dependencyCheckPyPackageAnalyzerEnabled := Some(false),
    dependencyCheckPyPackageAnalyzerEnabled := Some(false),
    dependencyCheckRubygemsAnalyzerEnabled := Some(false),
    dependencyCheckRetireJSAnalyzerEnabled := Some(false),
    dependencyCheckSwiftEnabled := Some(false),
    publishMavenStyle := true,
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots/")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2/")
    },
    releaseCrossBuild := false,
    releaseVersionBump := sbtrelease.Version.Bump.Minor,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommand("publishSigned"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
