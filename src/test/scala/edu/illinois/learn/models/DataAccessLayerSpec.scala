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
      dal.findForum(1).get.courseId must beEqualTo(2)
    }

    "there should be 2135 news" in {
      dal.countForumType("news").length must beEqualTo(2135)
    }
    "there should be 8446 general" in {
      dal.countForumType("general").length must beEqualTo(8446)
    }
  }

  "match classes" should {
    "classID 249 -> crn 30560" in {
      dal.getCRN(249).get must beEqualTo(30560)
    }
    "classID 213 -> crn 32294" in {
      dal.getCRN(213).get must beEqualTo(32294)
    }
    "crn 30560 -> classID 249" in {
      dal.getCourseId(30560).get must beEqualTo(249)
    }

    "crn 32294 -> classID 213" in {
      dal.getCourseId(32294).get must beEqualTo(213)
    }

    "classID non existisng should be none" in {
      dal.getCRN(2042085).isDefined must beFalse
    }
  }

  "forum complex object" should {
    "find forum discussion with id 1 must have courseId" in {
      dal.findForumDiscussion(1).get.courseId must beEqualTo(4)
    }

    "find forum post with id 1 must have discussionId" in {
      dal.findForumPost(1).get.discussionId must beEqualTo(1)
    }

    "forum info with id 2 should be a forum" in {
      dal.findForumInfo(2).isDefined must beTrue
    }
    "forum info with id 2 has 1 discussion" in {
      dal.findForumInfo(2).get.discussions.length must beEqualTo(1)
      dal.findForumInfo(2).get.discussions.head.id must beEqualTo(5634)
    }
    "forum info with id 2 has 1 post" in {
      dal.findForumInfo(2).get.posts.length must beEqualTo(1)
    }
  }
}
