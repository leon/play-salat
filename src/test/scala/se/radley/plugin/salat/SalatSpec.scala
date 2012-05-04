package se.radley.plugin.salat

import org.specs2.mutable.Specification
import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import java.io.File
import play.api.Play.current
import com.mongodb.casbah._

object SalatSpec extends Specification {

  lazy val fakeApp = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
    additionalConfiguration = Map(("mongodb.default.db" -> "salat-test"))
  )

  def salat = fakeApp.plugin[SalatPlugin].get

  "Salat Plugin" should {

    "start" in {
      running(fakeApp) {
        salat must beAnInstanceOf[SalatPlugin]
      }
    }
    "return a MongoCollection" in {
      running(fakeApp) {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }
    }

    // Failures
    "be disabled if no configuration exists" in {
      val noConfFakeApp = FakeApplication(additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"))
      running(noConfFakeApp) {
        noConfFakeApp.plugin[SalatPlugin] must equalTo(None)
      }
    }

    "fail if source doesn't exist" in {
      running(fakeApp) {
        salat.collection("salat-collection", "sourcethatdoesntexist") must throwAn[PlayException]
      }
    }
  }
}
