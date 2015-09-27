package models
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

import scala.language.postfixOps

/**
 * This model saves the contact's of the users
 *
 * @param name The name of the user
 * @param phone The phonenumber of the user
 * @param email The email adress of the user
 * @param message The message of the user
 * @param uuid The uuid to see which user posted it
 */
case class Contact(name: String, email: String, phone: String, message: String, uuid: String)

/**
 * This Model is used to get data from the contact model
 *
 * @author David Pinezich <david.pinezich@uzh.ch>
 * @version 1.0.0
 */
object Contact {

  /**
   * Find all Contacts
   *
   * @return List[Contact] List of all saved contacts
   */
  def findAll(): List[Contact] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM contacts").as(Contact.simpleContact *)
    }
  }

  /**
   * Inserts a new contact mail
   *
   * @param name The name of the contact
   * @param phone The phone number of the contact
   * @param message The message given
   * @param uuid The uuid of the user
   *
   * @return Returns the id of the insert
   */
  def insertContact(name: String, email: String, phone: String, message: String, uuid: String): Option[Long] = {
    val uuidUser = User.getUuidByEncryptedString(uuid)
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("INSERT INTO contacts(`name`, `email`, `phone`, `message`, `uuid`) VALUES ({name}, {email}, {phone}, {message}, {uuid})")
        .on('name -> name)
        .on('email -> email)
        .on('phone -> phone)
        .on('message -> message)
        .on('uuid -> uuidUser)
        .executeInsert()
    }
    id
  }

  /**
   * Contact Structure
   */
  val simpleContact = {
      get[String]("name") ~
        get[String]("email") ~
      get[String]("phone") ~
      get[String]("message") ~
      get[String]("uuid") map {
      case name ~ email ~ phone ~ message ~ uuid => Contact(name, email, phone, message, uuid)
    }
  }

}