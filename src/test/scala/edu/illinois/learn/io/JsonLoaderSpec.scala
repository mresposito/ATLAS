/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.controllers
// Specs2
import edu.illinois.learn.models._
import edu.illinois.learn.io._
import org.specs2.mutable._

class JsonLoaderSpec extends Specification {
  
  object jsonImp extends JsonLoaderImp
  
  "Json Loader Impl" should {
  	"be read from JSON" in {
  		ConfigReader.jsonLocation.contains(".json") must beTrue
  	}
  	"read small json" in {
  	  jsonImp.readInput("""
  	   [{"name": "michele"}]
  	  """) must beEqualTo(List(Serial("michele", List(), List(), List())))
  	}

    "read json with simple aggregation" in {
  	  jsonImp.readInput("""
  	   [{"name": "michele",
         "aggregations": [{"tag": "me"}]}]
      """) must beEqualTo(List(Serial("michele", List(), List(Aggregation("me")), List())))
    }

    "read json with column" in {
  	  jsonImp.readInput("""
  	   [{"name": "michele",
         "columns": [{"tag": "me", "query": "hello"}]}]
      """) must beEqualTo(List(
        Serial("michele", List(), List(), List(Column("hello", Some("me"))))))
    }
    "read json with column and only query" in {
  	  jsonImp.readInput("""
  	   [{"name": "michele",
         "columns": [{"query": "hello"}]}]
      """) must beEqualTo(List(
        Serial("michele", List(), List(), List(Column("hello", None)))))
    }

  	"read full series" in {
  		jsonImp.loadSeries.length must beGreaterThan(0)
  	}
  }
  
  object testLoader extends InputLoaderImp with JsonLoaderImp

  val semester = "2012fall"
  val tag = "fakeTag"

  "Input Loader readInput" should {
    "return empty if not marked" in {
      testLoader.readInput(semester, Aggregation(tag)) must beSome(Empty)
    }

    "return none if invalid a json format" in {
      "return empty if not marked" in {
        testLoader.readInput(semester, Aggregation(tag, Some("tehno"))) must beNone
      }
      "return none for non existing file" in {
        testLoader.readInput(semester, Aggregation(tag, Some("tehno.json"))) must beNone
      }
      "return nonempty list for existing file" in {
        testLoader.readInput(semester, Aggregation(tag, Some("listOfClasses.json"))) must beSome
      }
    }
  }
}
