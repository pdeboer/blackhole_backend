package models

import play.api.db._
import play.api.Play.current
import scala.language.postfixOps
import anorm._
import anorm.SqlParser._


/**
* Case Class for Coordinates
*
* @param id Coordinates id to have a definit indentification
* @param sdss_id Coordinates sdss_id
* @param ra Coordinates ra in decimals
* @param dec Coordinates dec in decimals
* @param active Coordinates active to shut down coordinates
*/
case class Coordinate(id: Option[Int], sdss_id: BigDecimal, ra: BigDecimal, dec: BigDecimal, active: Int)

/**
 * This Model is used to get data from the coordinates Model
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Coordinate {

  /**
   * Find all Coordinates within a limit and an offset
   *
   * @param limit The limit of coordinates which should be given back
   * @param offset The offset is the counterpair for the limit to set it like LIMIT 0, 1000
   * @return List[Coordinate] a list of all coordinates within this given range
   */
  def findSome(limit: Int = Coordinate.countCoordinates(), offset: Int = 0): List[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT `Id`, `sdss_id`, `ra`, `dec`, `active` FROM coordinates LIMIT {limit} OFFSET {offset}").on('limit -> limit, 'offset -> offset).as(Coordinate.simpleCoordinates *)
    }
  }

  /**
   * Finds all coordinates (full-list), actually a findSome without parametrs offset=0 and limit = count(coordinates)
   * @return List[Coordinate]
   */
  def findAll() : List[Coordinate] = {
    findSome()
  }

  /**
   * Find Coordinates by Id
   *
   * @param id Coordinate id
   * @return Option[Coordinate] returns zero or one coordinate which is found by id
   */
  def findById(id: Int): Option[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM coordinates WHERE id = {id} LIMIT 1").on('id -> id).as(Coordinate.simpleCoordinates.singleOpt)
    }
  }

  /**
   * Find Coordinates by sdss_id
   *
   * @param sdss_id Coordinate sdss_id
   * @return Option[Coordinate] returns zero or one coordinate which is found by sdss_id
   */
  def findBySdssId(sdss_id: BigDecimal): Option[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM coordinates WHERE sdss_id = {sdss_id}").on('sdss_id -> sdss_id).as(Coordinate.simpleCoordinates.singleOpt)
    }
  }

  /**
   * Find Random Coordinates
   *
   * @return Option[Coordinate] returns zero or one random coordinate
   */
  def findByRand(): Option[Coordinate] = {
  DB.withConnection { implicit connection =>
    SQL("SELECT * FROM coordinates ORDER BY RAND() LIMIT 1").as(Coordinate.simpleCoordinates.singleOpt)
  }
}

  /**
   * Find Random Coordinates within a given Set
   * => Joins the set
   *
   * @return Option[Coordinate] returns zero or max one random coordinate within a set
   */
  def findByRandWithSet(): Option[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT coordinates.Id, coordinates.sdss_id, coordinates.ra, coordinates.dec, coordinates.active FROM coordinates INNER JOIN coordinatesToSet ON coordinates.id = coordinatesToSet.coordinates_id INNER JOIN pplibdataanalyzer.set ON coordinatesToSet.set_id = pplibdataanalyzer.set.id WHERE pplibdataanalyzer.set.id = 1 ORDER BY RAND() LIMIT 1").as(Coordinate.simpleCoordinates.singleOpt)
    }
  }

  /**
   * Count of all Coordinates
   *
   * @return int Number of coordinates
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
   * Insert a coordinate
   *
   * @param sdss_id the given, unique! sdss_id
   * @param ra Ra number in decimal
   * @param dec Dec number in decimal
   * @return int 1 for errorless 0 for with error
   */
  def insertCoordinate(sdss_id: BigDecimal, ra: BigDecimal, dec: BigDecimal): Int = {
    DB.withConnection( { implicit connection =>
      SQL("INSERT INTO coordinates(`sdss_id`, `ra`, `dec`, `active`) VALUES ({sdss_id}, {ra}, {dec}, {active})").on('sdss_id -> sdss_id, 'ra -> ra, 'dec -> dec, 'active -> '1').executeInsert()
    })
    1
  }

  /**
   * Delete a coordinate by id
   *
   * @param Id id of coordinate to delete
   * @return int 1 for errorless 0 for with error
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