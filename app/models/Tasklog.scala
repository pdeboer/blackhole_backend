package models

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

import scala.language.postfixOps

/**
 * An entry in the Tasklog list
 *
 * @param uuid Tasklog The uuid of the task
 * @param set Tasklog set
 * @param sdss_id Tasklog sdss_id
 * @param question_id Tasklog question
 * @param spectra_id Tasklog spectra id (special, because there are more than one spectra possible)
 * @param answer Tasklog answer
 * @param ip Tasklog ip
 */
case class Tasklog(uuid: String, set: Int, sdss_id: BigDecimal, question_id: Int, spectra_id: Int, answer: String, ip: String)

/**
 * A rated entry in the tasklog (comment)
 * @param rating
 * @param uuid
 * @param ip
 * @param sdss_id
 */
case class ratedTasklog(rating: Int, uuid: String, ip: String, sdss_id: BigDecimal)

/**
 * Shows all classifications of a user
 *
 * @param sdss_id Sdss id of galaxy
 * @param question_id Id of the question (to task)
 * @param answer Answer on the question
 */
case class classificationsOfUser(sdss_id: BigDecimal, question_id: Int, answer: String)

/**
 * This Model is used to get data from the Tasklogmodel
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Tasklog {

  /**
   * Find all Tasklogs
   *
   * @return List[Tasklog] Lists all Tasklogs
   */
  def findAll(): List[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT uuid, set, sdss_id, question_id, answer, timestamp, ip from tasklog").as(Tasklog.simpleTasklog *)
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
   * Find all classifications of a user
   *
   * @return List[UserWithClassification] Lists all users
   */
  def findAllClassificationsOfUser(id: Int): List[classificationsOfUser] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT tasklog.sdss_id, tasklog.question_id, tasklog.answer FROM pplibdataanalyzer.tasklog INNER JOIN users ON users.uuid = tasklog.uuid WHERE users.`id` = {id} ORDER BY sdss_id, question_id")
        .on('id -> id)
        .as(Tasklog.classificationOfUser *)
    }
  }


  /**
   * Find best rated galaxy and their task
   *
   * @return List[ratedTasklog]
   */
  def findbestRated(): List[ratedTasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT SUM(IF(tasklog.answer = 'yes', tasks.value, 0)) AS rating, uuid, ip, sdss_id FROM pplibdataanalyzer.tasklog INNER JOIN tasks ON tasks.id = question_id GROUP BY sdss_id ORDER BY rating DESC").as(Tasklog.ratedTasklist *)
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
   * @param set The set of the user
   * @param sdss_id The sdss id (sdss_id)
   * @param question_id The question id
   * @param answer The answer given (yes / no)
   * @param ip The logged ip
   * @return Option[Long] The id of the insert
   */
  def insertTasklog(uuid: String, set: Int, sdss_id: BigDecimal, question_id: Int, spectra_id: Int, answer: String, ip: String): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("INSERT INTO tasklog(`uuid`, `set`, `sdss_id`, `question_id`, `spectra_id`, `answer`, `ip`) VALUES ({uuid}, {sdss_id}, {question_id}, {spectra_id}, {answer}, {ip})")
        .on('uuid -> uuid)
        .on('set -> set)
        .on('sdss_id -> sdss_id)
        .on('question_id -> question_id)
        .on('spectra_id -> spectra_id)
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
      get[Int]("set") ~
      get[BigDecimal]("sdss_id") ~
      get[Int]("question_id") ~
      get[Int]("spectra_id") ~
      get[String]("answer") ~
      get[String]("ip") map {
        case uuid ~ set ~ sdss_id ~ question_id ~ spectra_id ~ answer ~ ip => Tasklog(uuid, set, sdss_id, question_id, spectra_id, answer, ip)
      }
  }

  /**
   * rated Tasklog struct
   */
  val ratedTasklist = {
    get[Int]("rating") ~ // get[Option[Int]]
      get[String]("uuid") ~
      get[String]("ip") ~
      get[BigDecimal]("sdss_id") map {
        case rating ~ uuid ~ ip ~ sdss_id => ratedTasklog(rating, uuid, ip, sdss_id)
      }
  }

  /**
   * classificationOfUser struct
   */
  val classificationOfUser = {
      get[BigDecimal]("sdss_id") ~
      get[Int]("question_id") ~
      get[String]("answer") map {
      case sdss_id ~ questio_id ~ answer => classificationsOfUser(sdss_id, questio_id, answer)
    }
  }



}