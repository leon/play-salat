package se.radley.plugin.salat

import org.specs2.mutable.Specification
import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import java.io.File
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Generic._
import org.bson.types.ObjectId
import Formats.objectId

object ObjectIdSpec extends Specification {

  "ObjectId" should {
    "Be bindable in a form" in {
      import play.api.data._
      import play.api.data.Forms._

      val form = Form("id" -> objectId)

      val data = Map("id" -> "4fbc0737985e59dd8120b6d7")
      form.bind(data).get must equalTo(new ObjectId("4fbc0737985e59dd8120b6d7"))
    }
  }
}