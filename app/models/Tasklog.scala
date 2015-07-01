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
 * An entry in the Tasklog list
 *
 * @param uuid Tasklog uuid
 * @param coordinates_id Tasklog coordinates_id
 * @param question Tasklog question
 * @param answer Tasklog answer
 * @param ip Tasklog ip
 */
case class Tasklog(uuid: String, coordinates_id: BigDecimal, question_id: Int, answer: String, ip: String)

object Tasklog {

  /**
   * Find all Tasklogs
   *
   * @return
   */
  def findAll(): List[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT uuid, coordinates_id, question_id, answer, timestamp, ip from tasklog").as(Tasklog.simpleTasklog *)
    }
  }

  /**
   * Find tasklog piece by id
   *
   * @param uuid
   * @return
   */
  def findById(uuid: Int): Option[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM tasklog WHERE uuid = {uuid}").on('uuid -> uuid).as(Tasklog.simpleTasklog.singleOpt)
    }
  }

  /**
   * Find tasklog piece by id
   *
   * @param uuid
   * @return
   */
  def findLastLogEntry(uuid: String): Option[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM tasklog WHERE uuid = {uuid} ORDER BY timestamp DESC LIMIT 1").on('uuid -> uuid).as(Tasklog.simpleTasklog.singleOpt)
    }
  }


  /**
   * Insert Tasklog
   *
   * @param uuid
   * @param coordinates_id
   * @param question_id
   * @param answer
   * @param ip
   * @return
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
      case uuid~coordinates_id~question_id~answer~ip => Tasklog(uuid, coordinates_id, question_id, answer, ip)
    }
  }

}