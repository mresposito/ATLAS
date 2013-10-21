package edu.illinois.learn.controllers

import edu.illinois.learn.models.DataAccessLayer
import edu.illinois.learn.models.Class
import edu.illinois.learn.utils.TSVUtil

class RunWrapper[A <: ClassAnalysis](val cls: List[A]) extends TSVUtil {

  def run(fun: A => Map[String, List[Class]], jobName: String): Unit = cls.map { a: A =>
    writeResults(a.name + jobName, fun(a))
  }
  def runCounted(fun: A => Map[String, Int], jobName: String): Unit = cls.map { a: A =>
    writeResultsCounted(a.name + jobName, fun(a))
  }
}

object ClassRunner extends TSVUtil  {
  def main(args: Array[String]) = {
    val classData = "data/listOfClasses.json"
    val dal = new DataAccessLayer

    val all = new AllClasses(classData)
    val genEds = new GenEds(classData)
    val moodle = new Moodle(all.classes.filter{ el =>
      dal.getCourseId(el.crn).isDefined 
    })
  
    val classes = List(all, moodle, genEds, 
      new Lectures(classData), new Online(classData))
    val wrapper = new RunWrapper(classes)

    wrapper.run(_.countLocationPerSession, "LocationPerSection")
    wrapper.run(_.countInstructorPerSession, "InstuctorPerSection")
    wrapper.run(_.countSectionsPerDepartmens, "SectionsPerDepartments")
    wrapper.run(_.countSectionsPerClass, "SectionsPerClass")
    wrapper.runCounted(_.countClassesPerDepartment, "ClassesPerDepartment")
    // start forum analysis    
    writeResults("forumTypes", dal.countForumType)
    writeResults("forumsCountPerSection", dal.joinCourses(moodle.classes).groupBy {
      case (k,v) => k.classSpec
    })
    writeResults("forumsCountPerDepartment", dal.joinCourses(moodle.classes).groupBy {
      case (k,v) => k.dep
    })
    // start joins. Sloow 
    print("All Classes")
    val allCFA = new ClassForumAnalysis(moodle.classes)

    writeResultsCounted("postsPerClass", allCFA.postsPerClass)
  }
}
