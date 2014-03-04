package edu.illinois.learn.io

import edu.illinois.learn.models._
import edu.illinois.learn.controllers._
import com.typesafe.scalalogging.slf4j.Logging

sealed trait Input 
case class JsonInput(classes: List[Class]) extends Input

sealed class Writable
case class StringInt(str: String, int: Int) extends Writable

sealed trait Output 
case class TSVOutput[T<: Writable](results: List[T]) extends Output 
// If nothing in returned
case object Empty extends Input with Output

trait InputLoader {

  def readInput(semester: String, aggregation: Aggregation): Option[Input]
  def applyFilter(aggregation: Aggregation, input: Input): Input
}

trait InputLoaderImp extends InputLoader with JsonLoader with Logging {

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
    
	    // no input was specified in the query
//	  k
  def applyFilter(aggregation: Aggregation, input: Input): Input = {
    input
  }
}

trait OutputWriter {
  
  def write(output: Output)
}

trait OutputWriterImp extends OutputWriter {
  def write(output: Output) = {}
}

