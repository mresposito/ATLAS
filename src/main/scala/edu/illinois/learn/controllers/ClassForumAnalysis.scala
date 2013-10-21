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

  def postsPerClass = validCourses.map { cls =>
    (cls.classSpec,
      dal.findPostsPerClass(dal.getCourseId(cls.crn).get))
  }.toMap

  def forumCountPerSection = dal.joinCourses(classes).groupBy {
    case (k,v) => k.dep
  }

  def forumCountPerDepartment =  dal.joinCourses(classes).groupBy {
    case (k,v) => k.dep
  }
}
