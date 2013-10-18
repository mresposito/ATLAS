package edu.illinois.learn.models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class Forum(id: Long,
  course: Long)

object Forums extends Table[Forum]("mdl_forum") {
  def id = column[Long]("id", O.PrimaryKey)
  def course = column[Long]("course")

  def * = id ~ course <> (Forum, Forum.unapply _)
}

class DataAccessLayer {
  val connection = Database.forURL("jdbc:mysql://localhost/moodle", driver="com.mysql.jdbc.Driver", user="root") 

  def findForum(id: Long) = connection withSession {
    Query(Forums).filter(_.id === id).firstOption
  }
}
