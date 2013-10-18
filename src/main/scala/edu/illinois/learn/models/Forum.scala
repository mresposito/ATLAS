package edu.illinois.learn.models

import scala.slick.driver.MySQLDriver.simple._

case class Forum(id: Long,
  course: Long,
  forumType: String,
  name: String)

object Forums extends Table[Forum]("mdl_forum") {
  def id = column[Long]("id", O.PrimaryKey)
  def course = column[Long]("course")
  def forumType = column[String]("type")
  def name = column[String]("intro")

  def * = id ~ course ~ forumType ~ name <> (Forum, Forum.unapply _)
}
