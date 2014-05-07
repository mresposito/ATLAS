package edu.illinois.learn.controllers

import edu.illinois.learn.models._
import edu.illinois.learn.io._
import edu.illinois.learn.utils.TSVUtil
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.{ LazyLogging => Logging }

/**
 * This is the class that manages most
 * of the computation. Gets the input,
 * executes the query by calling the Data Access Layer
 * and writes to the specified output
 */
class QueryManager(semester: String, aggregation: Aggregation, column: Column)
	extends InputLoaderImp with OutputWriterImp with JsonLoaderImp {

  private def formatTag = {
    def upper(s: String) = s.head.toUpper + s.tail
    upper(aggregation.tag) + upper(aggregation.tag)
  }
  /**
   * Execute as a constructor
   */
  for {
    input <- readInput(semester, aggregation)
    output <- executeQuery(input)
  } yield write(output, semester + "New", formatTag)
  
  /**
   * Looks up in the DAL if we have the query
   */
  def executeQuery(input: Input): Option[Output] = {
    val dal = new DAL(semester, aggregation, column, input)
    val reflector = new Reflector(dal)
    val method = column.query

    if(reflector hasReflection method) {
      val o = reflector.call(method)
      Some(o.asInstanceOf[Output])
    } else {
      logger.error(s"The DAL does not contain query ${method}")
      None
    }
  }
}

class Reflector(val dal: DAL) {
  
  case class Caller[T>: Null<: AnyRef](klass:T) {
    def call(methodName: String, args: AnyRef*): AnyRef = {
      def argtypes = args.map(_.getClass)
      def method = klass.getClass.getMethod(methodName, argtypes: _*)
      method.invoke(klass ,args: _*)
    }
    def hasMethod(methodName: String): Boolean = try {
      klass.getClass().getMethod(methodName)
      true
    } catch {
      case e: NoSuchMethodException => false
    }
  }
  implicit def anyref2callable[T>: Null<: AnyRef](klass: T): Caller[T] = {
    new Caller(klass)
  }

  def hasReflection(method: String): Boolean = dal hasMethod method

  def call(method: String, args: AnyRef*) = dal.call(method, args: _*)
  def call(method: String) = dal call method
}