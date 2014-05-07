package edu.illinois.learn.controllers

import org.specs2.mutable._
import com.typesafe.scalalogging.slf4j.Logging
import edu.illinois.learn.models.Aggregation
import edu.illinois.learn.models.Column
import edu.illinois.learn.io._
  
class QueryManagerSpec extends Specification {
  
  def query(method: String, input: Input): Option[Output] = {
    val qm = new QueryManager("fall2012", Aggregation("a"),
      Column("fake", method))
    qm.executeQuery(input)
  }

  "A Query manager " should {
    "example method should be some" in {
      query("example", Empty) should beSome(Empty)
    }
  }
}