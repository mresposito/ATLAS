/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.classAnalysis

import com.typesafe.scalalogging.slf4j.Logging
import java.io.PrintWriter

case class Class(dep: String,
  courseNumber: Int,
  title: String,
  crn: Int,
  classType: String,
  sessions: String,
  time: String,
  days: String,
  location: String,
  instructor: String,
  credits: String,
  genEd: Option[String]) {

  def classSpec = dep + "\t" + courseNumber
}

abstract class ClassAnalysis {
  val name: String
  val classes: List[Class]

  def count(a: Map[String, List[Class]]) = a.map(k => (k._1, k._2.length))

  def countSectionsPerDepartmens = count(classes.groupBy(_.dep))

  def countInstructorPerSession = count(classes.groupBy(_.instructor))

  def countSectionsPerClass = count(classes.groupBy(_.classSpec))

  def countLocationPerSession = count(classes.groupBy(_.location))

  def writeResults[A](output: String, results: Map[String, A]) = {
    val p = new PrintWriter(output, "UTF-8") 
    p.print {
      results.toList.sortBy(el => el._2.toString.toInt). // little hack
        map(el => el._1 + "\t" + el._2.toString).mkString("\n")
    }
    p.close()
  }
}

class AllClasses(input: String) extends ClassAnalysis with JsonClassReader {
  val name ="allClasses"
  val classes = loadClasses(input)
}

class Lectures(input: String) extends ClassAnalysis with JsonClassReader {
  val name = "lectures"
  val classes = loadClasses(input).filter{ cls =>
    cls.classType contains "Lecture" 
  }
}

class GenEds(input: String) extends ClassAnalysis with JsonClassReader {
  val name = "genEds"
  val classes = loadClasses(input).filter{ cls =>
    cls.genEd.isDefined
  }
}

class Online(input: String) extends ClassAnalysis with TSVUtil with JsonClassReader {
  val name = "online"
  val onlineData = "data/lasOnlineClasses.tsv"

  val onlineList = readfromIO(onlineData)
  val onlineCrn = onlineList.map(el => el(3).trim.toInt)

  val classes = loadClasses(input).filter{ cls =>
    onlineCrn contains cls.crn
  }
}
