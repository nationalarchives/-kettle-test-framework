package uk.gov.nationalarchives.pentaho

import org.apache.jena.riot.RiotException
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.matchers.must.Matchers.a

class QueryManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "QueryManager.executeQuery" must {
    "return a Success with a count value" in {
      val sparqlQuery =
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?policy ?label WHERE { ?policy rdfs:label ?label . }"
      val result = QueryManager.executeQuery(sparqlQuery, "src/test/resources", "example", ".ttl")
      result.success.value must be(2)
    }
    "return a Failure with an Exception" in {
      val sparqlQuery =
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?policy ?label WHERE { ?policy rdfs:label ?label . }"
      val result = QueryManager.executeQuery(sparqlQuery, "", "", "")
      result.failure.exception mustBe a[RiotException]
    }
  }

}
