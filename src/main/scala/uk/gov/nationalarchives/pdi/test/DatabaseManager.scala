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

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.sql.{ DriverManager, Statement }
import scala.util.{ Try, Using }

/** Used to populate an RDBMS database with test data to be accessed from within a Pentaho Kettle transformation
  * @param jdbcUrl
  *   the JDBC URL of the test database
  */
case class DatabaseManager(jdbcUrl: String) {

  /** Executes the given SQL script and returns true if successful
    * @param sql
    *   the SQL script to execute
    * @return
    */
  def executeSqlScript(sql: String): Try[Boolean] =
    Using.Manager { use =>
      val connection = use(DriverManager.getConnection(jdbcUrl))
      val statement = use(connection.createStatement)
      statement.execute(sql)
    }

  def executeSqlScriptFromFile(sqlFile: Path): Try[Boolean] =
    Using.Manager { use =>
      val connection = use(DriverManager.getConnection(jdbcUrl))
      val statement: Statement = use(connection.createStatement)
      val sql = new String(Files.readAllBytes(sqlFile), UTF_8)
      statement.execute(sql)
    }

}
