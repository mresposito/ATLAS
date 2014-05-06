/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.models
// Specs2
import org.specs2.mutable._
import com.typesafe.scalalogging.slf4j.Logging

class ReflectionSpec extends Specification {
  
  val dal = new DAL("2012fall", Aggregation("a"), Column("b", "c"))
//  val reflector = Reflector(dal)
//  "Reflexive should" should {
//  	"has Reflexive should" in {
//      "be false if not valid method" in {
//        reflector.hasReflection("WhatsMyMethod") must beFalse
//      }
//      "be true for reflect method" in {
//        reflector.hasReflection("reflect") must beTrue
//      }
//      "be true for hasReflection method" in {
//        reflector.hasReflection("hasReflection") must beTrue
//      }
//      "be true for hello method" in {
//        reflector.hasReflection("hello") must beTrue
//      }
//  	}
//  	
//  	"reflect should" in {
//  	  "call reflect method correctly" in {
//	  	  reflector.reflect("hello")("michele") must beEqualTo("Hello michele")
//  	  }
//  	}
//  }
}
