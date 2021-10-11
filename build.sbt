import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import sbt.Keys.resolvers

val pentahoVersion = "9.1.0.0-SNAPSHOT"

lazy val root = Project("kettle-test-framework", file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    organization := "uk.gov.nationalarchives",
    version := "0.1-SNAPSHOT",
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
      "org.slf4j"                   % "slf4j-simple"          % "1.7.32",
      "com.h2database"              % "h2"                    % "1.4.200",
      "com.h2database"              % "h2"                    % "1.4.200" % "test",
      "uk.gov.nationalarchives.pdi" % "kettle-jena-plugins"   % "2.2.0-SNAPSHOT",
      "pentaho-kettle"              % "kettle-engine"         % pentahoVersion % "test" classifier "tests",
      "pentaho-kettle"              % "kettle-core"           % pentahoVersion % "test" classifier "tests",
      "org.scalatest"               %% "scalatest"            % "3.2.10" % "test",
      "org.scalatestplus"           %% "mockito-3-4"          % "3.2.10.0" % "test",
      "commons-io"                  % "commons-io"            % "2.11.0" % "test"
    )
  )

scalafmtOnCompile := true
