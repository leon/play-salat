package se.radley.plugin.salat

import play.api._
import play.api.mvc._
import play.api.Play.current
import com.mongodb.casbah.{WriteConcern, MongoCollection, MongoConnection}

class SalatPlugin(app: Application) extends Plugin {

  lazy val configuration = app.configuration.getConfig("mongodb").getOrElse(Configuration.empty)

  case class MongoSource(
    val host: String,
    val port: Int,
    val db: String,
    val user: Option[String],
    val password: Option[String]
  ){
    def collection(name: String) = {
      val conn = MongoConnection(host, port)(db)
      conn.setWriteConcern(WriteConcern.Safe)

      if (user.isDefined && password.isDefined)
        if (!conn.authenticate(user.getOrElse(""), password.getOrElse("")))
          throw configuration.reportError("mongodb", "Access denied to MongoDB database: [" + db + "] with user: [" + user.getOrElse("") + "]")
      conn(name)
    }

    def apply(name: String) = collection(name)

    override def toString() = (if(user.isDefined) user.get + "@" else "") + host + ":" + port + "/" + db
  }

  val sources: List[Tuple2[MongoSource, String]] = configuration.subKeys.map { source =>
    val db = configuration.getString(source + ".db").getOrElse(throw configuration.reportError("mongodb." + source + ".db", "db missing for source[" + source + "]"))
    val host = configuration.getString(source + ".host").getOrElse("127.0.0.1")
    val port = configuration.getInt(source + ".port").getOrElse(27017)
    val user:Option[String] = configuration.getString(source + ".user")
    val password:Option[String] = configuration.getString(source + ".password")
    MongoSource(host, port, db, user, password) -> source
  }.toList

  override def enabled = !configuration.subKeys.isEmpty

  override def onStart() {
    sources.map { source =>
      app.mode match {
        case Mode.Test =>
        case mode => Logger("play").info("mongodb [" + source._2 + "] connected at " + source._1)
      }
    }
  }

  def source(source: String): MongoSource = {
    sources.filter(_._2 == source).headOption.map(e => e._1).getOrElse(throw configuration.reportError("mongodb." + source, source + " doesn't exist"))
  }

  def collection(collectionName:String, sourceName:String = "default"): MongoCollection = source(sourceName)(collectionName)
}
