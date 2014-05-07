package edu.illinois.learn.io

import edu.illinois.learn.models._
import edu.illinois.learn.utils.TSVUtil
import edu.illinois.learn.controllers._
import com.typesafe.scalalogging.slf4j.{ LazyLogging => Logging }
import java.io.PrintWriter
import java.io.File

sealed trait Input 
case class JsonInput(classes: List[Class]) extends Input

sealed trait Output 
case class TSVOutput[T <% Ordered[T]](results: List[(String, T)]) extends Output 
// If nothing in returned
case object Empty extends Input with Output

trait InputLoader {

  def readInput(semester: String, aggregation: Aggregation): Option[Input]
  def applyFilter(aggregation: Aggregation, input: Input): Input
}

trait InputLoaderImp extends InputLoader with JsonLoader with Logging with InputFilters {

  private val inputBasePath = ConfigReader.getString("settings.inputFolder")

  private def readFromIO(semester: String, input: String): Option[Input] = {
  	val fullPath = inputBasePath + s"${semester}/${input}"
  	if(fullPath contains ".json") {
  	  try {
  	  	Some(JsonInput(loadClasses(fullPath)))
  	  } catch {
  	    case e: Exception => {
  	      logger.error(s"could not load JSON input from file ${fullPath}")
  	      None
  	    }
  	  }
  	} else {
  	  logger.info(s"The input type ${input} you have specified is not valid.")
  	  // no other type of input is declared so far
  	  None
  	}
  }

  def readInput(semester: String, aggregation: Aggregation): Option[Input] = {
    // read from IO and apply filter
    def processInput(input: String): Option[Input] = for {
	    rawInput <- readFromIO(semester, input)
	  } yield applyFilter(aggregation, rawInput)
    // if not defined, return empty 
    aggregation.input.map {
    	processInput
    }.getOrElse {
	    Some(Empty)
    }
  }
    
  def applyFilter(aggregation: Aggregation, input: Input): Input = aggregation.filter.map { filter =>
    input match {
      case JsonInput(e) => JsonInput(filterClasses(filter, e))
      case Empty => Empty
      case _ => {
      	logger.error(s"Input type does not have any filters. Create new ones.")
        input
      }
    }
  }.getOrElse {
    input
  }
  
  def filterClasses(filterName: String, input: List[Class]): List[Class] = filterName match {
    case "lectures" => lectures(input)
    case "genEds" => genEds(input)
    case "online" => online(input)
    case _ => {
      logger.error(s"Filter with name: ${filterName} is not valid. Please select a valid filter")
      input
    }
  }
}

trait InputFilters extends TSVUtil {
  /**
   * Read a list of online classes
   * in TSV format, then only takes the
   * online classes from the given input
   */
  def online(xs: List[Class]) = {
  	val onlineData = ConfigReader.getString("settings.onlineClasses")
    val onlineList = readfromIO(onlineData)
    val onlineCrn = onlineList.map(el => el(3).trim.toInt)
    xs.filter{ cls =>
      onlineCrn contains cls.crn
    }
  }
  /**
   * Looks at the lectures from a given input
   */
  def lectures(xs: List[Class]) = xs.filter { cls =>
    cls.classType contains "Lecture" 
  }
  /**
   * filters the gen eds,
   */
  def genEds(xs: List[Class]) = xs.filter { cls =>
    cls.genEd.isDefined
  }
}

trait OutputWriter {
  
  def write(output: Output, folder: String, file: String)
}

trait OutputWriterImp extends OutputWriter {
  
  private val outputPath = ConfigReader.getString("settings.outputFolder")
  
  private def printOutput(path: String, content: String) = {
    val p = new PrintWriter(path, "UTF-8")
    p print content
    p.close()
  }
  
  private def checkPath(folder: String) = {
    val path = new File(folder)
    if(! path.exists()) {
      path.mkdir()
    }
  }
  
  def write(output: Output, folder: String, file: String) = {
    val writePath = outputPath + folder 
    checkPath(writePath)
    prepareWrite(output, writePath + "/" + file + ".tsv")
  }
    
  private def prepareWrite(output: Output, path: String) = output match {
    case TSVOutput(list) => {
      val result = list.map { case (k,v) => s"${k}\t${v}" } mkString("\n")
      printOutput(path, result)
    }
    case Empty => {}
  }
}