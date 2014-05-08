package edu.illinois.learn.controllers

import org.specs2.mutable._
import edu.illinois.learn.models._
import edu.illinois.learn.io._
  
class QueryManagerSpec extends Specification {
  
  def query(method: String, input: Input): Option[Output] = {
    val q = Query(Column(method), Some("fall2012"), Some(Aggregation("a")))
    val qm = new QueryManager(q)
    qm.executeQuery(input)
  }

  "A Query manager " should {
    "example method should be some" in {
      query("example", Empty) should beSome(Empty)
    }
  }
}