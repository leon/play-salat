package se.radley.plugin

import play.api._
import play.api.Play.current
import com.mongodb.casbah.MongoCollection

package object mongodb {
  def getCollection(collectionName: String, sourceName:String = "default")(implicit app: Application): MongoCollection = {
    app.plugin[MongoDBPlugin].map(_.getCollection(collectionName, sourceName)).getOrElse(throw new Exception("Salat plugin is not registered."))
  }
}

