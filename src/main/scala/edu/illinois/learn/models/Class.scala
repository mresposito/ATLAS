package edu.illinois.learn.models

import scala.slick.driver.MySQLDriver.simple._

case class CRN(id: Long,
  courseId: Long,
  crn: Int,
  name: String,
  instructor: String)

object CRNs extends Table[CRN]("mdl_enrol_autoroster_section") {
  def id = column[Long]("id", O.PrimaryKey)
  def courseId = column[Long]("courseid")
  def crn = column[Int]("crn")
  def name = column[String]("sectionname")
  def instructor = column[String]("instructor")

  def * = id ~ courseId ~ crn ~ name ~ instructor <> (CRN, CRN.unapply _)
}

case class Class(dep: String,
  courseNumber: Int,
  title: String,
  crn: Int,
  classType: String,
  session: String,
  time: String,
  days: String,
  location: String,
  instructor: String,
  credits: String,
  genEd: Option[String]) {

  def classSpec = dep + "\t" + courseNumber
}
