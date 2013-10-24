/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.controllers

// Specs2
import org.specs2.mutable._
import com.typesafe.scalalogging.slf4j.Logging

class ClassAnalysisSpec extends Specification {

  val aasZulu = "data/AASZULUClasses.json"
  val classes = ClassLoader.loadJson(aasZulu)

  "count methods" should {

    val cls = ClassLoader.loadAll(classes)
    val lcs = ClassLoader.loadLectures(classes)

    "count sessions per departent AAS 100 is 7" in {
      cls.countSectionsPerClass("AAS\t100").length must equalTo(7)
    }
    "count sessions per departent AAS 201 is 6" in {
      cls.countSectionsPerClass("AAS\t201").length must equalTo(6)
    }

    "count AAL is 29" in {
      cls.countSectionsPerDepartmens("AAS").length must equalTo(29)
    }
    "count ZULU is 1" in {
      cls.countSectionsPerDepartmens("ZULU").length must equalTo(1)
    }
    "count lectures AAL is 17" in {
      lcs.countSectionsPerDepartmens("AAS").length must equalTo(17)
    }
    "count ZULU lectures is 1" in {
      lcs.countSectionsPerDepartmens("ZULU").length must equalTo(1)
    }
  }

  "load online classes" should {
    "loading 10 classes" in {
      ClassLoader.loadOnline(classes).classes.length must equalTo(0)
    }

    "loading full list" in {
      // TODO: fixme!
      ClassLoader.loadOnline(ClassLoader.loadJson("data/listOfClasses.json")).classes.length must equalTo(130)
    }
  }
}
