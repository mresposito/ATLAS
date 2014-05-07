/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.models

// Specs2
import org.specs2.mutable._
import edu.illinois.learn.io.Empty
import edu.illinois.learn.io.TSVOutput

class DALSpec extends Specification {
  
  val dal = new DAL("2012fall", Aggregation("a"), Column("b", "c"), Empty)
  "Count forum type" should {
    "count 8446 general" in {
      val TSVOutput(out) = dal.countForumType
      val (k, v) = out.find {
        case (k, v) => k == "general"
      }.get
      v must beEqualTo(8446)
    }
  }
}

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
    "forum info with id 1 has no post" in {
      dal.findPostsPerClass(1) must beEqualTo(0)
    }
  }
  
  "count enrollments" should {
  	"CRN 51193 must have 17 enrollments" in {
  	  dal.getCRNEnrollment(51193) must beSome(7)
  	}
  	"CRN 59947 must have 42 enrollments" in {
  	  dal.getCRNEnrollment(59947) must beSome(42)
  	}
  	"CRN 36296 must have 141 enrollments" in {
  	  dal.getCRNEnrollment(36296) must beSome(141)
  	}
  	"Fake CRN should be none" in {
  	  dal.getCRNEnrollment(23092) must beNone
  	}
  }
}