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
  
  lazy val fakeAppFromURI = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
    additionalConfiguration = Map(
      ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017/salat-test")
    )
  )
  
  lazy val fakeAppFromURIs = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
    additionalConfiguration = Map(
      ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017,mongodb.org:1337/salat-test")
    )
  )
  
  lazy val fakeAppFromURIsWithAuth = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
    additionalConfiguration = Map(
      ("mongodb.default.uri" -> "mongodb://nyancat:ILoveMyKittens@127.0.0.1:27017,mongodb.org:1337,192.168.88.99:27000/salat-test")
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
    
    // tests with a single URI defined
    "start with URI only" in {
      running(fakeAppFromURI) {
        salat must beAnInstanceOf[SalatPlugin]
      }
    }

    "return a MongoCollection with URI only" in {
      running(fakeAppFromURI) {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }
    }
    
    "set replicasets with URI only" in {
      running(fakeAppFromURI) {
        def s = fakeAppFromURI.plugin[SalatPlugin].get
        s must beAnInstanceOf[SalatPlugin]
        val source = s.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017)))
      }
    }
    
    // tests with multiple URIs defined
    "start with URIs only" in {
      running(fakeAppFromURIs) {
        salat must beAnInstanceOf[SalatPlugin]
      }
    }

    "return a MongoCollection with URIs only" in {
      running(fakeAppFromURIs) {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }
    }
    
    "set replicasets with URIs only" in {
      running(fakeAppFromURIs) {
        def s = fakeAppFromURIs.plugin[SalatPlugin].get
        s must beAnInstanceOf[SalatPlugin]
        val source = s.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017), new ServerAddress("mongodb.org", 1337)))
      }
    }
    
    // tests with multiple authenticated URIs defined
    "start with authenticated URIs only" in {
      running(fakeAppFromURIsWithAuth) {
        salat must beAnInstanceOf[SalatPlugin]
      }
    }

    "return a MongoCollection with authenticated URIs only" in {
      running(fakeAppFromURIsWithAuth) {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }
    }
    
    "set replicasets with authenticated URIs only" in {
      running(fakeAppFromURIsWithAuth) {
        def s = fakeAppFromURIsWithAuth.plugin[SalatPlugin].get
        s must beAnInstanceOf[SalatPlugin]
        val source = s.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017),
            new ServerAddress("mongodb.org", 1337),
            new ServerAddress("192.168.88.99", 27000)
        ))
      }
    }
    
    "should propagate authentication credentials" in {
      running(fakeAppFromURIsWithAuth) {
        def s = fakeAppFromURIsWithAuth.plugin[SalatPlugin].get
        s must beAnInstanceOf[SalatPlugin]
        val source = s.source("default")
        source.user must equalTo(Some("nyancat"))
        source.password must equalTo(Some("ILoveMyKittens"))
      }
    }
  }
}
