package se.radley.plugin.salat

import play.api._
import play.api.mvc._
import play.api.Play.current
import com.mongodb.Mongo
import com.mongodb.casbah.{MongoCollection, MongoConnection}

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

    // Clear all graters from salat to force reloading, see <https://github.com/novus/salat/issues/31>
    ctx.clearAllGraters()

    datasources.map { source =>
      try {
        val m = new Mongo(source._1.host, source._1.port)
        val db = m.getDB(source._1.db)
        if (source._1.user.isDefined && source._1.password.isDefined) {
          val auth = db.authenticate(source._1.user.get, source._1.password.get.toArray);
          if (auth == false)
            throw configuration.reportError(source._2 + ".host", "Authentication failed for [" + source._1 + "]")
        }
        db.getStats()
        app.mode match {
          case Mode.Test =>
          case mode => Logger("salat").info("mongodb [" + source._2 + "] connected at " + source._1)
        }
      } catch {
        case e => {
          throw configuration.reportError(source._2 + ".host", "Cannot connect to database [" + source._2 + "]: " + source._1, Some(e.getCause))
        }
      }
    }
  }

  override def onStop() {
    // @todo do we need to close the salat connection?
  }

  def getSource(name: String): MongoSource = {
    datasources.filter(_._2 == name).headOption.map(e => e._1).getOrElse(sys.error(" - could not find mongosource for " + name))
  }

  def getCollection(collectionName:String, sourceName:String = "default"): MongoCollection = {
    val source = getSource(sourceName)
    MongoConnection(source.host, source.port)(source.db)(collectionName)
  }
}
