package uk.gov.nationalarchives.pentaho

import org.scalatest.BeforeAndAfterAll
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.io.File
import java.sql.SQLException
import scala.reflect.io.Directory

class DatabaseManagerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

  private val databaseDir = "./data-dir2"
  private val databaseName = "test-db"
  private val databaseUrl = s"jdbc:h2:$databaseDir/$databaseName;database_to_upper=false;mode=MSSQLServer"

  "DatabaseManager.executeSqlScript" must {

    "return a Success of true or false when all OK" in {
      val sqlScript = "create table tbl_closuretype(closure_type char(1), cltype_desc varchar(255));"
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScript(sqlScript)
      result.success.value must be(false)
    }

    "return a Failure wrapping an exception when there is a problem" in {
      val sqlScript = "not a sql script"
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScript(sqlScript)
      result.failure.exception mustBe a[SQLException] // have message errorMessage
    }

  }

  override def beforeAll(): Unit =
    clearDatabaseDataDir()

  override def afterAll() {
    clearDatabaseDataDir()
  }

  private def clearDatabaseDataDir(): Unit =
    new Directory(new File(databaseDir)).deleteRecursively()

}
