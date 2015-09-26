package models
import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._
/**
 * An entry in the Task list
 *
 * @param id Task The id of the Task
 * @param task Task The task description itself
 * @param taskType Task The type of the task
 * @param formerTaskId Task The task which is before in the tasklist
 * @param laterTaskId Task The task which is later in the tasklist
 * @param exitOn Task Is there an exit on rule?
 * @param businessRule Taks Is there a given business rule
 * @param comment Task The task comment, to have as a description for the task
 */
case class Task(id: Int, task: String, taskType: String, value: Int, formerTaskId: Int, laterTaskId: Int, exitOn: String, businessRule: String, comment: String)

/**
 * Task data access
 */
object Task {

  /**
   * Find all Tasks
   *
   * @return List[Task] All tasks
   */
  def findAll(): List[Task] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM tasks").as(Task.simpleTask *)
    }
  }

  /**
   * Find Task by Id
   *
   * @param id The id of the Task
   * @return Option[Task] Gives back zero or one task with the id
   */
  def findById(id: Int): Option[Task] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tasks WHERE id = {id}").on('id -> id).as(Task.simpleTask.singleOpt)
    }
  }

  /**
   * The comment can be updated in the cms
   *
   * @param id The id of the task
   * @param comment The new comment
   * @return Int Gives back a one if success or a zero if the new comment update failed
   */
  def updateComment(id: Int, comment: String): Int = {
    DB.withConnection({ implicit connection =>
      val result = SQL("UPDATE tasks SET `comment` = {comment} WHERE `id` = {id}")
        .on('comment -> comment)
        .on('id -> id)
        .executeUpdate()
    })
    1
  }

  /**
   * Simple Task structure
   */
  val simpleTask = {
    get[Int]("id") ~
      get[String]("task") ~
      get[String]("taskType") ~
      get[Int]("value") ~
      get[Int]("formerTaskId") ~
      get[Int]("laterTaskId") ~
      get[String]("exitOn") ~
      get[String]("businessRule") ~
      get[String]("comment") map {
        case id ~ task ~ taskType ~ value ~ formerTaskId ~ laterTaskId ~ exitOn ~ businessRule ~ comment => Task(id, task, taskType, value, formerTaskId, laterTaskId, exitOn, businessRule, comment)
      }
  }

}