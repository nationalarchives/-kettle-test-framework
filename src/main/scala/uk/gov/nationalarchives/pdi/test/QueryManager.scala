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

import org.apache.jena.query._
import org.apache.jena.rdf.model.{ Model, ModelFactory }
import org.apache.jena.riot._
import uk.gov.nationalarchives.pdi.test.helpers.IOHelper.findFile

import java.nio.file.Path
import scala.annotation.tailrec
import scala.util.{ Failure, Success, Try, Using }

/** Used to execute SPARQL queries against RDF files output by Pentaho Kettle during transformation testing
  */
object QueryManager {

  /** Executes the given SPARQL query against the given RDF file and returns the size of the result if successful or an
    * error on failure
    * @param sparqlString
    *   the SPARQL query
    * @param rdfDirectory
    *   the RDF output directory
    * @param rdfFilenamePrefix
    *   the prefix of the RDF output filename
    * @param rdfFilenameSuffix
    *   the suffix of the RDF output filename
    * @return
    */
  def executeQuery(
    sparqlString: String,
    rdfDirectory: Path,
    rdfFilenamePrefix: String,
    rdfFilenameSuffix: String
  ): Try[Int] = executeQuery(sparqlString, rdfDirectory, List(rdfFilenamePrefix), rdfFilenameSuffix)

  /** Executes the given SPARQL query against the given RDF file and returns the size of the result if successful or an
    * error on failure
    * @param sparqlString
    *   the SPARQL query
    * @param rdfDirectory
    *   the RDF output directory
    * @param rdfFilenamePrefixes
    *   the prefixes of the RDF output filenames
    * @param rdfFilenameSuffix
    *   the suffix of the RDF output filename
    * @return
    */
  def executeQuery(
    sparqlString: String,
    rdfDirectory: Path,
    rdfFilenamePrefixes: List[String],
    rdfFilenameSuffix: String
  ): Try[Int] =
    buildQueryAndModel(sparqlString, getRdfFilenames(rdfDirectory, rdfFilenamePrefixes, rdfFilenameSuffix)) match {
      case Success((query, model)) =>
        Using(QueryExecutionFactory.create(query, model)) { queryExec =>
          val results = queryExec.execSelect
          val rs = ResultSetFactory.copyResults(results)
          rs.size()
        }
      case Failure(e) => Failure(e)
    }

  private def buildQueryAndModel(sparqlString: String, rdfFilenames: List[String]): Try[(Query, Model)] = {
    val query = Try(QueryFactory.create(sparqlString))
    combine(rdfFilenames).flatMap(m => query.flatMap(q => Try(q, m)))
  }

  private def getRdfFilenames(directory: Path, prefixes: List[String], suffix: String): List[String] =
    prefixes.map(prefix => getRdfFilename(directory, prefix, suffix))

  private def getRdfFilename(directory: Path, prefix: String, suffix: String): String =
    findFile(directory, prefix, suffix) match {
      case Success(Some(file)) => file.toAbsolutePath.toString
      case _                   => ""
    }

  private def createCombinedModel(rdfFilename: String, model: Try[Model]): Try[Model] =
    model.flatMap { m1 =>
      Try(RDFDataMgr.loadModel(rdfFilename)).flatMap { m2 =>
        Success(ModelFactory.createUnion(m1, m2))
      }
    }

  private def combine(files: List[String]): Try[Model] = {
    @tailrec
    def combineModels(files: List[String], model: Try[Model]): Try[Model] =
      files match {
        case Nil          => model
        case head :: tail => combineModels(tail, createCombinedModel(head, model))
      }
    combineModels(files, Try(ModelFactory.createDefaultModel()))
  }

}
