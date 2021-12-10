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

import org.apache.jena.riot.RiotException
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.nio.file.Paths

class QueryManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "QueryManager.executeQuery" must {
    "return a Success with a count value" in {
      val exampleRdfFile = Paths.get(this.getClass.getClassLoader.getResource("example.ttl").toURI)
      val exampleRdfParentDir = exampleRdfFile.getParent
      val sparqlQuery =
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?policy ?label WHERE { ?policy rdfs:label ?label . }"
      val result = QueryManager.executeQuery(sparqlQuery, exampleRdfParentDir, List("example"), ".ttl")
      result.success.value must be(2)
    }
    "return a Failure with an Exception" in {
      val sparqlQuery =
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?policy ?label WHERE { ?policy rdfs:label ?label . }"
      val result = QueryManager.executeQuery(sparqlQuery, Paths.get("."), List(""), "")
      result.failure.exception mustBe a[RiotException]
    }
    "create and query a combined graph from two RDF files" in {
      val exampleRdfFile = Paths.get(this.getClass.getClassLoader.getResource("output-record-descriptions.ttl").toURI)
      val exampleRdfParentDir = exampleRdfFile.getParent
      val sparqlQuery =
        s"""PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
           |PREFIX dct: <http://purl.org/dc/terms/>
           |PREFIX odrl: <http://www.w3.org/ns/odrl/2/>
           |SELECT ?policy ?label WHERE {
           |?item dct:accessRights ?rights .
           |?rights odrl:hasPolicy ?policy .
           |?policy a odrl:Policy .
        }""".stripMargin
      val result = QueryManager
        .executeQuery(sparqlQuery, exampleRdfParentDir, List("output-record-descriptions", "policies"), ".ttl")
      result.success.value must be(1)
    }
    "create and query a combined graph from three RDF files" in {
      val exampleRdfFile = Paths.get(this.getClass.getClassLoader.getResource("output-record-descriptions.ttl").toURI)
      val exampleRdfParentDir = exampleRdfFile.getParent
      val sparqlQuery =
        s"""PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
           |PREFIX dct: <http://purl.org/dc/terms/>
           |PREFIX odrl: <http://www.w3.org/ns/odrl/2/>
           |PREFIX ver: <http://purl.org/linked-data/version#>
           |PREFIX cat: <http://cat.nationalarchives.gov.uk/>
           |SELECT ?policy WHERE {
           |?concept dct:type cat:record-concept ;
           |         ver:currentVersion ?description .
           |?description dct:accessRights ?rights .
           |?rights odrl:hasPolicy ?policy .
           |?policy a odrl:Policy .
        }""".stripMargin
      val result = QueryManager
        .executeQuery(sparqlQuery, exampleRdfParentDir, List("output-record-descriptions", "output-record-concepts", "policies"), ".ttl")
      result.success.value must be(1)
    }
  }

}
