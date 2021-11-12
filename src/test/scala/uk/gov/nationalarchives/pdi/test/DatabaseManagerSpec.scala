package uk.gov.nationalarchives.pdi.test

import org.scalatest.BeforeAndAfterEach
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.io.File
import java.nio.file.Paths
import java.sql.SQLException
import scala.reflect.io.Directory

class DatabaseManagerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  private val databaseDir = "./data-dir2"
  private val databaseName = "test-db"
  private val databaseUrl = s"jdbc:h2:$databaseDir/$databaseName;database_to_upper=false;mode=MSSQLServer"

  "DatabaseManager.executeSqlScript" must {

    "return a Success of true or false when all OK" in {
      val sqlScript = "CREATE TABLE tbl_closuretype(closure_type char(1), cltype_desc varchar(255));"
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScript(sqlScript)
      result.success.value must be(false)
    }

    "return a Failure wrapping an exception when there is a problem" in {
      val sqlScript = "not a sql script"
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScript(sqlScript)
      result.failure.exception mustBe a[SQLException]
    }

  }

  "DatabaseManager.executeSqlScriptFromFile" must {

    "return a Success of true or false when all OK" in {
      val sqlScriptFile = Paths.get(this.getClass.getClassLoader.getResource("example.sql").toURI).toFile
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScriptFromFile(sqlScriptFile)
      result.success.value must be(false)
    }

    "return a Failure wrapping an exception when there is a problem" in {
      val notASqlScriptFile = Paths.get(this.getClass.getClassLoader.getResource("example.ttl").toURI).toFile
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScriptFromFile(notASqlScriptFile)
      result.failure.exception mustBe a[SQLException]
    }

  }

  override def beforeEach(): Unit =
    clearDatabaseDataDir()

  override def afterEach() {
    clearDatabaseDataDir()
  }

  private def clearDatabaseDataDir(): Unit =
    new Directory(new File(databaseDir)).deleteRecursively()

}
