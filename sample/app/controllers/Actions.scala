package controllers

import models._
import play.api.mvc._
import play.api.libs.json._

object Actions extends Results with BodyParsers {

  /**
   * Simplifies handling incoming json by wrapping validation and returning BadRequest if it fails
   * @param action the underlying action
   * @tparam A a class that has an implicit Read available
   * @return a response
   */
  def JsonAction[A](action: A => Result)(implicit reader: Reads[A]): EssentialAction = {
    Action(parse.json) { implicit request =>
      request.body.validate[A].fold(
        valid = { json =>
          action(json)
        },
        invalid = (e => BadRequest(JsError.toFlatJson(e)).as("application/json"))
      )
    }
  }
}

