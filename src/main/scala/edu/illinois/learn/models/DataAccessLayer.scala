package edu.illinois.learn.models

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.driver.MySQLDriver.simple.{ Query => SQuery }
import edu.illinois.learn.controllers.ConfigReader
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Database.threadLocalSession
import edu.illinois.learn.io.Input
import edu.illinois.learn.io.Output
import edu.illinois.learn.io.Empty
import edu.illinois.learn.io.TSVOutput
import edu.illinois.learn.io.JsonInput
import scala.slick.jdbc.StaticQuery0

class DAL (query: Query, input: Input = Empty) extends DBConnection {
  
  import OutputConverters._
  
  def upper(s: String) = s.head.toUpper + s.tail
  private val formattedSemester = query.semester.map{ s =>
    s.subSequence(0, 4) + " " + upper(s.subSequence(4, s.length()).toString)
  }.getOrElse("")

  /** methods to try reflexive stuff */
  def hello = "Hello "
  def hi(name: String) = "hi " + name

  def example: Output = input.asInstanceOf[Output]
  val classes = input match {
    case JsonInput(cls) => cls 
    case _ => List()
  }
  
  /**
   * QUERY METHODS
   */
  /**
   * Classes methods
   */
  def countSectionsPerDepartment: Output = classes.groupBy(_.dep)

  def countInstructorPerSection: Output = classes.groupBy(_.instructor)

  def countSectionsPerClass: Output = classes.groupBy(_.classSpec)

  def countClassesPerDepartment: Output = classes.groupBy(_.dep).
    map{ case(k,v) => (k, v.groupBy(_.classSpec).size) }

  def countLocationPerSection: Output = classes.groupBy(_.location)

  /**
   * Forum methods
   */
  def forumPerClass: Output = joinCourses(classes).groupBy {
    case (k,v) => k.classSpec
  }

  def forumPerDepartment: Output = joinCourses(classes).groupBy {
    case (k,v) => k.dep
  }
  
  def postsPerClass: Output = connection withSession {
    Q.queryNA[(String, Int)](s"""
  SELECT eas.`sectionname`, COUNT(fp.id) count
  FROM mdl_forum f, mdl_course c, `mdl_enrol_autoroster_section` eas,
    mdl_forum_discussions fd, `mdl_forum_posts` fp
  WHERE c.id = f.course AND eas.`crid` = c.id
    AND fd.`forum` = f.id AND fp.`discussion` = fd.`id`
    AND eas.sectionname LIKE '%${formattedSemester}%'

  GROUP BY f.course
  ORDER BY count DESC;""")
  }

  /**
   * Database queries
   */
  def countForumType: Output = AggregatedOutput {
    connection withSession {
      SQuery(Forums).list.groupBy(_.forumType)
    }
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

  def getCRNEnrollment(crn: Int): Option[Int] = connection withSession {
    val q = Q.queryNA[Int](s"""
    SELECT COUNT(DISTINCT ue.userid)
  	FROM mdl_user_enrolments ue, mdl_enrol e, mdl_course c, `mdl_enrol_autoroster_section` eas
  	WHERE	ue.enrolid = e.id
  		AND c.id = e.courseid
  		AND eas.`courseid` = e.courseid
  		AND eas.`crn` = ${crn}
  	GROUP BY c.id
    """)
    q.firstOption
  }
}

trait Statistics {

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
}

trait DBConnection {

  lazy val connection = {
    // TODO: Include password setting
    val url = ConfigReader.getString("database.url")
    val user = ConfigReader.getString("database.user")
    Database.forURL(url, driver="com.mysql.jdbc.Driver", user=user)
  }
}

object OutputConverters {
  
  implicit def AggregatedOutput[T](m: List[(String, List[T])]): Output = {
    val results = m.map {
      case (k, v) => (k, v.length)
    }.sortBy(_._2)
    TSVOutput(results)
  }
  implicit def ToOutput[T <% Ordered[T]](
    m: List[(String, T)]): Output = TSVOutput(m.toList.sortBy(_._2))

  implicit def mToList[V](m: Map[String, V]): List[(String, V)] = m.toList
  implicit def mToOut[T](m: Map[String, List[T]]): Output = mToList(m)
  implicit def mToOut[T <% Ordered[T]](m: Map[String, T]): Output = mToList(m)
  implicit def stQuery[T <% Ordered[T]](a: StaticQuery0[(String, T)]): Output = a.list
}
