package edu.illinois.learn.io

import edu.illinois.learn.models._
import edu.illinois.learn.controllers._
import org.json4s.jackson.JsonMethods._
import scala.io.Source
import org.json4s.DefaultFormats._
import org.json4s._

trait JsonLoader {
	def loadSeries: List[Serial]
	def loadClasses(input: String): List[Class] 
}

trait JsonLoaderImp extends JsonLoader {

  implicit val formatters = DefaultFormats

	def loadSeries: List[Serial] = {
	   val jsonLocation = ConfigReader.jsonLocation
	   readInput(openFile(jsonLocation))
	}
    
  def openFile(jsonLoc: String) = {
    Source.fromFile(jsonLoc).getLines.toList.mkString("")
  }
  
	def readInput(json: String) = {
	   parse(json).extract[List[Serial]]
	}
	
	def loadClasses(input: String): List[Class] = for {
    line <- Source.fromFile(input).getLines.toList
  } yield parse(line).extract[Class]

}
