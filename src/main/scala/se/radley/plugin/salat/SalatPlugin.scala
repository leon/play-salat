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
  
  /**
   * Extracts host as String and port as Int from host string
   * and defaults port to 27017 if it doesn't exist.
   * 
   * E.g. localhost:9999 returns (localhost, 9999)
   * E.g. localhost returns (localhost, 27017)
   */
  def hostAndPort(host: String): (String, Int) = host.contains(':') match {
    case true => {
      val Array(h,p) = host.split(':')
      (h,p.toInt)
    }
    case false => (host, 27017)
  }

  lazy val sources: Map[String, MongoSource] = configuration.subKeys.map { sourceKey =>
    val source = configuration.getConfig(sourceKey).getOrElse(Configuration.empty)
    
    // support MongoURI as per http://www.mongodb.org/display/DOCS/Connections
    val (host, port, user, password, hosts, db) = source.getString("uri").map{ uri => {
      val all = MongoURI(uri)
      val (host,port) = hostAndPort(all.hosts(0))
      val user = all.username match {
        case "null" => None
        case null => None
        case s => Some(s)
      }
      
      val password = all.password match {
        case null => None
        case s => all.password.foldLeft("")(_ + _.toString) match {
          case "" => None
          case s => Some(s)
        }
      }
      
      // to List[ServerAddress]
      val hosts = all.hosts.map(host => {
        val (h,p) = hostAndPort(host)
        new ServerAddress(h, p)
      })
      
      (host, port, user, password, hosts.toList, all.database)
    }}.getOrElse{
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
      
      (host, port, user, password, hosts, db)
    }

    val writeConcern = WriteConcern.valueOf(source.getString("writeconcern", Some(Set("fsyncsafe", "replicassafe", "safe", "normal"))).getOrElse("safe"))

    // If there are replicasets configured go with those otherwise fallback to simple config
    if (hosts.isEmpty)
      sourceKey -> MongoSource(List(new ServerAddress(host, port)), db, writeConcern, user, password)
    else
      sourceKey -> MongoSource(hosts, db, writeConcern, user, password)
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

  def source(source: String): MongoSource = {
    sources.get(source).getOrElse(throw configuration.reportError("mongodb." + source, source + " doesn't exist"))
  }

  def collection(collectionName:String, sourceName:String = "default"): MongoCollection = source(sourceName)(collectionName)
}
