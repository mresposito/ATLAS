/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.classAnalysis

import scala.io.Source
import java.io.File
import org.json4s._
import org.json4s.jackson.JsonMethods._

trait TSVUtil {

  def readTSVLines(input: String):Array[String] = {
    Source.fromFile(input).getLines.drop(1).toArray
  }

  def readfromIO(input: String):Array[Array[String]] = {
    readTSVLines(input).map(_.split("\t"))
  }
}

trait JsonClassReader {
  implicit val formatters = DefaultFormats
  
  def loadClasses(input: String): List[Class] = for {
    line <- Source.fromFile(input).getLines.toList
  } yield parse(line).extract[Class]
}
