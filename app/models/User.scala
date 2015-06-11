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
 * An entry in the product catalogue.
 *
 * @param email User email
 * @param firstname User firstname
 * @param lastname User lastname
 * @param roleId User roleId
 * @param active User active
 * @param password User password
 */
case class User(email: String, firstname: String, lastname: String, roleId: Int, active: Int, password: String)

/**
 * Products data access
 */
object User {

  def findAll(): List[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simpleUser *)
    }
  }

  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users WHERE email = {email}").on('email -> email).as(User.simpleUser.singleOpt)
    }
  }

  def findByEmailAndPassword(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users WHERE email = {email} AND password = {password}")
        .on('email -> email)
        .on('password -> password)
        .as(User.simpleUser.singleOpt)
    }
  }

  val simpleUser = {
      get[String]("email") ~
      get[String]("firstname") ~
      get[String]("lastname") ~
        get[Int]("roleId") ~
        get[Int]("active") ~
      get[String]("password") map {
      case email~firstname~lastname~roleId~active~password => User(email, firstname, lastname, roleId, active, password)
    }
  }

  def insertUser(email: String, lastname: String, firstname: String, roleId: Int, password: String): Int = {
    DB.withConnection( { implicit connection =>
      SQL("INSERT INTO users(`email`, `lastname`, `firstname`, `roleId`, `password`) VALUES ({email}, {lastname}, {firstname}, {roleId}, {password})").on('email -> email, 'lastname -> lastname, 'firstname -> firstname, 'roleId -> roleId, 'password -> password).executeInsert()
    })
    1
  }

  def deleteUser(email: String): Int = {
    DB.withConnection( { implicit connection =>
      val result = SQL("DELETE FROM users WHERE `email` = {email}").on('email -> email).executeUpdate()
    })
    1
  }
/*
  def findAll = this.users.toList.sortBy(_.email)
*/
  /**
   * The product with the given EAN code.
   */
 // def findByEmail(email: String) = this.users.find(_.email == email)


  /**
   * Saves a product to the catalog.

  def save(user: User) = {
    findByEmail(user.email).map( oldUser =>
      this.user = this.user - oldUser + user
    ).getOrElse(
        throw new IllegalArgumentException("User not found")
      )
  } */
}