package uk.gov.nationalarchives.pentaho

import java.io.File
import java.sql.{ DriverManager, Statement }
import scala.io.Source
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

  def executeSqlScriptFromFile(sqlFile: File): Try[Boolean] =
    Using.Manager { use =>
      val sqlSource = use(Source.fromFile(sqlFile))
      val connection = use(DriverManager.getConnection(jdbcUrl))
      val statement: Statement = use(connection.createStatement)
      val sql = sqlSource.getLines().mkString
      statement.execute(sql)
    }

}
