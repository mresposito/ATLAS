package edu.illinois.learn.controllers

import edu.illinois.learn.models.DataAccessLayer
import edu.illinois.learn.models.Class
import edu.illinois.learn.utils.TSVUtil
import edu.illinois.learn.utils.JsonClassReader

class RunWrapper(val cls: List[ClassAnalysis]) extends TSVUtil {

  def run(fun: ClassAnalysis => Map[String, List[Class]], jobName: String): Unit = cls.map { a: ClassAnalysis =>
    writeResults(a.name + jobName, fun(a))
  }
  def runCounted(fun: ClassAnalysis => Map[String, Int], jobName: String): Unit = cls.map { a: ClassAnalysis =>
    writeResultsCounted(a.name + jobName, fun(a))
  }
}

object ClassRunner extends TSVUtil with JsonClassReader {
  def main(args: Array[String]) = {
    val classData = "data/listOfClasses.json"
    val dal = new DataAccessLayer
    val classesFromJson = loadClasses(classData)

    val all = ClassLoader.loadAll(classesFromJson)
    val genEds = new ClassAnalysis("genEds", ClassLoader.genEds(classesFromJson))
    val moodle = new ClassAnalysis("moodle", all.classes.filter{ el =>
      dal.getCourseId(el.crn).isDefined 
    })
    val lecture = ClassLoader.loadLectures(classesFromJson)
    val online = ClassLoader.loadOnline(classesFromJson)
    val moodleGenEd = new ClassAnalysis("moodleGenEd",
      ClassLoader.genEds(moodle.classes)
    )
  
    val classes = List(all, moodle, genEds, moodleGenEd,
      lecture, online)
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
