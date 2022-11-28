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

package uk.gov.nationalarchives.pdi.test

import org.scalatest.BeforeAndAfterEach
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.nationalarchives.pdi.test.helpers.IOHelper.delete

import java.io.IOException
import java.nio.file.{ FileVisitResult, Files, Path, Paths, SimpleFileVisitor }
import java.nio.file.attribute.BasicFileAttributes
import java.sql.{ DriverManager, SQLException }

class DatabaseManagerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  // TODO(AR) this is a workaround for the driver being unregistered after the first run of the tests in sbt -- see https://stackoverflow.com/questions/14033629/playframework-2-0-scala-no-suitable-driver-found-in-tests
  DriverManager.registerDriver(new org.h2.Driver)

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
      val sqlScriptFile = Paths.get(this.getClass.getClassLoader.getResource("example.sql").toURI)
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScriptFromFile(sqlScriptFile)
      result.success.value must be(false)
    }

    "return a Failure wrapping an exception when there is a problem" in {
      val notASqlScriptFile = Paths.get(this.getClass.getClassLoader.getResource("example.ttl").toURI)
      val databaseManager = DatabaseManager(databaseUrl)
      val result = databaseManager.executeSqlScriptFromFile(notASqlScriptFile)
      result.failure.exception mustBe a[SQLException]
    }

  }

  override def beforeEach(): Unit =
    clearDatabaseDataDir()

  override def afterEach(): Unit =
    clearDatabaseDataDir()

  private def clearDatabaseDataDir(): Unit = {
    val dataDir = Paths.get(databaseDir)
    delete(dataDir)
  }
}
