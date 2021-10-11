package uk.gov.nationalarchives.pentaho.helpers

import java.sql.ResultSet

case class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
  def hasNext: Boolean = rs.next()
  def next(): ResultSet = rs
}
