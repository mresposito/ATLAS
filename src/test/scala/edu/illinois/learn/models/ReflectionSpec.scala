/*
 * Author: Michele Esposito 
 */
package edu.illinois.learn.models
// Specs2
import org.specs2.mutable._
import edu.illinois.learn.controllers.Reflector
import edu.illinois.learn.io.Empty

class ReflectionSpec extends Specification with QuerySpec {
  
  val reflector = new Reflector(dal)
  "Reflexive " should {
  	"has Reflexive should" in {
      "be false if not valid method" in {
        reflector.hasReflection("WhatsMyMethod") must beFalse
      }
      "be true for reflect method" in {
        reflector.hasReflection("reflect") must beFalse
      }
      "be false for hasReflection method" in {
        reflector.hasReflection("hasReflection") must beFalse
      }
      "be true for hello method" in {
        reflector.hasReflection("hello") must beTrue
      }
  	}
  	
  	"reflect should" in {
  	  "call reflect method correctly" in {
	  	  reflector.call("hello") must beEqualTo("Hello ")
  	  }
  	  
  	  "call methods with more arguments" in {
  	    reflector.call("hi", "michele") must beEqualTo("hi michele")
  	  }
  	}
  }
}