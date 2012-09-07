package se.radley.plugin

import play.api.{Play, Application, PlayException}
import play.api.Play.current
import com.mongodb.casbah.MongoCollection

package object salat {

  /**
   * get the underlying salat MongoCollection
   * @param collectionName The MongoDB collection name
   * @param sourceName The configured source name
   * @return MongoCollection
   */
  def mongoCollection(collectionName: String, sourceName:String = "default")(implicit app: Application): MongoCollection = {
    app.plugin[SalatPlugin].map(_.collection(collectionName, sourceName)).getOrElse(throw PlayException("SalatPlugin is not registered.", "You need to register the plugin with \"500:se.radley.plugin.salat.SalatPlugin\" in conf/play.plugins"))
  }

  /**
   * Returns a capped MongoCollection
   * @param collectionName The MongoDB collection name
   * @param size The capped collection size
   * @param max the capped collection max number of documents
   * @param sourceName The configured source name
   * @return MongoCollection
   */
  def mongoCappedCollection(collectionName: String, size: Int, max: Option[Int] = None, sourceName:String = "default")(implicit app: Application): MongoCollection = {
    app.plugin[SalatPlugin].map(_.cappedCollection(collectionName, size, max, sourceName)).getOrElse(throw PlayException("SalatPlugin is not registered.", "You need to register the plugin with \"500:se.radley.plugin.salat.SalatPlugin\" in conf/play.plugins"))
  }
}
