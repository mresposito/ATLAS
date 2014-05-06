package edu.illinois.learn.controllers

import edu.illinois.learn.models._
import edu.illinois.learn.io._
import edu.illinois.learn.utils.TSVUtil
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.Logging

/**
 * This is the class that manages most
 * of the computation. Gets the input,
 * executes the query by calling the Data Access Layer
 * and writes to the specified output
 */
class QueryManager(semester: String, aggregation: Aggregation, column: Column)
	extends InputLoaderImp with OutputWriterImp with JsonLoaderImp {

  def apply = for {
    input <- readInput(semester, aggregation)
    output <- executeQuery(input)
  } yield write(output)
  
  def executeQuery(input: Input): Option[Output] = {
    // verify if the reflection is valid
    // MetaDal.reflect(column.query).map {
  	None
  }
}