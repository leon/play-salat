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

  lazy val fakeApp = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
    additionalConfiguration = Map(
      ("mongodb.default.db" -> "salat-test"),
      ("mongodb.default.writeconcern" -> "normal")
    )
  )

  lazy val fakeReplicaSetApp = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
    additionalConfiguration = Map(
      ("mongodb.default.db" -> "salat-test"),
      ("mongodb.default.replicaset.host1.host" -> "10.0.0.1"),
      ("mongodb.default.replicaset.host1.port" -> "27018"),
      ("mongodb.default.replicaset.host2.host" -> "10.0.0.2")
    )
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

    "set write concern" in {
      running(fakeApp) {
        val source = salat.source("default")
        source.writeConcern must equalTo(WriteConcern.Normal)
      }
    }

    "set replicasets" in {
      running(fakeReplicaSetApp) {
        def s = fakeReplicaSetApp.plugin[SalatPlugin].get
        s must beAnInstanceOf[SalatPlugin]
        val source = s.source("default")
        source.hosts must equalTo(List(new ServerAddress("10.0.0.1", 27018), new ServerAddress("10.0.0.2", 27017)))
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
