package edu.illinois.learn.models

import org.specs2.mutable._

class SerialSpec extends Specification {
  
  val col1 = Column("a", "b")
  val col2 = Column("c", "d")
  val ag1 = Aggregation("a")
  val columns = List(col1, col2)
  "Make Series" should {
    "Make emty if its all empty" in {
      Serial("myserial", List(), List(), List()).makeQueries should beEmpty
    }
    "Make with only columns" in {
      Serial("myserial", List(), List(), columns).makeQueries should beEqualTo(List(
        Query(col1, None, None), Query(col2, None, None)))
    }
    "Work with semesters" in {
      Serial("myserial", List("semester1", "semester2"),
          List(), List(col1)).makeQueries should beEqualTo(List(
        Query(col1, Some("semester1"), None), Query(col1, Some("semester2"), None)))
    }
    "Work with aggregation" in {
      Serial("myserial", List(),
          List(ag1), List(col1, col2)).makeQueries should beEqualTo(List(
        Query(col1, None, Some(ag1)), Query(col2, None, Some(ag1))))
    }
    "Work with simple example" in {
      Serial("myserial", List("spring"),
          List(ag1), List(col1)).makeQueries should beEqualTo(List(
        Query(col1, Some("spring"), Some(ag1))))
    }
    "No columns no list" in {
      Serial("myserial", List("spring"),
          List(ag1), List()).makeQueries should beEmpty
    }
  }
}