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
 */
case class User(email: String, firstname: String, lastname: String, roleId: Int, password: String)

/**
 * Products data access
 */
object User {


  var users = Set(
    User("david.pinezich@gmail.com", "David", "Pinezich", 1, "test"),
    User("david.pinezich@gmail.com2", "David2", "Pinezich2", 1, "test1")
  )



  /**
   * Products sorted by EAN code.
   */
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
      get[String]("password") map {
      case email~firstname~lastname~roleId~password => User(email, firstname, lastname, roleId, password)
    }
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
   */
  def save(user: User) = {
    findByEmail(user.email).map( oldUser =>
      this.users = this.users - oldUser + user
    ).getOrElse(
        throw new IllegalArgumentException("User not found")
      )
  }
}