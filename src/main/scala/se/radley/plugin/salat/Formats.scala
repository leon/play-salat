package se.radley.plugin.salat

import play.api.data._
import play.api.data.format._
import play.api.data.format.Formats._
import play.api.data.Forms._
import com.mongodb.casbah.Imports._

object Formats {
  /**
   * Formatter for the `ObjectId` type.
   */
  implicit def objectIdFormatter: Formatter[ObjectId] = new Formatter[ObjectId] {

    def bind(key: String, data: Map[String, String]) = {
      stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[ObjectId]
          .either(new ObjectId(s))
          .left.map(e => Seq(FormError(key, "error.objectid", Nil)))
      }
    }

    def unbind(key: String, value: ObjectId) = Map(key -> value.toString)
  }

  /**
   * Constructs a mapping for a objectId field.
   *
   * For example:
   * {{{
   * Form("id" -> objectId)
   * }}}
   */
  val objectId: Mapping[ObjectId] = of[ObjectId]
}
