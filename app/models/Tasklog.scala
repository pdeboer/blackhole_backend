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
 * @param question Tasklog question
 * @param answer Tasklog answer
 */
case class Tasklog(uuid: String, question: String, answer: String)

object Tasklog {

  def findAll(): List[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tasklog").as(Tasklog.simpleTasklog *)
    }
  }

  def findById(uuid: Int): Option[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tasklog WHERE uuid = {uuid}").on('uuid -> uuid).as(Tasklog.simpleTasklog.singleOpt)
    }
  }

  def insertTasklog(uuid: String, question: String, answer: String): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("INSERT INTO tasklog(`uuid`, `question`, `answer`) VALUES ({uuid}, {question}, {answer})")
        .on('uuid -> uuid)
        .on('question -> question)
        .on('answer -> answer)
        .executeInsert()
    }
    id
  }

  val simpleTasklog = {
      get[String]("uuid") ~ // get[Option[Int]]
      get[String]("question") ~
      get[String]("answer") map {
      case uuid~question~answer => Tasklog(uuid, question, answer)
    }
  }

}