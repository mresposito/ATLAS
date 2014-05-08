package edu.illinois.learn.controllers

import edu.illinois.learn.models._
import edu.illinois.learn.io._
import edu.illinois.learn.utils.TSVUtil
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.LazyLogging

object ConfigReader {
  val conf = ConfigFactory.load
  def getString(str: String) = conf.getString(str)
  def jsonLocation: String = conf.getString("settings.configJson")
}

trait SeriesRunner extends LazyLogging {
  this: JsonLoader =>
    
  /**
   * Runs the analysis on every series that we have
   */
  def start = loadSeries map runSerial

  private def runSerial(serial: Serial) = {
    logger.info(s"Running series: ${serial.name}")
    run(serial)
  }
    
  private def run(serial: Serial) = serial.makeQueries.map { query =>
    new QueryManager(query)
  }
}

object ProgramRunner {

  object SeriesRunnerImpl extends SeriesRunner with JsonLoaderImp
  /**
   *  entry point of the program
   *  Runs all the series inside
   *  the configuration file
   */ 
  def main(args: Array[String]): Unit = {
    SeriesRunnerImpl.start
  }
}

class SemesterRunner(semester: String) extends TSVUtil with LazyLogging {

  def executeForumAnalysis(forums: List[ClassForumAnalysis]) = {
    def runCounted[A](fun: ClassForumAnalysis => Map[String, A],
      jobName: String): Unit = {}
    runCounted(_.enrollmentPerClass, "EnrollmentPerClass")
    runCounted(_.departmentPostCount, "EnrollmentPerDepartment")

    runCounted(_.postsPerClass, "PostsPerClass")
    runCounted(_.postsPerDepartment, "PostsPerDepartment")
    runCounted(_.deviationPerClass, "PostStdPerClass")
    // post counts
    runCounted(_.postsPerStudentPerClass, "PostsPerStudentPerClass")
    runCounted(_.postsPerStudentPerDepartment, "PostsPerStudentPerDepartment")
  }
}