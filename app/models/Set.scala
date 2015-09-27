package models
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

import scala.language.postfixOps

/**
 * This model serves the set definition to match with the coordinates
 *
 * @param id The id of the Set
 * @param set_name The name of the set
 * @param is_active The status of the set
 */
case class Set(id: Int, set_name: String, is_active: Int)

/**
 * This Model is used to get data from the Setmodel
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Set {

  /**
   * Find all Sets
   *
   * @return List[Set] List of all sets
   */
  def findAll(): List[Set] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM sets").as(Set.simpleSet *)
    }
  }

  /**
   * Find the active Set
   *
   * @return Option[Set] zero or one set to be found by id
   */
  def findActiveSet(): Int = {
    DB.withConnection { implicit connection =>
      val rowOption = SQL("SELECT id FROM sets WHERE is_active = 1")
        .apply
        .headOption
      rowOption.map(row => row[Int]("id")).getOrElse(0)
    }
  }

  /**
   * Inserts a new set
   *
   * @param set_name The name of the set
   * @param is_active If the set is active
   *
   * @return The insert id
   */
  def insertSet(set_name: String, is_active: Int): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("INSERT INTO comments(`set_name`, `is_active`) VALUES ({set_name}, {is_active})")
        .on('set_name -> set_name)
        .on('is_active -> is_active)
        .executeInsert()
    }
    id
  }
  /**
   * Set structure
   */
  val simpleSet = {
    get[Int]("id") ~
      get[String]("set_name") ~
      get[Int]("is_active") map {
      case id ~ set_name ~ is_active => Set(id, set_name, is_active)
    }
  }

}