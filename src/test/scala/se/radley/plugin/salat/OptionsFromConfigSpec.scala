package se.radley.plugin.salat

import org.specs2.mutable.Specification
import com.mongodb.MongoOptions
import play.api.Configuration
import com.mongodb.DBDecoderFactory
import com.mongodb.DBEncoderFactory
import javax.net.SocketFactory
import com.mongodb.DBDecoder
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.AllExpectations
import scala.reflect.ClassManifest

class OptionsFromConfigSpec extends SpecificationWithJUnit with AllExpectations {

  "OptionsFromConfig" should {
    "Override all defaults when all props are present" in {
      val allNonDefaultConfiguration = Map(
        ("mongodb.default.options.autoConnectRetry" -> "true"),
        ("mongodb.default.options.connectionsPerHost" -> "333"),
        ("mongodb.default.options.threadsAllowedToBlockForConnectionMultiplier" -> "22"),
        ("mongodb.default.options.maxWaitTime" -> "68000"),
        ("mongodb.default.options.connectTimeout" -> "34000"),
        ("mongodb.default.options.socketTimeout" -> "21000"),
        ("mongodb.default.options.socketKeepAlive" -> "true"),
        ("mongodb.default.options.maxAutoConnectRetryTime" -> "20"),
        ("mongodb.default.options.slaveOk" -> "true"),
        ("mongodb.default.options.safe" -> "true"),
        ("mongodb.default.options.w" -> "1"),
        ("mongodb.default.options.wtimeout" -> "10"),
        ("mongodb.default.options.fsync" -> "true"),
        ("mongodb.default.options.j" -> "true"),
        ("mongodb.default.options.dbDecoderFactory" -> "se.radley.plugin.salat.NonDefaultDBDecoderFactory"),
        ("mongodb.default.options.dbEncoderFactory" -> "se.radley.plugin.salat.NonDefaultDBEncoderFactory"),
        ("mongodb.default.options.socketFactory" -> "se.radley.plugin.salat.NonDefaultDBSocketFactory"),
        ("mongodb.default.options.description" -> "Some Description"))

      val sourceConfig = Configuration.from(allNonDefaultConfiguration).getConfig("mongodb.default").get
      val optionsConfig = sourceConfig.getConfig("options")
      val optionsOpt = OptionsFromConfig(optionsConfig)
      optionsOpt must beSome
      val options = optionsOpt.get
      // All Overridden
      options.autoConnectRetry must beTrue
      options.connectionsPerHost must be equalTo(333)
      options.threadsAllowedToBlockForConnectionMultiplier must be equalTo(22)
      options.maxWaitTime must be equalTo(68000)
      options.connectTimeout must be equalTo(34000)
      options.socketTimeout must be equalTo(21000)
      options.socketKeepAlive must beTrue
      options.maxAutoConnectRetryTime must be equalTo(20)
      options.slaveOk must beTrue
      options.safe must beTrue
      options.w must be equalTo(1)
      options.wtimeout must be equalTo(10)
      options.fsync must beTrue
      options.j must beTrue
      options.description must be equalTo("Some Description")
      options.dbDecoderFactory must haveClass[NonDefaultDBDecoderFactory]
      options.dbEncoderFactory must haveClass[NonDefaultDBEncoderFactory]
      //options.socketFactory must haveClass[NonDefaultSocketFactory] FIXME Dependency problem
    }

    "Override some defaults for present props" in {
      val someNonDefaultConfiguration = Map(
        ("mongodb.default.options.connectionsPerHost" -> "255"),
        ("mongodb.default.options.threadsAllowedToBlockForConnectionMultiplier" -> "24"),
        ("mongodb.default.options.connectTimeout" -> "60000"))

      val defaultOptions = new MongoOptions

      val sourceConfig = Configuration.from(someNonDefaultConfiguration).getConfig("mongodb.default").get
      val optionsConfig = sourceConfig.getConfig("options")
      val optionsOpt = OptionsFromConfig(optionsConfig)
      optionsOpt must beSome
      val options = optionsOpt.get
      // Overridden
      options.connectionsPerHost must be equalTo(255)
      options.threadsAllowedToBlockForConnectionMultiplier must be equalTo(24)
      options.connectTimeout must be equalTo(60000)
      // Remain defaults
      options.autoConnectRetry must beFalse
      options.maxWaitTime must be equalTo(1000 * 60 * 2)
      options.socketTimeout must be equalTo(0)
      options.socketKeepAlive must beFalse
      options.maxAutoConnectRetryTime must be equalTo(0)
      options.slaveOk must beFalse
      options.safe must beFalse
      options.w must be equalTo(0)
      options.wtimeout must be equalTo(0)
      options.fsync must beFalse
      options.j must beFalse
      options.description must beNull
      options.dbDecoderFactory must be(defaultOptions.dbDecoderFactory)
      options.dbEncoderFactory must be(defaultOptions.dbEncoderFactory)
      options.socketFactory must be(defaultOptions.socketFactory)
    }

    "Return none options if config is not defined" in {
      val undefinedConfig = None
      val options = OptionsFromConfig(undefinedConfig)
      options must beNone
    }

    "Return none options if config is empty" in {
      val emptyConfig = Some(Configuration.empty)
      val options = OptionsFromConfig(emptyConfig)
      options must beNone
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