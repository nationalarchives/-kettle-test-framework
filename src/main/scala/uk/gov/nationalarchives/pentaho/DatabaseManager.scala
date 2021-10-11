package uk.gov.nationalarchives.pentaho

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
    Using.Manager { _ =>
      val connection = DriverManager.getConnection(jdbcUrl)
      val statement: Statement = connection.createStatement
      statement.execute(sql)
    }

}
