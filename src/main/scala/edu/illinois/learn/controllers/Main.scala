package edu.illinois.learn.controllers

import edu.illinois.learn.models.DataAccessLayer
import edu.illinois.learn.models.Class
import edu.illinois.learn.utils.TSVUtil

class RunWrapper[A <: ClassAnalysis](val cls: List[A]) extends TSVUtil {

  def run(fun: A => Map[String, List[Class]], jobName: String): Unit = cls.map { a: A =>
    writeResults(a.name + jobName, fun(a))
  }
}

object ClassRunner extends TSVUtil  {
  def main(args: Array[String]) = {
    val classData = "data/listOfClasses.json"
    val all = new AllClasses(classData)
    val classes = List(all, new Lectures(classData),
      new GenEds(classData), new Online(classData))
    val wrapper = new RunWrapper(classes)
    val dal = new DataAccessLayer

    wrapper.run(_.countSectionsPerDepartmens, "SectionsPerDepartments")
    wrapper.run(_.countInstructorPerSession, "InstuctorPerSection")
    wrapper.run(_.countSectionsPerClass, "SectionsPerClass")
    wrapper.run(_.countLocationPerSession, "LocationPerSection")
    
    writeResults("forumTypes", dal.countForumType)
    writeResults("forumsCountPerSection", dal.joinCourses(all.classes).groupBy {
      case (k,v) => k.classSpec
    })
    writeResults("forumsCountPerDepartment", dal.joinCourses(all.classes).groupBy {
      case (k,v) => k.dep
    })
    print("All Classes")
    val allCFA = new ClassForumAnalysis(all.classes)
    writeResultsCounted("postsPerClass", allCFA.postsPerClass)
  }
}
