package edu.illinois.learn.controllers

import edu.illinois.learn.models.DataAccessLayer
import edu.illinois.learn.models.{Class, ForumInfo}

case class ClassForum(cls: Class, forums: List[ForumInfo])

class ClassForumAnalysis(val classes: List[Class]) {
  val dal = new DataAccessLayer
  val classId = classes.map { cls => 
    ClassForum(cls, dal.findForumsByCRN(cls.crn).map(dal.buildForumInfo))
  }
}
