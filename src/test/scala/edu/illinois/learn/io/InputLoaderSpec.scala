/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.io

import org.specs2.mutable._
import edu.illinois.learn.models._

class InputLoaderSpec extends Specification {
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
  val file = "AASZULUClasses.json"
  val agg = Aggregation(tag, Some(file))
  val invalidFilter = filter("mm")
  val classes = testLoader.loadClasses(s"data/${semester}/${file}")
  val json = JsonInput(classes)
  def filter(name: String) = agg.copy(filter = Some(name))

  "Input loader applyFilter" should {
    "handle corner cases correctly" in {
	    "empty filter must return input value" in {
		  	testLoader.applyFilter(agg, json) must beEqualTo(json)
	    }
	    "empty filter must return input value even if empty" in {
		  	testLoader.applyFilter(agg, Empty) must beEqualTo(Empty)
	    }
	    "valid filter must return empty if value is empty" in {
		  	testLoader.applyFilter(invalidFilter, Empty) must beEqualTo(Empty)
	    }
	    "invalid filter must return original input" in {
		  	testLoader.applyFilter(invalidFilter, json) must beEqualTo(json)
	    }
    }
    "have filters behave correctly" in {
      "lecture filter" in {
      	testLoader.applyFilter(filter("lectures"), json) must beEqualTo(JsonInput(testLoader.lectures(classes)))
      }
      "genEds filter" in {
      	testLoader.applyFilter(filter("genEds"), json) must beEqualTo(JsonInput(testLoader.genEds(classes)))
      }
      "online filter" in {
      	testLoader.applyFilter(filter("online"), json) must beEqualTo(JsonInput(testLoader.online(classes)))
      }
    }
  }
}