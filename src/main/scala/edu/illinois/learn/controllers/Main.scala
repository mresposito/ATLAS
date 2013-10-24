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
    val moodleOnline = new ClassAnalysis("moodleOnline",
      ClassLoader.online(moodle.classes))
    val lecture = ClassLoader.loadLectures(classesFromJson)
    val online = ClassLoader.loadOnline(classesFromJson)
    val moodleGenEd = new ClassAnalysis("moodleGenEd",
      ClassLoader.genEds(moodle.classes)
    )
    // run the analysis on all the classes
    executeClassAnalysis(List(all, moodle, genEds, moodleGenEd,
      lecture, online))
    // start forum analysis    
    writeResults("forumTypes", dal.countForumType)

    print("All Classes")

    val toFm = List(moodle, moodleGenEd, moodleOnline)
    executeForumAnalysis(toFm.map(el => new ClassForumAnalysis(el)))
  }

  def executeForumAnalysis(forums: List[ClassForumAnalysis]) = {
    def run[A](fun: ClassForumAnalysis => Map[String, List[A]],
      jobName: String): Unit = forums.map { a: ClassForumAnalysis =>
      writeResults(a.name + "Forum" + jobName, fun(a))
    }
    def runCounted[A](fun: ClassForumAnalysis => Map[String, A],
      jobName: String): Unit = forums.map { a: ClassForumAnalysis =>
      writeResultsCounted(a.name + "Forum" + jobName, fun(a))
    }
    run(_.forumCountPerClass, "PerClass")
    run(_.forumCountPerDepartment, "PerDepartment")
    runCounted(_.postsPerClass, "PostsPerClass")
    runCounted(_.deviationPerClass, "PostStdPerClass")
  }

  def executeClassAnalysis(classes: List[ClassAnalysis]) = {
    val wrapper = new RunWrapper(classes)
    wrapper.run(_.countLocationPerSession, "LocationPerSection")
    wrapper.run(_.countInstructorPerSession, "InstuctorPerSection")
    wrapper.run(_.countSectionsPerDepartmens, "SectionsPerDepartments")
    wrapper.run(_.countSectionsPerClass, "SectionsPerClass")
    wrapper.runCounted(_.countClassesPerDepartment, "ClassesPerDepartment")
  }
}
