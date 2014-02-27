package edu.illinois.learn.controllers

import edu.illinois.learn.models.DataAccessLayer
import edu.illinois.learn.models.Class
import edu.illinois.learn.utils.TSVUtil
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

import com.typesafe.scalalogging.slf4j.Logging

object ClassRunner extends Logging {

   val conf = ConfigFactory.load
  
  def main(args: Array[String]): Unit = {
    val semesters = conf.getList("classes.long")
    val sem: Seq[String] = semesters.unwrapped().map(_.toString)
    sem.map(s => new SemesterRunner(s))
  }
}

class SemesterRunner(semester: String) extends TSVUtil with  Logging {

   val conf = ConfigFactory.load
   val listOfClasses = conf.getString("input.listOfClasses")
   val dal = new DataAccessLayer
   runAnalysis
    
  def runAnalysis: Unit = {
    // loads the class metadata from json files
    val classData = listOfClasses + s"/${semester}/listOfClasses.json"
    // loads the semester classes
    val classesFromJson = ClassLoader.loadClasses(classData)

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
    writeResults("allForumTypes", dal.countForumType)

    print("All Classes")

    val toFm = List(moodle, moodleGenEd, moodleOnline)
    executeForumAnalysis(toFm.map(el => new ClassForumAnalysis(el)))
  }

  def executeForumAnalysis(forums: List[ClassForumAnalysis]) = {
    def run[A](fun: ClassForumAnalysis => Map[String, List[A]],
      jobName: String): Unit = forums.map { a: ClassForumAnalysis =>
      writeResults(semester + "/" + a.name + "Forum" + jobName, fun(a))
    }
    def runCounted[A](fun: ClassForumAnalysis => Map[String, A],
      jobName: String): Unit = forums.map { a: ClassForumAnalysis =>
      writeResultsCounted(semester + "/" + a.name + "Forum" + jobName, fun(a))
    }
    run(_.forumCountPerClass, "PerClass")
    run(_.forumCountPerDepartment, "PerDepartment")
    runCounted(_.enrollmentPerClass, "EnrollmentPerClass")
    runCounted(_.departmentPostCount, "EnrollmentPerDepartment")

    runCounted(_.postsPerClass, "PostsPerClass")
    runCounted(_.postsPerDepartment, "PostsPerDepartment")
    runCounted(_.deviationPerClass, "PostStdPerClass")
    // post counts
    runCounted(_.postsPerStudentPerClass, "PostsPerStudentPerClass")
    runCounted(_.postsPerStudentPerDepartment, "PostsPerStudentPerDepartment")
  }

  def executeClassAnalysis(cls: List[ClassAnalysis]) = {
    run(_.countLocationPerSession, "LocationPerSection")
    run(_.countInstructorPerSession, "InstuctorPerSection")
    run(_.countSectionsPerDepartmens, "SectionsPerDepartment")
    run(_.countSectionsPerClass, "SectionsPerClass")
    runCounted(_.countClassesPerDepartment, "ClassesPerDepartment")

	  def run(fun: ClassAnalysis => Map[String, List[Class]], jobName: String): Unit = cls.map { a: ClassAnalysis =>
	    writeResults(semester + "/" + a.name + jobName, fun(a))
	  }
	  def runCounted(fun: ClassAnalysis => Map[String, Int], jobName: String): Unit = cls.map { a: ClassAnalysis =>
	    writeResultsCounted(semester + "/" + a.name + jobName, fun(a))
	  }
  }
}
