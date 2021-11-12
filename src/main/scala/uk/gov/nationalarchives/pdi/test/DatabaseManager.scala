package uk.gov.nationalarchives.pdi.test

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.sql.{ DriverManager, Statement }
import scala.util.{ Try, Using }

/**
  * Used to populate an RDBMS database with test data to be accessed from within a Pentaho Kettle transformation
  * @param jdbcUrl the JDBC URL of the test database
  */
case class DatabaseManager(jdbcUrl: String) {

  /**
    * Executes the given SQL script and returns true if successful
    * @param sql the SQL script to execute
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
