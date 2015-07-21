package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps
import play.api.Logger

import anorm._
import anorm.SqlParser._
/**
 * An entry in the Task list
 *
 * @param id Task id
 * @param task Task task
 * @param taskType Task taskType
 * @param formerTaskId Task formerTaskId
 * @param laterTaskId Task laterTaskId
 * @param exitOn Task exitOn
 * @param businessRule Taks businessRule
 * @param comment Task comment
 */
case class Task(id: Int, task: String, taskType: String, value: Int, formerTaskId: Int, laterTaskId: Int, exitOn: String, businessRule: String, comment: String)

/**
 * Task data access
 */
object Task {

  /**
   * Find all Tasks
   *
   * @return
   */
  def findAll(): List[Task] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM tasks").as(Task.simpleTask *)
    }
  }

  /**
   * Find Task by Id
   *
   * @param id
   * @return
   */
  def findById(id: Int): Option[Task] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tasks WHERE id = {id}").on('id -> id).as(Task.simpleTask.singleOpt)
    }
  }



  def updateComment(id: Int, comment: String): Int = {
    DB.withConnection( { implicit connection =>
      val result = SQL("UPDATE tasks SET `comment` = {comment} WHERE `id` = {id}")
        .on('comment -> comment)
        .on('id -> id)
        .executeUpdate()
    })
    1
  }



  /**
   * Task structure
   */
  val simpleTask = {
    get[Int]("id")~
    get[String]("task") ~
      get[String]("taskType") ~
      get[Int]("value") ~
      get[Int]("formerTaskId") ~
      get[Int]("laterTaskId") ~
      get[String]("exitOn")~
      get[String]("businessRule")~
      get[String]("comment") map {
      case id~task~taskType~value~formerTaskId~laterTaskId~exitOn~businessRule~comment => Task(id, task, taskType, value, formerTaskId, laterTaskId, exitOn, businessRule, comment)
    }
  }

}