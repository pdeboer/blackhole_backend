package models

import akka.event.slf4j.Logger
import play.api.db._
import play.api.Play.current
import scala.language.postfixOps
import anorm._
import anorm.SqlParser._
import play.api.Logger._


case class CompletedTaskLog(uuid: String, status: String, sdss_id: BigDecimal, set: Int)



/**
 * This Model is used to get data from the Completedtasklog-Model
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object CompletedTaskLog {

  /**
   * Find all Tasklogs
   *
   * @return List[Tasklog] Lists all Tasklogs
   */
  def findAll(): List[CompletedTaskLog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT uuid, status, sdss_id, set FROM completedtasklog").as(CompletedTaskLog.simpleCompletedTaskLog *)
    }
  }

  /**
   * Find the last completedtasklog entry of an user by uuid
   *
   * @param uuid The uuid of the user
   * @return Option[CompletedTaskLog] Gives back zero or the tasklog for a certain uuid
   */
  def findLastLogEntry(uuid: String): Option[CompletedTaskLog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM completedtasklog WHERE uuid = {uuid} ORDER BY timestamp DESC LIMIT 1").on('uuid -> uuid).as(CompletedTaskLog.simpleCompletedTaskLog.singleOpt)
    }
  }

  /**
   * Find all completedtasklogs by a given uuid
   *
   * @param uuid The uuid of the user
   * @return Option[CompletedTaskLog] Gives back zero or more CompletedTasklogs for a certain uuid
   */
  def findById(uuid: Int): Option[CompletedTaskLog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM completedtasklog WHERE uuid = {uuid}").on('uuid -> uuid).as(CompletedTaskLog.simpleCompletedTaskLog.singleOpt)
    }
  }


  /**
   * Gets the number of solved tasks by uuid
   *
   * @param uuid The uuid of the user
   * @return Int the number of solved tasks
   */
  def getNumberOfCompletedTasks(uuid: String): Int = {
    DB.withConnection { implicit connection =>
      val result = SQL("SELECT COUNT(*) as count FROM pplibdataanalyzer.completedtasklog WHERE `uuid` = {uuid} LIMIT 1")
        .on('uuid -> uuid)
        .apply
        .headOption
      result.map(row => row[Int]("count")).getOrElse(0)
    }
  }

  /**
   * Insert a completed tasklog for easier matching the completed tasks by uuid
   *
   * @param uuid The uuid of the task completioner
   * @param status The status (the "why") of the completion
   * @param sdss_id The sdss_id to indentify the completed galaxy
   * @param set The set which the galaxy is for
   *
   * @return Option[Long] shows if the completion is ok or not
   */
  def insertCompletedTasklog(uuid: String, status: String, sdss_id: BigDecimal, set: Int): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("INSERT INTO completedtasklog(`uuid`, `status`, `sdss_id`, `set`) VALUES ({uuid}, {status}, {sdss_id}, {set})")
        .on('uuid -> uuid)
        .on('status -> status)
        .on('sdss_id -> sdss_id)
        .on('set -> set)
        .executeInsert()
    }
    id
  }

  /**
   * Simple CompletedTaskLogStructure
   */
  val simpleCompletedTaskLog = {
      get[String]("uuid") ~
      get[String]("status") ~
      get[BigDecimal]("sdss_id") ~
      get[Int]("set")  map {
      case uuid ~ status ~ sdss_id ~ set => CompletedTaskLog(uuid, status, sdss_id, set)
    }
  }


}