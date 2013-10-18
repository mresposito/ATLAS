/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.classAnalysis

// Specs2
import org.specs2.mutable._
import com.typesafe.scalalogging.slf4j.Logging

class ClassAnalysisSpec extends Specification {

  val aasZulu = "data/AASZULUClasses.json"

  "count methods" should {

    val cls = new AllClasses(aasZulu)
    val lcs = new Lectures(aasZulu)

    "count sessions per departent AAS 100 is 7" in {
      cls.countSectionsPerClass("AAS\t100") must equalTo(7)
    }
    "count sessions per departent AAS 201 is 6" in {
      cls.countSectionsPerClass("AAS\t201") must equalTo(6)
    }

    "count AAL is 29" in {
      cls.countSectionsPerDepartmens("AAS") must equalTo(29)
    }
    "count ZULU is 1" in {
      cls.countSectionsPerDepartmens("ZULU") must equalTo(1)
    }
    "count lectures AAL is 17" in {
      lcs.countSectionsPerDepartmens("AAS") must equalTo(17)
    }
    "count ZULU lectures is 1" in {
      lcs.countSectionsPerDepartmens("ZULU") must equalTo(1)
    }
  }

  "load online classes" should {
    "loading 10 classes" in {
      new Online(aasZulu).classes.length must equalTo(0)
    }

    "loading full list" in {
      new Online(aasZulu).classes.length must equalTo(63)
    }
  }
}
