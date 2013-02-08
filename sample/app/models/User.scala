package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

case class User(
  id: ObjectId = new ObjectId,
  username: String,
  password: String,
  address: Option[Address] = None,
  added: Date = new Date(),
  updated: Option[Date] = None,
  @Key("company_id")company: Option[ObjectId] = None
)

object User extends UserDAO with UserJson

trait UserDAO extends ModelCompanion[User, ObjectId] {
  def collection = mongoCollection("users")
  val dao = new SalatDAO[User, ObjectId](collection) {}

  // Indexes
  collection.ensureIndex(DBObject("username" -> 1), "user_email", unique = true)

  // Queries
  def findOneByUsername(username: String): Option[User] = dao.findOne(MongoDBObject("username" -> username))
  def findByCountry(country: String) = dao.find(MongoDBObject("address.country" -> country))
  def authenticate(username: String, password: String): Option[User] = findOne(DBObject("username" -> username, "password" -> password))
}

/**
 * Trait used to convert to and from json
 */
trait UserJson {

  implicit val userJsonWrite = new Writes[User] {
    def writes(u: User): JsValue = {
      Json.obj(
        "id" -> u.id,
        "username" -> u.username,
        "address" -> u.address,
        "added" -> u.added,
        "updated" -> u.updated
      )
    }
  }
  implicit val userJsonRead = (
    (__ \ 'id).read[ObjectId] ~
    (__ \ 'username).read[String] ~
    (__ \ 'password).read[String] ~
    (__ \ 'address).readNullable[Address] ~
    (__ \ 'added).read[Date] ~
    (__ \ 'updated).readNullable[Date] ~
    (__ \ 'company).readNullable[ObjectId]
  )(User.apply _)
}
