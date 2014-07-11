package se.radley.plugin.salat

import play.api.Configuration
import com.mongodb.casbah.MongoOptions
import com.mongodb._
import javax.net.ssl.SSLSocketFactory
import scala.Some
import javax.net.SocketFactory

object OptionsFromConfig {

  private def getInstanceFromName[T](name: String): Option[T] = {
    try {
      Some(Class.forName(name).newInstance().asInstanceOf[T])
    } catch {
      case ex: ClassNotFoundException => None
    }
  }

  def apply(config: Configuration): Option[com.mongodb.MongoClientOptions] = {
    if (config.keys.isEmpty) {
      return None
    }

    val builder = new MongoClientOptions.Builder()

    config.getBoolean("autoConnectRetry").map(v => builder.autoConnectRetry(v))
    config.getInt("connectionsPerHost").map(v => builder.connectionsPerHost(v))
    config.getInt("connectTimeout").map(v => builder.connectTimeout(v))
    config.getBoolean("cursorFinalizerEnabled").map(v => builder.cursorFinalizerEnabled(v))
    config.getString("dbDecoderFactory").flatMap(className => getInstanceFromName[DBDecoderFactory](className)).map(v => builder.dbDecoderFactory(v))
    config.getString("dbEncoderFactory").flatMap(className => getInstanceFromName[DBEncoderFactory](className)).map(v => builder.dbEncoderFactory(v))
    config.getString("description").map(v => builder.description(v))
    config.getLong("maxAutoConnectRetryTime").map(v => builder.maxAutoConnectRetryTime(v))
    config.getInt("maxWaitTime").map(v => builder.maxWaitTime(v))
    config.getString("readPreference").flatMap { name =>
      try {
        Some(ReadPreference.valueOf(name))
      } catch {
        case ex: IllegalArgumentException => None
      }
    }.map(v => builder.readPreference(v))
    config.getString("socketFactory").flatMap(className => getInstanceFromName[SocketFactory](className)).map(v => builder.socketFactory(v))
    config.getBoolean("socketKeepAlive").map(v => builder.socketKeepAlive(v))
    config.getInt("socketTimeout").map(v => builder.socketTimeout(v))
    config.getInt("threadsAllowedToBlockForConnectionMultiplier").map(v => builder.threadsAllowedToBlockForConnectionMultiplier(v))
    config.getString("writeConcern").map(name => WriteConcern.valueOf(name)).map(v => builder.writeConcern(v))
    config.getBoolean("ssl").map(v => if (v) builder.socketFactory(SSLSocketFactory.getDefault()))

    Some(builder.build())
  }
}