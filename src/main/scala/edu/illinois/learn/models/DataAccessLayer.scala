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

  def joinCourses(courses: List[Class]): List[(Class, Forum)] = connection withSession { 
    val q = for {
      f <- Forums
      c <- CRNs if f.courseId === c.courseId
    } yield (c.crn, f)
    q.list.map { case (k,v) =>
      (courses.find( _.crn == k), v)
    }.filter(_._1.isDefined).map{ case (k,v) =>
      (k.get, v)
    }
  }

  def getCRN(courseId: Long) = connection withSession {
    Query(CRNs).filter(_.courseId === courseId).map(_.crn).firstOption
  }

  def getCourseId(crn: Int) = connection withSession {
    Query(CRNs).filter(_.crn === crn).map(_.courseId).firstOption
  }

  def findForumPost(id: Long) = connection withSession {
    Query(ForumPosts).filter(_.id === id).firstOption
  }

  def findForumDiscussion(id: Long) = connection withSession {
    Query(ForumDiscussions).filter(_.id === id).firstOption
  }

  def findPostsPerClass(courseId: Long): Int = connection withSession {
    (for {
      f <- Forums if f.courseId is courseId
      d <- ForumDiscussions if f.id is d.forumId
      p <- ForumPosts if p.discussionId is d.id 
    } yield p).list.length
  }

  def stddev(xs: List[Int]): Double = stddev(xs, mean(xs))
  def stddev(xs: List[Int], avg: Double): Double = xs match {
    case Nil => 0.0
    case ys => math.sqrt((0.0 /: ys) {
      (a,e) => a + math.pow(e - avg, 2.0)
    } / xs.size)
  }

  def mean(xs: List[Int]): Double = xs match {
    case Nil => 0.0
    case ys => ys.reduceLeft(_ + _) / ys.size.toDouble
  }

  def findDeviationPerClass(courseId: Long): Double = connection withSession {
    val q = (for { 
      f <- Forums if f.courseId is courseId
      d <- ForumDiscussions if f.id is d.forumId
      p <- ForumPosts if p.discussionId is d.id 
    } yield p)
    val postsPerStudent = q.list.groupBy(_.userId).map {
      case (k,v) => v.length
    }
    stddev(postsPerStudent.toList)
  }
}
