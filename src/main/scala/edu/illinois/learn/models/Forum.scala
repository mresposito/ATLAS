package edu.illinois.learn.models

import scala.slick.driver.MySQLDriver.simple._
import java.sql.Timestamp

case class ForumInfo(
  meta: Forum,
  discussion: ForumDiscussion,
  post: ForumPost)

case class Forum(
  id: Long,
  courseId: Long,
  forumType: String,
  name: String)

object Forums extends Table[Forum]("mdl_forum") {
  def id = column[Long]("id", O.PrimaryKey)
  def courseId = column[Long]("course")
  def forumType = column[String]("type")
  def name = column[String]("intro")

  def * = id ~ courseId ~ forumType ~ name <> (Forum, Forum.unapply _)
}

case class ForumDiscussion(
  id: Long,
  courseId: Long,
  forumId: Long,
  name: String,
  userId: Long)

object ForumDiscussions extends Table[ForumDiscussion]("mdl_forum_discussions") {
  def id = column[Long]("id", O.PrimaryKey)
  def courseId = column[Long]("course")
  def forumId = column[Long]("forum")
  def name = column[String]("name")
  def userId = column[Long]("userid")

  def * = id ~ courseId ~ forumId ~
    name ~ userId <> (ForumDiscussion, ForumDiscussion.unapply _)
}

case class ForumPost(
  id: Long,
  discussionId: Long,
  parentId: Long,
  userId: Long,
  created: Timestamp,
  subject: String,
  message: String)

object ForumPosts extends Table[ForumPost]("mdl_forum_posts") {
  def id = column[Long]("id", O.PrimaryKey)
  def discussionId = column[Long]("discussion")
  def parentId = column[Long]("parent")
  def userId = column[Long]("userid")
  def created = column[Timestamp]("created")
  def subject = column[String]("subject")
  def message = column[String]("message")

  def * = id ~ discussionId ~ parentId ~ 
    userId ~ created ~ subject ~ message <> (ForumPost, ForumPost.unapply _)
}
