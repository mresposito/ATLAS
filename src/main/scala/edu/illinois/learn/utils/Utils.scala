/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.utils

import scala.io.Source
import com.typesafe.scalalogging.slf4j.{ LazyLogging => Logging }

trait TSVUtil extends Logging {

  def readTSVLines(input: String):Array[String] = try {
    Source.fromFile(input).getLines.drop(1).toArray
  } catch {
    case e: Exception => {
      logger.error(s"Could not read from file ${input}.")
      Array()
    }
  }

  def readfromIO(input: String):Array[Array[String]] = {
    readTSVLines(input).map(_.split("\t"))
  }
}