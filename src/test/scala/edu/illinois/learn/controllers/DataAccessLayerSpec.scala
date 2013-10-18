/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.models

// Specs2
import org.specs2.mutable._
import com.typesafe.scalalogging.slf4j.Logging

class DataAccessLayerSpec extends Specification {

  val dal = new DataAccessLayer 

  "query forums" should {
    "find a forum with id 1" in {
      dal.findForum(1).isDefined must beTrue
    }

    "the forum with id 1 should have course == 2" in {
      dal.findForum(1).get.course must beEqualTo(2)
    }

    "there should be 2135 news" in {
      dal.countForumType("news").length must beEqualTo(2135)
    }
    "there should be 8446 general" in {
      dal.countForumType("general").length must beEqualTo(8446)
    }
  }
}
