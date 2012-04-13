package se.radley.plugin.salat

import play.api.mvc._
import java.net.URLDecoder
import org.bson.types.ObjectId

object Binders {

  type ObjectId = org.bson.types.ObjectId

  /**
   * QueryString binder for ObjectId
   */
  implicit def bindableObjectIdQueryString = new QueryStringBindable[ObjectId] {
    def bind(key: String, params: Map[String, Seq[String]]) = params.get(key).flatMap(_.headOption).map { value =>
      if (ObjectId.isValid(value))
        Right(new ObjectId(value))
      else
        Left("Cannot parse parameter " + key + " as ObjectId")
    }
    def unbind(key: String, value: ObjectId) = key + "=" + value.toString
  }

  /**
   * Path binder for ObjectId.
   */
  implicit def bindableObjectIdPath = new PathBindable[ObjectId] {
    def bind(key: String, value: String) = {
      if (ObjectId.isValid(value))
        Right(new ObjectId(value))
      else
        Left("Cannot parse parameter " + key + " as ObjectId")
    }
    def unbind(key: String, value: ObjectId) = value.toString
  }

  /**
   * Convert a ObjectId to a Javascript String
   */
  implicit def litteralObjectId = new JavascriptLitteral[ObjectId] {
    def to(value: ObjectId) = value.toString
  }
}