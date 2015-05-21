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
 * @param id Tasklog id
 * @param uuid Tasklog uuid
 * @param question Tasklog question
 * @param answer Tasklog answer
 */
case class Tasklog(id: Int, uuid: String, question: String, answer: String)

object Tasklog {

  def findAll(): List[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tasklog").as(Tasklog.simpleTasklog *)
    }
  }

  def findById(id: Int): Option[Tasklog] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tasklog WHERE id = {id}").on('id -> id).as(Tasklog.simpleTasklog.singleOpt)
    }
  }

  def insertLog(tasklog: Tasklog): Option[Long] = {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO tasklog VALUES ({uuid}, {question}, {answer})")
        .on('uuid -> tasklog.uuid, 'question -> tasklog.question, 'answer -> tasklog.answer)
        .executeInsert()
    }
  }

  val simpleTasklog = {
    get[Int]("id")~
      get[String]("uuid") ~
      get[String]("question") ~
      get[String]("answer") map {
      case id~uuid~question~answer => Tasklog(id, uuid, question, answer)
    }
  }

}