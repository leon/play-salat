package se.radley.plugin.salat

import play.api.Configuration
import com.mongodb.casbah.MongoOptions

object OptionsFromConfig {

  def apply(config: Option[Configuration]): Option[com.mongodb.MongoOptions] = {
    if (config.isDefined && config.get.keys.isEmpty) None
    else config.map { implicit conf =>
      val defaults = new com.mongodb.MongoOptions
      MongoOptions(
        autoConnectRetry = ("autoConnectRetry", defaults.autoConnectRetry),
        connectionsPerHost = ("connectionsPerHost", defaults.connectionsPerHost),
        threadsAllowedToBlockForConnectionMultiplier = ("threadsAllowedToBlockForConnectionMultiplier", defaults.threadsAllowedToBlockForConnectionMultiplier),
        maxWaitTime = ("maxWaitTime", defaults.maxWaitTime),
        connectTimeout = ("connectTimeout", defaults.connectTimeout),
        socketTimeout = ("socketTimeout", defaults.socketTimeout),
        socketKeepAlive = ("socketKeepAlive", defaults.socketKeepAlive),
        maxAutoConnectRetryTime = ("maxAutoConnectRetryTime", defaults.maxAutoConnectRetryTime),
        slaveOk = ("slaveOk", defaults.slaveOk),
        safe = ("safe", defaults.safe),
        w = ("w", defaults.w),
        wTimeout = ("wtimeout", defaults.wtimeout),
        fsync = ("fsync", defaults.fsync),
        j = ("j", defaults.j),
        dbDecoderFactory = ("dbDecoderFactory", defaults.dbDecoderFactory),
        dbEncoderFactory = ("dbEncoderFactory", defaults.dbEncoderFactory),
        //socketFactory = ("socketFactory", defaults.socketFactory), FIXME Dependency problem
        description = ("description", defaults.description))
    }
  }

  implicit def getBoolean(prop: (String, Boolean))(implicit conf: Configuration): Boolean = conf.getBoolean(prop._1) getOrElse prop._2
  implicit def getString(prop: (String, String))(implicit conf: Configuration): String = conf.getString(prop._1) getOrElse prop._2
  implicit def getInt(prop: (String, Int))(implicit conf: Configuration): Int = conf.getInt(prop._1) getOrElse prop._2
  implicit def getLong(prop: (String, Long))(implicit conf: Configuration): Long = conf.getMilliseconds(prop._1) getOrElse prop._2
  implicit def getFactory[F](prop: (String, F))(implicit conf: Configuration): F = {
    conf.getString(prop._1).map { name => Class.forName(name).newInstance().asInstanceOf[F] } getOrElse prop._2
  }

}