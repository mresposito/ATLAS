package edu.illinois.learn.models

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.driver.MySQLDriver.simple.{ Query => SQuery }
import edu.illinois.learn.controllers.ConfigReader
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import edu.illinois.learn.io.Input
import edu.illinois.learn.io.Output
import edu.illinois.learn.io.Empty
import edu.illinois.learn.io.TSVOutput

case class Enrollment (
	semester: String,
	shortName: String,
	crn: String,
	enrollments: Int)

trait DBConnection {

  lazy val connection = {
    // TODO: Include password setting
    val url = ConfigReader.getString("database.url")
    val user = ConfigReader.getString("database.user")
    Database.forURL(url, driver="com.mysql.jdbc.Driver", user=user)
  }
}


class DAL (query: Query, input: Input = Empty) extends DBConnection {
  
  def hello = "Hello "
  def hi(name: String) = "hi " + name
  
  def AggregatedOutput[T](m: Map[String, List[T]]): Output = {
    val results = m.map {
      case (k, v) => (k, v.length)
    }.toList.sortBy(_._2)
    TSVOutput(results)
  }
  
  /**
   * Query methods
   */
  def example: Output = input.asInstanceOf[Output]

  def countForumType: Output = AggregatedOutput {
    connection withSession {
      SQuery(Forums).list.groupBy(_.forumType)
    }
  }
}

class DataAccessLayer extends DBConnection {

  def findForum(id: Long) = connection withSession {
    SQuery(Forums).filter(_.id === id).firstOption
  }

  def countForumType = connection withSession {
    SQuery(Forums).list.groupBy(_.forumType)
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
    SQuery(CRNs).filter(_.courseId === courseId).map(_.crn).firstOption
  }

  def getCourseId(crn: Int) = connection withSession {
    SQuery(CRNs).filter(_.crn === crn).map(_.courseId).firstOption
  }

  def findForumPost(id: Long) = connection withSession {
    SQuery(ForumPosts).filter(_.id === id).firstOption
  }

  def findForumDiscussion(id: Long) = connection withSession {
    SQuery(ForumDiscussions).filter(_.id === id).firstOption
  }
  
  implicit val getEnrollmentResult = GetResult(r => Enrollment(r.<<, r.<<, r.<<, r.<<))
  
  def getCourseEnrollments = connection withSession {
	    Q.queryNA[Enrollment]("""
	  """)
  }
  
  def getCRNEnrollment(crn: Int): Option[Int] = connection withSession {
    val q = Q.queryNA[Int](s"""
    SELECT count(DISTINCT ue.userid)
  	FROM mdl_user_enrolments ue, mdl_enrol e, mdl_course c, `mdl_enrol_autoroster_section` eas
  	WHERE	ue.enrolid = e.id
  		AND c.id = e.courseid
  		AND eas.`courseid` = e.courseid
  		AND eas.`crn` = ${crn}
  	GROUP BY c.id
    """)
    q.firstOption
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