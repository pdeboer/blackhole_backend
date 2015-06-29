package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps
import play.api.Logger

import anorm._
import anorm.SqlParser._

import io.really.jwt._
import play.api.libs.json.Json
/**
 * The Workset
 *
 * @param id Workset id
 * @param coordinates_id Workset coordinates_id
 * @param set_id Workset set_id
 */
case class Workset(id: Int, coordinates_id: Int, set_id: Int)

/**
 * Workset access
 */
object Workset {

  def findById(set_id: Int): List[Workset] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT id, coordinates_id, set_id FROM coordinatesToSet WHERE set_id = {set_id}").on('set_id -> set_id).as(Workset.simpleWorkset *)
    }
  }

  def getAllSets(): List[(Int, String)] =  {
    DB.withConnection { implicit connection =>
      val selectSets = SQL("SELECT id, set_name FROM set")
      val sets = selectSets().map(row =>
        row[Int]("id") -> row[String]("set_name")
      ).toList
      sets
    }
  }


  /**
   * Struct of Workset
   */
  val simpleWorkset = {
    get[Int]("id") ~
      get[Int]("coordinates_id") ~
      get[Int]("set_id") map {
      case id~coordinates_id~set_id => Workset(id, coordinates_id, set_id)
    }
  }

}

