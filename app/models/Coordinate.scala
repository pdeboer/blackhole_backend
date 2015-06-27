package models

import play.api.data.Form
import play.api.data.Forms._
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps
import play.api.Logger

import anorm._
import anorm.SqlParser._

import scala.text

/**
* An entry in Coordinates Lis
*
* @param id Coordinates id
* @param sdss_id Coordinates sdss_id
* @param ra Coordinates ra
* @param dec Coordinates dec
* @param active Coordinates active
*/
case class Coordinate(id: Option[Int], sdss_id: BigDecimal, ra: BigDecimal, dec: BigDecimal, active: Int)

object Coordinate {

  /**
   * Find all Coordinates
   *
   * @param limit
   * @param offset
   * @return
   */
  def findSome(limit: Int = Coordinate.countCoordinates(), offset: Int = 0): List[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT `Id`, `sdss_id`, `ra`, `dec`, `active` FROM coordinates LIMIT {limit} OFFSET {offset}").on('limit -> limit, 'offset -> offset).as(Coordinate.simpleCoordinates *)
    }
  }

  def findAll() : List[Coordinate] = {
    findSome()
  }

  /**
   * Find Coordinates by Id
   *
   * @param id
   * @return
   */
  def findById(id: Int): Option[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM coordinates WHERE id = {id}").on('id -> id).as(Coordinate.simpleCoordinates.singleOpt)
    }
  }

  /**
   * Find Random Coordinates
   *
   * @return
   */
  def findByRand(): Option[Coordinate] = {
  DB.withConnection { implicit connection =>
    SQL("SELECT * FROM coordinates ORDER BY RAND() LIMIT 1").as(Coordinate.simpleCoordinates.singleOpt)
  }
}

  /**
   * Count Coordinates
   *
   * @return
   */
  def countCoordinates(): Int = {
    DB.withConnection { implicit connection =>
      val rowOption = SQL("SELECT COUNT(*) as count FROM coordinates")
        .apply
        .headOption
      rowOption.map(row => row[Int]("count")).getOrElse(0)
    }

  }



  /**
   * Insert Coordinates
   *
   * @param sdss_id
   * @param ra
   * @param dec
   * @return
   */
  def insertCoordinate(sdss_id: BigDecimal, ra: BigDecimal, dec: BigDecimal): Int = {
    DB.withConnection( { implicit connection =>
      SQL("INSERT INTO coordinates(`sdss_id`, `ra`, `dec`, `active`) VALUES ({sdss_id}, {ra}, {dec}, {active})").on('sdss_id -> sdss_id, 'ra -> ra, 'dec -> dec, 'active -> '1').executeInsert()
    })
    1
  }

  /**
   * Delete Coordinates
   *
   * @param Id
   * @return
   */
  def deleteCoordinate(Id: Int): Int = {
    DB.withConnection( { implicit connection =>
      val result = SQL("DELETE FROM coordinates WHERE `Id` = {id}").on('id -> Id).executeUpdate()
    })
    1
  }

  /**
   * Structure for Coordinates
   */
  val simpleCoordinates = {
    get[Option[Int]]("id")~
      get[BigDecimal]("sdss_id")~
      get[BigDecimal]("ra") ~
      get[BigDecimal]("dec") ~
      get[Int]("active") map {
      case id~sdss_id~ra~dec~active => Coordinate(id, sdss_id, ra, dec, active)
    }
  }

}