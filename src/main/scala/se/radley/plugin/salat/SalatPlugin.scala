package se.radley.plugin.salat

import play.api._
import play.api.mvc._
import play.api.Play.current
import com.mongodb.casbah.{WriteConcern, MongoCollection, MongoConnection}

class SalatPlugin(app: Application) extends Plugin {

  lazy val configuration = app.configuration.getConfig("mongodb").getOrElse(Configuration.empty)

  private val sources = configuration.subKeys

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
        if (conn.authenticate(user.getOrElse(""), password.getOrElse("")))
          throw new Exception("Couldn't login to MongoDB database: " + db)
      conn(name)
    }

    override def toString() = {
      if(user.isDefined) user.get + "@" else "" + host + ":" + port + "/" + db
    }
  }

  val datasources: List[Tuple2[MongoSource, String]] = sources.map { source =>
    val host = configuration.getString(source + ".host").getOrElse("127.0.0.1")
    val port = configuration.getInt(source + ".port").getOrElse(27017)
    val db = configuration.getString(source + ".db").getOrElse(sys.error("Missing configuration [mongo." + source + ".db]"))
    val user:Option[String] = configuration.getString(source + ".user")
    val password:Option[String] = configuration.getString(source + ".password")
    MongoSource(host, port, db, user, password) -> source
  }.toList

  private lazy val isDisabled = {
    configuration.subKeys.isEmpty
  }

  override def enabled = isDisabled == false

  override def onStart() {
    datasources.map { source =>
      app.mode match {
        case Mode.Test =>
        case mode => Logger("play").info("mongodb [" + source._2 + "] connected at " + source._1)
      }
    }
  }

  def getSource(name: String): MongoSource = {
    datasources.filter(_._2 == name).headOption.map(e => e._1).getOrElse(sys.error(" - could not find mongosource for " + name))
  }

  def getCollection(collectionName:String, sourceName:String = "default"): MongoCollection = {
    val source = getSource(sourceName)
    source.collection(collectionName)
  }
}
