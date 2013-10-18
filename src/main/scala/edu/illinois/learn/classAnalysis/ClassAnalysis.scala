/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.classAnalysis

import com.typesafe.scalalogging.slf4j.Logging

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

  def countSectionsPerDepartmens = classes.groupBy(_.dep)

  def countInstructorPerSession = classes.groupBy(_.instructor)

  def countSectionsPerClass = classes.groupBy(_.classSpec)

  def countLocationPerSession = classes.groupBy(_.location)
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