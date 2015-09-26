package models
import play.api.db._
import play.api.Play.current
import scala.language.postfixOps

import anorm._
import anorm.SqlParser._

/**
 * The Workset which is a set of coordinates to be shown now
 *
 * @param id Workset The id of the workset
 * @param coordinates_id Workset The id of the respected coordinates
 * @param set_id Workset The set_id which tells the "name" id of the workset
 */
case class Workset(id: Int, coordinates_id: Int, set_id: Int)

/**
 * This Model is used to get data from the worksetmodel
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Workset {

  /**
   * Find a workset by their set id
   *
   * @param set_id The set id of the worksetz
   * @return List[Workset] The List of worksets found by id
   */
  def findById(set_id: Int): List[Workset] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT id, coordinates_id, set_id FROM coordinatesToSet WHERE set_id = {set_id}").on('set_id -> set_id).as(Workset.simpleWorkset *)
    }
  }

  /**
   * Returns all worksets
   *
   * @return List[(int, String)] All sets in the database
   */
  def getAllSets(): List[(Int, String)] = {
    DB.withConnection { implicit connection =>
      val selectSets = SQL("SELECT id, set_name FROM set")
      val sets = selectSets().map(row =>
        row[Int]("id") -> row[String]("set_name")).toList
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
        case id ~ coordinates_id ~ set_id => Workset(id, coordinates_id, set_id)
      }
  }

}

