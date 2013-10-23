/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.utils

import edu.illinois.learn.models.Class
import java.io.PrintWriter
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source

trait TSVUtil {

  def readTSVLines(input: String):Array[String] = {
    Source.fromFile(input).getLines.drop(1).toArray
  }

  def readfromIO(input: String):Array[Array[String]] = {
    readTSVLines(input).map(_.split("\t"))
  }

  def count[B](a: Map[String, List[B]]): Map[String, Int] = a.map(k => (k._1, k._2.length))

  def writeResults[B](path: String, results: Map[String, List[B]], output: String = "results/") = {
    writeResultsCounted(path, count(results))
  }
  
  def writeResultsCounted[B](path: String, results: Map[String, B], output: String = "results/") = {
    val p = new PrintWriter(output + path + ".tsv", "UTF-8")
    p.print {
      results.toList.sortBy(el => {
        val e = el._2.toString
        if(e contains ".")
          e.toDouble
        else 
          e.toInt
      }). // little hack
        map(el => el._1 + "\t" + el._2.toString).mkString("\n")
    }
    p.close()
  }
}

trait JsonClassReader {
  implicit val formatters = DefaultFormats
  
  def loadClasses(input: String): List[Class] = for {
    line <- Source.fromFile(input).getLines.toList
  } yield parse(line).extract[Class]
}
