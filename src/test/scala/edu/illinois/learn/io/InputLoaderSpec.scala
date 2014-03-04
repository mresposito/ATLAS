/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.io

import org.specs2.mutable._
import edu.illinois.learn.models._

class JsonLoaderSpec extends Specification {
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
