package se.radley.plugin

import play.api._
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.gridfs.GridFS
import play.api.Play.current

package object salat {

  /**
   * Returns a MongoCollection
   * @param collectionName The MongoDB collection name
   * @param sourceName The configured source name
   * @return MongoCollection
   */
  def mongoCollection(collectionName: String, sourceName:String = "default")(implicit app: Application): MongoCollection = {
    app.plugin[SalatPlugin].map(_.collection(collectionName, sourceName)).getOrElse(throw new PlayException("SalatPlugin is not registered.", "You need to register the plugin with \"500:se.radley.plugin.salat.SalatPlugin\" in conf/play.plugins"))
  }

  /**
   * Returns a capped MongoCollection
   * @param collectionName The MongoDB collection name
   * @param size The capped collection size
   * @param max the capped collection max number of documents
   * @param sourceName The configured source name
   * @return MongoCollection
   */
  def mongoCappedCollection(collectionName: String, size: Long, max: Option[Long] = None, sourceName:String = "default")(implicit app: Application): MongoCollection = {
    app.plugin[SalatPlugin].map(_.cappedCollection(collectionName, size, max, sourceName)).getOrElse(throw new PlayException("SalatPlugin is not registered.", "You need to register the plugin with \"500:se.radley.plugin.salat.SalatPlugin\" in conf/play.plugins"))
  }

  /**
   * Returns a GridFS bucket
   * @param bucketName The GridFS bucket name
   * @param sourceName The configured source name
   * @return GridFS
   */
  def gridFS(bucketName: String, sourceName:String = "default")(implicit app: Application): GridFS = {
    app.plugin[SalatPlugin].map(_.gridFS(bucketName, sourceName)).getOrElse(throw new PlayException("SalatPlugin is not registered.", "You need to register the plugin with \"500:se.radley.plugin.salat.SalatPlugin\" in conf/play.plugins"))
  }

}
