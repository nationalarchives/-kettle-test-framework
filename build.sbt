import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import sbt.Keys.resolvers

val pentahoVersion = "9.1.0.0-SNAPSHOT"

ThisBuild / versionScheme := Some("semver-spec")

lazy val root = Project("kettle-test-framework", file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    organization := "uk.gov.nationalarchives.pdi",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.13.6",
    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/nationalarchives/kettle-test-framework")),
    startYear := Some(2021),
    description := "Kettle Test Framework",
    organizationName := "The National Archives",
    organizationHomepage := Some(url("http://nationalarchives.gov.uk")),
    scalacOptions += "-target:jvm-1.8",
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "PentahoNexus" at "https://nexus.pentaho.org/content/groups/omni"
    ),
    headerLicense := Some(HeaderLicense.MIT("2020", "The National Archives")),
    libraryDependencies ++= Seq(
      "pentaho-kettle"              % "kettle-core"           % pentahoVersion,
      "pentaho-kettle"              % "kettle-engine"         % pentahoVersion,
      "org.pentaho.di.plugins"      % "pdi-core-plugins-impl" % pentahoVersion,
      "org.apache.jena"             % "apache-jena-libs"      % "3.17.0",
      "org.slf4j"                   % "slf4j-simple"          % "1.7.32" % Test,
      "com.h2database"              % "h2"                    % "1.4.200" % Test,
      "uk.gov.nationalarchives.pdi" % "kettle-jena-plugins"   % "2.2.0" % Test,
      "pentaho-kettle"              % "kettle-engine"         % pentahoVersion % Test classifier "tests",
      "pentaho-kettle"              % "kettle-core"           % pentahoVersion % Test classifier "tests",
      "org.scalatest"               %% "scalatest"            % "3.2.10" % Test,
      "org.scalatestplus"           %% "mockito-3-4"          % "3.2.10.0" % Test,
      "commons-io"                  % "commons-io"            % "2.11.0" % Test
    )
  )

scalafmtOnCompile := true
