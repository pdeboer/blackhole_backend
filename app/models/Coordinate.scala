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
* @param ra Coordinates ra
* @param dec Coordinates dec
* @param active Coordinates active
*/
case class Coordinate(id: Int, ra: String, dec: String, active: Int)

object Coordinate {

  def findAll(): List[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM coordinates").as(Coordinate.simpleCoordinates *)
    }
  }

  def findById(id: Int): Option[Coordinate] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM coordinates WHERE id = {id}").on('id -> id).as(Coordinate.simpleCoordinates.singleOpt)
    }
  }

  def findByRand(): Option[Coordinate] = {
  DB.withConnection { implicit connection =>
    SQL("SELECT * FROM coordinates ORDER BY RAND() LIMIT 1").as(Coordinate.simpleCoordinates.singleOpt)
  }
}

  def insertCoordinate(ra: String, dec: String): Int = {
    DB.withConnection( { implicit connection =>
      SQL("INSERT INTO coordinates(`ra`, `dec`, `active`) VALUES ({ra}, {dec}, {active})").on('ra -> ra, 'dec -> dec, 'active -> '1').executeInsert()
    })
    1
  }

  def deleteCoordinate(Id: Int): Int = {
    DB.withConnection( { implicit connection =>
      val result = SQL("DELETE FROM coordinates WHERE `Id` = {id}").on('id -> Id).executeUpdate()
    })
    1
  }

  /*
  val id: Int = SQL("insert into City(name, country) values ({name}, {country}")
    .on("Cambridge", "New Zealand").executeInsert()
*/

  val simpleCoordinates = {
    get[Int]("id")~
      get[String]("ra") ~
      get[String]("dec") ~
      get[Int]("active") map {
      case id~ra~dec~active => Coordinate(id, ra, dec, active)
    }
  }

}