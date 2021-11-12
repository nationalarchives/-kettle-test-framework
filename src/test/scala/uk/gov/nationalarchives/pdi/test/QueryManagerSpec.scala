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
      val result = QueryManager.executeQuery(sparqlQuery, exampleRdfParentDir, "example", ".ttl")
      result.success.value must be(2)
    }
    "return a Failure with an Exception" in {
      val sparqlQuery =
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?policy ?label WHERE { ?policy rdfs:label ?label . }"
      val result = QueryManager.executeQuery(sparqlQuery, Paths.get("."), "", "")
      result.failure.exception mustBe a[RiotException]
    }
  }

}
