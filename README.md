# MongoDB(Casbah) / Salat Plugin for Play Framework 2 (Scala only)
This plugin is BETA and may or may not work properly, I just need some more time to test it properly...

By wrapping https://github.com/mongodb/casbah and https://github.com/novus/salat play now works great with MongoDB

## Installation
Start by adding the plugin, in your `project/Build.scala`

    val appDependencies = Seq(
      "se.radley" %% "play-plugins-mongodb" % "1.0-SNAPSHOT"
    )

Then we need to add a resolver so that `sbt` knows where to get it from

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "MongoDB Plugin Repo" at "Havn't got my OSS repo yet.."
    )

Then we continue to the `conf/application.conf` file. We need to disable some plugins that we don't need.
Add these lines:

    dbplugin = disabled
    evolutionplugin = disabled
    ehcacheplugin = disabled

## Configuration
now we need to setup our connections. The plugin is modeled after how the (DB / JDBC) plugin in play works.

    mongodb.default.db = "mydb"
    # Optional values
    #mongodb.default.host = "127.0.0.1"
    #mongodb.default.port = 27017
    #mongodb.default.user = "leon"
    #mongodb.default.password = "123456"

As you can see above the default is to only specify a db name, the plugin will then try to connect to `127.0.0.1:27017` using no username or password.

## Connecting to more than one db (not necessarily on the same port/ip)
If you would like to connect to two databases you need to create two names

    mongodb.myotherdb.db = "otherdb"

Then when you call `getCollection("collectionname", "myotherdb")` you specify the name of the source

Check out the [sample directory](https://github.com/leon/play-mongodb/tree/master/sample) and the [wiki](https://github.com/leon/play-mongodb/wiki)