/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.controllers

import com.typesafe.scalalogging.slf4j.Logging
import edu.illinois.learn.models.Class
import edu.illinois.learn.utils.TSVUtil
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source

object ClassLoader extends TSVUtil {

  implicit val formatters = DefaultFormats
  
  def loadClasses(input: String): List[Class] = for {
    line <- Source.fromFile(input).getLines.toList
  } yield parse(line).extract[Class]

  def loadJson(input: String) = loadClasses(input)
  
  def lectures(xs: List[Class]) = xs.filter{ cls =>
    cls.classType contains "Lecture" 
  }

  def genEds(xs: List[Class]) = xs.filter{ cls =>
    cls.genEd.isDefined
  }

  def online(xs: List[Class]) = {
    val onlineData = "data/lasOnlineClasses.tsv"
    val onlineList = readfromIO(onlineData)
    val onlineCrn = onlineList.map(el => el(3).trim.toInt)
    xs.filter{ cls =>
      onlineCrn contains cls.crn
    }
  }

  def loadAll(xs: List[Class]) = new ClassAnalysis("allClasses", xs)
  def loadOnline(xs: List[Class]) = new ClassAnalysis("online", online(xs))
  def loadLectures(xs: List[Class]) = new ClassAnalysis("lectures", lectures(xs))
}

class ClassAnalysis(val name: String, val classes: List[Class]) {

  def countSectionsPerDepartmens = classes.groupBy(_.dep)

  def countInstructorPerSession = classes.groupBy(_.instructor)

  def countSectionsPerClass = classes.groupBy(_.classSpec)

  def countClassesPerDepartment = classes.groupBy(_.dep).
    map{ case(k,v) => (k, v.groupBy(_.classSpec).size) }

  def countLocationPerSession = classes.groupBy(_.location)
}
