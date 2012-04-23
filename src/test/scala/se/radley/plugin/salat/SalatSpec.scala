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
    /*
    "render template" in {
      running(fakeApp) {
        val result = velocity.render("simple.vm")(FakeRequest())
        result must beAnInstanceOf[String]
        result must equalTo("Hello")
      }
    }
    "render template with variable" in {
      running(fakeApp) {
        val result = velocity.render("hello.vm", Map(
          "name" -> "Leon"
        ))(FakeRequest())
        result must equalTo("Hello Leon")
      }
    }
    "render template with global variable" in {
      running(fakeApp) {
        velocity.addGlobal("name", "Leon")
        val result = velocity.render("hello.vm")(FakeRequest())
        result must equalTo("Hello Leon")
      }
    }
    "render template as html" in {
      running(fakeApp) {
        val result = velocity.html("simple.vm")(FakeRequest())
        result must beAnInstanceOf[Html]
        result.body must equalTo("Hello")
      }
    }
    "render template as txt" in {
      running(fakeApp) {
        val result = velocity.txt("simple.vm")(FakeRequest())
        result must beAnInstanceOf[Txt]
        result.body must equalTo("Hello")
      }
    }
    "render template as xml" in {
      running(fakeApp) {
        val result = velocity.xml("xml.vm")(FakeRequest())
        result must beAnInstanceOf[Xml]
        result.body must equalTo("""<?xml version="1.0" encoding="UTF-8"?><body>Hello</body>""")
      }
    }

    // Failures
    "fail when template doesn't exist" in {
      running(fakeApp) {
        velocity.render("does-not-exist.vm")(FakeRequest()) must throwAn[TemplateNotFoundException]
      }
    }
    "fail when rendering template with variable that doesn't exist" in {
      running(fakeApp) {
        velocity.render("fail-variable.vm")(FakeRequest()) must throwAn[TemplateMethodInvocationException]
      }
    }
    "fail when rendering template with invalid syntax" in {
      running(fakeApp) {
        velocity.render("fail-syntax.vm")(FakeRequest()) must throwAn[TemplateParseException]
      }
    }
    */
  }
}
