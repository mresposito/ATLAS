package edu.illinois.learn.controllers

import edu.illinois.learn.models.DataAccessLayer
import edu.illinois.learn.models.{Class, ForumInfo}

case class ClassForum(cls: Class, forums: List[ForumInfo])

class ClassForumAnalysis(val classAnalysis: ClassAnalysis) {
  val dal = new DataAccessLayer
  val classes = classAnalysis.classes
  val name = classAnalysis.name

  val validCourses = classes.filter { cls =>
    dal.getCourseId(cls.crn).isDefined
  }
  
  val courseWithEnrollment = classes.map { cls =>
  	(cls, dal.getCRNEnrollment(cls.crn))
  }.filter(_._2.isDefined)

  def postsPerClass = validCourses.map { cls =>
    (cls.classSpec,
      dal.findPostsPerClass(dal.getCourseId(cls.crn).get))
  }.toMap
  
  def postsPerStudentPerClass = courseWithEnrollment.map {
    case (cls, Some(enr)) => (cls.classSpec,
    	dal.findPostsPerClass(dal.getCourseId(cls.crn).get)/enr)
  }.toMap

  def postsPerDepartment = validCourses.map { cls =>
    (cls.dep,
      dal.findPostsPerClass(dal.getCourseId(cls.crn).get))
  }.groupBy{ 
    case (k,v) => k
  }.map {
    case (k,v) => (k, v.map(_._2).sum)
  }
  
  lazy val departmentPostCount = courseWithEnrollment.groupBy(_._1.dep).map {
    case(dep, lst) => (dep, lst.map(_._2.get).sum)
  }.toMap
  
  def postsPerStudentPerDepartment = postsPerDepartment.filter{ 
    case(dep, v) => departmentPostCount.contains(dep)
  }.map {
    case (dep, v) => (dep, v/departmentPostCount(dep))
  }

  def deviationPerClass = validCourses.map { cls =>
    (cls.classSpec,
      dal.findDeviationPerClass(dal.getCourseId(cls.crn).get))
  }.toMap

  def forumCountPerClass = dal.joinCourses(classes).groupBy {
    case (k,v) => k.classSpec
  }

  def forumCountPerDepartment =  dal.joinCourses(classes).groupBy {
    case (k,v) => k.dep
  }
}

