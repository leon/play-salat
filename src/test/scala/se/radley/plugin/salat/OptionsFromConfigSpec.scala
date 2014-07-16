package se.radley.plugin.salat

import org.specs2.mutable.Specification
import com.mongodb._
import play.api.Configuration
import javax.net.SocketFactory
import org.specs2.specification.AllExpectations
import sun.security.ssl.SSLSocketFactoryImpl

class OptionsFromConfigSpec extends Specification with AllExpectations {

  "OptionsFromConfig" should {
    "Override all defaults when all props are present" in {
      val allNonDefaultConfiguration = Map(
        ("mongodb.default.options.autoConnectRetry" -> "true"),
        ("mongodb.default.options.connectionsPerHost" -> "333"),
        ("mongodb.default.options.connectTimeout" -> "34000"),
        ("mongodb.default.options.cursorFinalizerEnabled" -> "true"),
        ("mongodb.default.options.dbDecoderFactory" -> "se.radley.plugin.salat.NonDefaultDBDecoderFactory"),
        ("mongodb.default.options.dbEncoderFactory" -> "se.radley.plugin.salat.NonDefaultDBEncoderFactory"),
        ("mongodb.default.options.description" -> "Some Description"),
        ("mongodb.default.options.maxAutoConnectRetryTime" -> "20"),
        ("mongodb.default.options.maxWaitTime" -> "68000"),
        ("mongodb.default.options.readPreference" -> "PRIMARY"),
        ("mongodb.default.options.socketFactory" -> "se.radley.plugin.salat.NonDefaultSocketFactory"),
        ("mongodb.default.options.socketKeepAlive" -> "true"),
        ("mongodb.default.options.socketTimeout" -> "21000"),
        ("mongodb.default.options.threadsAllowedToBlockForConnectionMultiplier" -> "22"),
        ("mongodb.default.options.writeConcern" -> "SAFE")
      )

      val configuration = Configuration.from(allNonDefaultConfiguration).getConfig("mongodb.default.options").get
      val optionsOpt = OptionsFromConfig(configuration)
      optionsOpt must beSome
      val options = optionsOpt.get
      // All Overridden
      options.isAutoConnectRetry must beTrue
      options.getConnectionsPerHost must be equalTo(333)
      options.getConnectTimeout must be equalTo(34000)
      options.isCursorFinalizerEnabled must beTrue
      options.getDbDecoderFactory must haveClass[NonDefaultDBDecoderFactory]
      options.getDbEncoderFactory must haveClass[NonDefaultDBEncoderFactory]
      options.getDescription must be equalTo("Some Description")
      options.getMaxAutoConnectRetryTime must be equalTo(20)
      options.getMaxWaitTime must be equalTo(68000)
      options.getReadPreference must be equalTo(ReadPreference.primary())
      options.getSocketFactory must haveClass[NonDefaultSocketFactory]
      options.isSocketKeepAlive must beTrue
      options.getSocketTimeout must be equalTo(21000)
      options.getThreadsAllowedToBlockForConnectionMultiplier must be equalTo(22)
      options.getWriteConcern must be equalTo(WriteConcern.SAFE)
    }

    "Return none options if config is empty" in {
      val options = OptionsFromConfig(Configuration.empty)
      options must beNone
    }

    "Set SSL factory is ssl = true" in {
      val conf = Map(
        ("mongodb.default.options.ssl" -> "true")
      )

      val configuration = Configuration.from(conf).getConfig("mongodb.default.options").get
      val optionsOpt = OptionsFromConfig(configuration)

      optionsOpt must beSome
      val options = optionsOpt.get

      options.getSocketFactory must haveClass[SSLSocketFactoryImpl]
    }
  }
  
}

class NonDefaultDBDecoderFactory extends DBDecoderFactory {
	def create() = null
}
class NonDefaultDBEncoderFactory extends DBEncoderFactory {
	def create() = null
}
class NonDefaultSocketFactory extends SocketFactory {
	def createSocket(host: String, port: Int) = null
  def createSocket(address: java.net.InetAddress, port: Int) = null
  def createSocket(host: String, port: Int, clientAddress: java.net.InetAddress, clientPort: Int) = null
  def createSocket(address: java.net.InetAddress, port: Int, clientAddress: java.net.InetAddress, clientPort: Int) = null
}