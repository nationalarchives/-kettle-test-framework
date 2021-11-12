package uk.gov.nationalarchives.pdi.test.helpers

import java.sql.ResultSet

case class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
  def hasNext: Boolean = rs.next()
  def next(): ResultSet = rs
}
