package se.radley.plugin.salat

import play.api._
import play.api.mvc._
import play.api.Play.current
import com.mongodb.casbah.{WriteConcern, MongoCollection, MongoConnection, MongoURI}
import com.mongodb.ServerAddress

class SalatPlugin(app: Application) extends Plugin {

  lazy val configuration = app.configuration.getConfig("mongodb").getOrElse(Configuration.empty)

  case class MongoSource(
    val hosts: List[ServerAddress],
    val db: String,
    val writeConcern: com.mongodb.WriteConcern,
    val user: Option[String] = None,
    val password: Option[String] = None
  ){
    def connection = {
      val c = MongoConnection(hosts)(db)
      c.setWriteConcern(writeConcern)
      c
    }

    def collection(name: String) = {
      val conn = connection;
      if (user.isDefined && password.isDefined)
        if (!conn.authenticate(user.getOrElse(""), password.getOrElse("")))
          throw configuration.reportError("mongodb", "Access denied to MongoDB database: [" + db + "] with user: [" + user.getOrElse("") + "]")
      conn(name)
    }

    def apply(name: String) = collection(name)

    override def toString() = {
      (if (user.isDefined) user.get + "@" else "") +
      hosts.map(h => h.getHost + ":" + h.getPort).mkString(", ") +
      "/" + db
    }
  }

  lazy val sources: Map[String, MongoSource] = configuration.subKeys.map { sourceKey =>
    val source = configuration.getConfig(sourceKey).getOrElse(Configuration.empty)

    source.getString("uri").map { str =>
      // MongoURI config - http://www.mongodb.org/display/DOCS/Connections
      val uri = MongoURI(str)
      val hosts = uri.hosts.map { host =>
        if (host.contains(':')) {
          val Array(h, p) = host.split(':')
          new ServerAddress(h, p.toInt)
        } else {
          new ServerAddress(host)
        }
      }.toList
      val db = uri.database
      val writeConcern = uri.options.getWriteConcern
      val user = Option(uri.username).filterNot(_.isEmpty)
      val password = Option(uri.password).map(_.mkString).filterNot(_.isEmpty)
      sourceKey -> MongoSource(hosts, db, writeConcern, user, password)
    }.getOrElse {
      val db = source.getString("db").getOrElse(throw configuration.reportError("mongodb." + sourceKey + ".db", "db missing for source[" + sourceKey + "]"))

      // Simple config
      val host = source.getString("host").getOrElse("127.0.0.1")
      val port = source.getInt("port").getOrElse(27017)
      val user:Option[String] = source.getString("user")
      val password:Option[String] = source.getString("password")

      // Replica set config
      val hosts: List[ServerAddress] = source.getConfig("replicaset").map { replicaset =>
        replicaset.subKeys.map { hostKey =>
          val c = replicaset.getConfig(hostKey).get
          val host = c.getString("host").getOrElse(throw configuration.reportError("mongodb." + sourceKey + ".replicaset", "host missing for replicaset in source[" + sourceKey + "]"))
          val port = c.getInt("port").getOrElse(27017)
          new ServerAddress(host, port)
        }.toList
      }.getOrElse(List.empty)

      val writeConcern = WriteConcern.valueOf(source.getString("writeconcern", Some(Set("fsyncsafe", "replicassafe", "safe", "normal"))).getOrElse("safe"))

      // If there are replicasets configured go with those otherwise fallback to simple config
      if (hosts.isEmpty)
        sourceKey -> MongoSource(List(new ServerAddress(host, port)), db, writeConcern, user, password)
      else
        sourceKey -> MongoSource(hosts, db, writeConcern, user, password)
    }
  }.toMap

  override def enabled = !configuration.subKeys.isEmpty

  override def onStart() {
    sources.map { source =>
      app.mode match {
        case Mode.Test =>
        case mode => Logger("play").info("mongodb [" + source._1 + "] connected at " + source._2)
      }
    }
  }

  /**
   * Returns the MongoSource that has been configured in application.conf
   * @param source The source name ex. default
   * @return A MongoSource
   */
  def source(source: String): MongoSource = {
    sources.get(source).getOrElse(throw configuration.reportError("mongodb." + source, source + " doesn't exist"))
  }

  /**
   * Returns MongoCollection that has been configured in application.conf
   * @param collectionName The MongoDB collection name
   * @param sourceName The source name ex. default
   * @return A MongoCollection
   */
  def collection(collectionName:String, sourceName:String = "default"): MongoCollection = source(sourceName)(collectionName)
}
