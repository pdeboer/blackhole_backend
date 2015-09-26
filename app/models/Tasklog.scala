package models

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps
import anorm._
import anorm.SqlParser._

/**
 * An entry in the Tasklog list
 *
 * @param uuid Tasklog The uuid of the task
 * @param coordinates_id Tasklog coordinates_id
 * @param question_id Tasklog question
 * @param answer Tasklog answer
 * @param ip Tasklog ip
 */
case class Tasklog(uuid: String, coordinates_id: BigDecimal, question_id: Int, answer: String, ip: String)

/**
 * A rated entry in the tasklog (comment)
 * @param rating
 * @param uuid
 * @param ip
 * @param coordinates_id
 */
case class ratedTasklog(rating: Int, uuid: String, ip: String, coordinates_id: BigDecimal)

/**
 * The TaskLog object
 */
object Tasklog {

  /**
   * Find all Tasklogs
   *
   * @return List[Tasklog] Lists all Tasklogs
   */
  def findAll(): List[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT uuid, coordinates_id, question_id, answer, timestamp, ip from tasklog").as(Tasklog.simpleTasklog *)
    }
  }

  /**
   * Find all tasklogs by a given uuid
   *
   * @param uuid The uuid of the user
   * @return Option[Tasklog] Gives back zero or more tasklogs for a certain uuid
   */
  def findById(uuid: Int): Option[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM tasklog WHERE uuid = {uuid}").on('uuid -> uuid).as(Tasklog.simpleTasklog.singleOpt)
    }
  }

  /**
   * Find the last tasklog entry of an user by uuid
   *
   * @param uuid The uuid of the user
   * @return Option[Tasklog] Gives back zero or the tasklog for a certain uuid
   */
  def findLastLogEntry(uuid: String): Option[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM tasklog WHERE uuid = {uuid} ORDER BY timestamp DESC LIMIT 1").on('uuid -> uuid).as(Tasklog.simpleTasklog.singleOpt)
    }
  }

  /**
   * Find best rated galaxy and their task
   *
   * @return List[ratedTasklog]
   */
  def findbestRated(): List[ratedTasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT SUM(IF(tasklog.answer = 'yes', tasks.value, 0)) AS rating, uuid, ip, coordinates_id FROM pplibdataanalyzer.tasklog INNER JOIN tasks ON tasks.id = question_id GROUP BY coordinates_id ORDER BY rating DESC").as(Tasklog.ratedTasklist *)
    }
  }

  /**
   * Gets the number of solved tasks by uuid
   *
   * @param uuid The uuid of the user
   * @return Int the number of solved tasks
   */
  def getNumberOfSolvedTasks(uuid: String): Int = {
    DB.withConnection { implicit connection =>
      val result = SQL("SELECT COUNT(*)-1 as count FROM pplibdataanalyzer.tasklog WHERE `uuid` = {uuid} LIMIT 1")
        .on('uuid -> uuid)
        .apply
        .headOption
      result.map(row => row[Int]("count")).getOrElse(0)
    }
  }

  /**
   * Insert Tasklog
   *
   * @param uuid The uuid of the user
   * @param coordinates_id The coordinates id (sdss_id)
   * @param question_id The question id
   * @param answer The answer given (yes / no)
   * @param ip The logged ip
   * @return Option[Long] The id of the insert
   */
  def insertTasklog(uuid: String, coordinates_id: BigDecimal, question_id: Int, answer: String, ip: String): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("INSERT INTO tasklog(`uuid`, `coordinates_id`, `question_id`, `answer`, `ip`) VALUES ({uuid}, {coordinates_id}, {question_id}, {answer}, {ip})")
        .on('uuid -> uuid)
        .on('coordinates_id -> coordinates_id)
        .on('question_id -> question_id)
        .on('answer -> answer)
        .on('ip -> ip)
        .executeInsert()
    }
    id
  }

  /**
   * Tasklog struct
   */
  val simpleTasklog = {
    get[String]("uuid") ~ // get[Option[Int]]
      get[BigDecimal]("coordinates_id") ~
      get[Int]("question_id") ~
      get[String]("answer") ~
      get[String]("ip") map {
        case uuid ~ coordinates_id ~ question_id ~ answer ~ ip => Tasklog(uuid, coordinates_id, question_id, answer, ip)
      }
  }

  /**
   * rated Tasklog struct
   */
  val ratedTasklist = {
    get[Int]("rating") ~ // get[Option[Int]]
      get[String]("uuid") ~
      get[String]("ip") ~
      get[BigDecimal]("coordinates_id") map {
        case rating ~ uuid ~ ip ~ coordinates_id => ratedTasklog(rating, uuid, ip, coordinates_id)
      }
  }

}