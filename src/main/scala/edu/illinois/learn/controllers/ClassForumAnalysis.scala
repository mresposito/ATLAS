package edu.illinois.learn.controllers

import edu.illinois.learn.models.DataAccessLayer
import edu.illinois.learn.models.{Class, ForumInfo}

case class ClassForum(cls: Class, forums: List[ForumInfo])

class ClassForumAnalysis(val classes: List[Class]) {
  val dal = new DataAccessLayer

  val validCourses = classes.filter { cls =>
    dal.getCourseId(cls.crn).isDefined
  }

  def postsPerClass = validCourses.map { cls =>
    (cls.classSpec,
      dal.findPostsPerClass(dal.getCourseId(cls.crn).get))
  }.toMap
}
