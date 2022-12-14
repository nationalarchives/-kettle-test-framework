/*
 * Copyright (c) 2021 The National Archives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.pentaho.di.trans.step.StepMetaInterface
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.nationalarchives.pdi.step.jena.model.JenaModelStepMeta
import uk.gov.nationalarchives.pdi.step.jena.serializer.JenaSerializerStepMeta
import uk.gov.nationalarchives.pdi.step.jena.shacl.JenaShaclStepMeta
import uk.gov.nationalarchives.pdi.test.helpers.IOHelper.delete
import uk.gov.nationalarchives.pdi.test.{ DatabaseManager, QueryManager, WorkflowManager }

import java.nio.file.{ Files, Paths }
import java.sql.DriverManager
import scala.util.{ Failure, Success, Using }

class ExampleWorkflowSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll
{

  // TODO(AR) this is a workaround for the driver being unregistered after the first run of the tests in sbt -- see https://stackoverflow.com/questions/14033629/playframework-2-0-scala-no-suitable-driver-found-in-tests
  DriverManager.registerDriver(new org.h2.Driver)

  private val exampleWorkflow = "example.ktr"
  private val outputDirectory = "output"
  private val resultFilenamePrefix = "example"
  private val resultFilenameSuffix = ".ttl"
  private val shaclDirectory = Paths.get("shacl")
  private val shaclDirectoryPath = shaclDirectory.toAbsolutePath.toString
  private val shaclFilename = "odrl-shacl.ttl"
  private val databaseDir = "./data-dir"
  private val databaseName = "test-db"
  private val databaseUrl = s"jdbc:h2:$databaseDir/$databaseName;database_to_upper=false;mode=MSSQLServer"
  private val databaseManager = DatabaseManager(databaseUrl)

  private val plugins: List[Class[_ <: StepMetaInterface]] = List(
    classOf[JenaModelStepMeta],
    classOf[JenaSerializerStepMeta],
    classOf[JenaShaclStepMeta]
  )

  "With the given data, the example workflow" must {
    "produce an RDF result file containing exactly two rdfs:label properties" in {
      val sql: String =
        """
          |create table tbl_closuretype(closure_type char(1), cltype_desc varchar(255));
          |insert into tbl_closuretype values ('A','Open on Transfer');
          |insert into tbl_closuretype values ('D','Retained Until');
          |create table tbl_item(closure_type char(1), closure_code int, last_date int, open_date datetime);
          |insert into tbl_item values('A', 0, 19911231, null);
          |insert into tbl_item values('D',1996, null, null)
       """.stripMargin
      insertData(sql)

      val params: Map[String, String] =
        Map(
          "OUTPUT_FILEPATH" -> outputDirectory,
          "OUTPUT_FILENAME" -> s"$resultFilenamePrefix$resultFilenameSuffix",
          "SHACL_DIRECTORY" -> shaclDirectoryPath,
          "SHACL_FILENAME"  -> shaclFilename
        )
      val workflowFile = Paths.get(this.getClass.getClassLoader.getResource(exampleWorkflow).toURI)
      val workflowParentDir = workflowFile.getParent
      Using(Files.newInputStream(workflowFile)) { workflowInputStream =>
        val _ =
          WorkflowManager.runTransformation(workflowInputStream, workflowParentDir, Some(params), None, Some(plugins))
      }
      val result = QueryManager.executeQuery(
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?policy ?label WHERE { ?policy rdfs:label ?label. }",
        Paths.get(outputDirectory),
        List(resultFilenamePrefix),
        resultFilenameSuffix
      )
      result mustBe Success(2)
    }
  }

  private def insertData(sql: String): Unit =
    databaseManager.executeSqlScript(sql) match {
      case Success(_)         => ()
      case Failure(exception) => fail(s"Unable to insert data due to ${exception.getMessage}")
    }

  override def beforeAll(): Unit = {
    clearDatabaseDataDir()
    deleteTestFiles()
  }

  override def afterAll(): Unit = {
    clearDatabaseDataDir()
    deleteTestFiles()
  }

  private def clearDatabaseDataDir(): Unit =
    delete(Paths.get(databaseDir))

  private def deleteTestFiles(): Unit =
    delete(Paths.get(outputDirectory))

}
