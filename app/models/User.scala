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
 * @param uuid User uuid
 */
case class User(email: String, firstname: String, lastname: String, roleId: Int, active: Int, password: String, uuid: String)

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
      SQL("select * from users WHERE email = {email} AND password = {password} AND active = 1")
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
      get[String]("password")~
        get[String]("uuid") map {
      case email~firstname~lastname~roleId~active~password~uuid => User(email, firstname, lastname, roleId, active, password, uuid)
    }
  }

  def insertUser(email: String, lastname: String, firstname: String, roleId: Int, password: String): Int = {
    val uuid = java.util.UUID.randomUUID.toString //@ToDo test for double random uuids
    DB.withConnection( { implicit connection =>
      SQL("INSERT INTO users(`email`, `lastname`, `firstname`, `roleId`, `password`, `uuid`) VALUES ({email}, {lastname}, {firstname}, {roleId}, {password}, {uuid})").on('email -> email, 'lastname -> lastname, 'firstname -> firstname, 'roleId -> roleId, 'password -> password, 'uuid -> uuid).executeInsert()
    })
    1
  }

  def deleteUser(email: String): Int = {
    DB.withConnection( { implicit connection =>
      val result = SQL("DELETE FROM users WHERE `email` = {email}").on('email -> email).executeUpdate()
    })
    1
  }

  def changeActiveUser(email: String): Int = {
    DB.withConnection( { implicit connection =>
      val checkState = SQL("SELECT active as active FROM users WHERE `email` = {email}").on('email -> email).apply().head
      val oldState = checkState[Int]("active")
      var newState = 1
      if(oldState == 1) {
        newState = 0
      }
      val result = SQL("UPDATE users SET `active` = {active} WHERE `email` = {email}")
        .on('active -> newState)
        .on('email -> email)
        .executeUpdate()
    })
    1
  }

}