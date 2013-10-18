package edu.illinois.learn.models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

class DataAccessLayer {
  val connection = Database.forURL("jdbc:mysql://localhost/moodle", driver="com.mysql.jdbc.Driver", user="root") 

  def findForum(id: Long) = connection withSession {
    Query(Forums).filter(_.id === id).firstOption
  }

  def countForumType = connection withSession {
    Query(Forums).list.groupBy(_.forumType)
  }
}
