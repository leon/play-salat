package models

import com.mongodb.casbah.Imports._
import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Address(
  street: String,
  zip: String,
  country: String
)

object Address {
  // Conversions
  implicit val addressJsonWrite = new Writes[Address] {
    def writes(a: Address): JsValue = {
      Json.obj(
        "street" -> a.street,
        "zip" -> a.zip,
        "country" -> a.country
      )
    }
  }

  implicit val addressJsonRead = (
    (__ \ 'street).read[String] ~
    (__ \ 'zip).read[String] ~
    (__ \ 'country).read[String]
  )(Address.apply _)
}
