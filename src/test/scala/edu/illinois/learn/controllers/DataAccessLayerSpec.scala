/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.models

// Specs2
import org.specs2.mutable._
import com.typesafe.scalalogging.slf4j.Logging

class ClassAnalysisSpec extends Specification {

  val dal = new DataAccessLayer 

  "query forums" should {
    "find a forum with id 1" in {
      dal.findForum(1).isDefined must beTrue
    }

    "the forum with id 1 should have course == 2" in {
      dal.findForum(1).get.course must beEqualTo(2)
    }
  }
}
