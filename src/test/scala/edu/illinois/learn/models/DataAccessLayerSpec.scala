/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.models

// Specs2
import org.specs2.mutable._
import edu.illinois.learn.io.Empty
import edu.illinois.learn.io.TSVOutput

trait QuerySpec {
  val query = Query(Column("b"), None, None)
  val dal = new DAL(query)
}

class DALSpec extends Specification with QuerySpec {
  
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