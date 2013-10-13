/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.hadoop

// Specs2
import org.specs2.mutable.Specification

// Scalding
import com.twitter.scalding._
import com.typesafe.scalalogging.slf4j.Logging

class ClassDataJob extends Specification with TupleConversions {

  "A WordCount job" should {
    JobTest("edu.illinois.learn.hadoop.WordCountJob").
      arg("input", "inputFile").
      arg("output", "outputFile").
      source(TextLine("inputFile"), List("0" -> "hack hack hack and hack")).
      sink[(String,Int)](Csv("outputFile")){ outputBuffer =>
        val outMap = outputBuffer.toMap
        "count words correctly" in {
          outMap("hack") must be_==(4)
          outMap("and") must be_==(1)
        }
      }.
      run.
      finish
  }

  "A Typed WordCount job" should {
    JobTest("edu.illinois.learn.hadoop.TypedWordCountJob").
      arg("input", "inputFile").
      arg("output", "outputFile").
      source(TextLine("inputFile"), List("0" -> "hack hack hack and hack")).
      sink[(String,Int)](Csv("outputFile")){ outputBuffer =>
        val outMap = outputBuffer.toMap
        "count words correctly" in {
          outMap("hack") must be_==(4)
          outMap("and") must be_==(1)
        }
      }.
      run.
      finish
  }

  // "A Department Count job" should {
  //   JobTest("edu.illinois.learn.hadoop.DepartmentCountJob").
  //     arg("input", "inputFile").
  //     arg("output", "outputFile").
  //     source(Tsv("inputFile"), List((0,"AAS\the\t"))).
  //     sink[(String,Int)](Csv("outputFile")){ outputBuffer =>
  //       val outMap = outputBuffer.toMap
  //       "count departments correctly" in {
  //         outMap("AAS") must be_==(1)
  //       }
  //     }.
  //     run.
  //     finish
  // }
}
