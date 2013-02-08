package controllers.api

import play.api.mvc._
import play.api.libs.json._
import models._
import controllers.Actions._

import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._

object Users extends Controller {

  def index() = Action {
    val users = User.findAll().toList
    Ok(Json.toJson(users))
  }

  def create = JsonAction[User] { user =>
    User.save(user, WriteConcern.Safe)
    Ok(Json.toJson(user))
  }

  def view(id: ObjectId) = Action {
    User.findOneById(id).map { user =>
      Ok(Json.toJson(user))
    } getOrElse {
      NotFound
    }
  }

  def update(id: ObjectId) = JsonAction[User] { requestUser =>
    val user = requestUser.copy(id)
    User.save(user, WriteConcern.Safe)
    Ok(Json.toJson(user))
  }

  def delete(id: ObjectId) = Action {
    User.removeById(id)
    Ok("")
  }
}
