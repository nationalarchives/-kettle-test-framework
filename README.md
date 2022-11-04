# Kettle Test Framework

[![Build Status](https://github.com/nationalarchives/kettle-test-framework/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/nationalarchives/kettle-test-framework/actions/workflows/ci.yml)
[![Scala 2.13](https://img.shields.io/badge/scala-2.13-red.svg)](http://scala-lang.org)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.gov.nationalarchives.pdi/kettle-test-framework_2.13/badge.svg)](https://search.maven.org/search?q=g:uk.gov.nationalarchives.pdi)

A framework for testing Pentaho Kettle Transformations.

## Building from Source Code

### Pre-requisites for building the project:
* [sbt](https://www.scala-sbt.org/) >= 1.4.3  
* [Scala](https://www.scala-lang.org/) >= 2.13.6
* [Java JDK](https://adoptopenjdk.net/) >= 1.8
* [Git](https://git-scm.com)

### Build steps:
1. Clone the Git repository:
```
git clone https://github.com/nationalarchives/kettle-test-framework.git
```
2. Compile and install to your local Ivy or Maven repository (or use the version from [Maven Central](https://search.maven.org/search?q=g:uk.gov.nationalarchives.pdi)):
```
sbt clean compile publishLocal
```
After this other builds on the same machine can depend on it:
```
libraryDependencies += "uk.gov.nationalarchives" %% "kettle-test-framework" % "0.4.0-SNAPSHOT"
```

## Usage

The test framework is designed to allow you to test Pentaho Kettle transformations without needing to have the Pentaho Data Integration tool or an RDBMS installed on the local machine. It achieves by running Pentaho and the H2 database in embedded mode during tests.

Testing generally involves three phases:

1. Populating the database with test data by writing a SQL script which can be executed via the `DatabaseManager`

2. Executing the transformation via the `WorkflowManager`, having specifying any parameters or plugins that are needed by the transformation.

3. Querying the resulting output to check for expected results via the `QueryManager`. Currently the only RDF output data and SPARQL queries are supported, but this may be extended to other formats in future. 

An example test is provided at `src/test/scala/ExampleWorkflowSpec.scala`

To ensure that your Kettle transformations are suitable for testing it is important that they have no dependencies on specific databases or file paths. Such elements should be parameterised in the transformations so that they can be specified at execution time by the test framework.  

## Publishing a Release to Maven Central

1. Run `sbt clean release`
2. Answer the questions
3. Login to https://oss.sonatype.org/ then Close, and Release the Staging Repository
