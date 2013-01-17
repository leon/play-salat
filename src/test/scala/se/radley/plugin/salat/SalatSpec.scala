package se.radley.plugin.salat

import org.specs2.mutable.Specification
import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import java.io.File
import play.api.Play.current
import com.mongodb.casbah._
import com.mongodb.ServerAddress

object SalatSpec extends Specification {

  lazy val salatApp = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin")
  )

  "Salat Plugin with basic config" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.writeconcern" -> "normal")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "set write concern" in {
        val source = salat.source("default")
        source.writeConcern must equalTo(WriteConcern.Normal)
      }

      "fail if source doesn't exist" in {
        salat.collection("salat-collection", "sourcethatdoesntexist") must throwAn[PlayException]
      }
    }

    "be disabled if no configuration exists" in {
      val app = FakeApplication(additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"))
      running(app) {
        app.plugin[SalatPlugin] must beNone
      }
    }
  }

  "Salat Plugin with uri config" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017/salat-test")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "populate hosts from URI" in {
        salat must beAnInstanceOf[SalatPlugin]
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017)))
      }

      /*
      // @todo if we need to test username and password we need to use an embedded mongo instance
      "populate username and password from URI" in {
        salat must beAnInstanceOf[SalatPlugin]
        val source = salat.source("default")
        source.user must equalTo(Some("leon"))
        source.password must equalTo(Some("password"))
      }*/
    }
  }

  "Salat Plugin with multiple uri config" should {
    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017,mongodb.org:1337/salat-test")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "populate hosts with multiple URIs" in {
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017), new ServerAddress("mongodb.org", 1337)))
      }
    }
  }

  "Salat Plugin with replicaset config" should {

    lazy val app = FakeApplication(
      additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.replicaset.host1.host" -> "10.0.0.1"),
        ("mongodb.default.replicaset.host2.host" -> "10.0.0.2"),
        ("mongodb.default.replicaset.host2.port" -> "27018")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "populate hosts from config" in {
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("10.0.0.1", 27017), new ServerAddress("10.0.0.2", 27018)))
      }
    }
  }
}
