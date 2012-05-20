# MongoDB Salat plugin for Play Framework 2 (Scala only)
Salat is a ORM for MongoDBs scala driver called Casbah.

 * https://github.com/mongodb/casbah
 * https://github.com/novus/salat

[![Build Status](https://secure.travis-ci.org/leon/play-salat.png)](http://travis-ci.org/leon/play-salat)

## Installation

Use g8 to start a new salat enabled play project

### Install g8 on OSX using homebrew
    
    brew update && brew install giter8

Or read about the other ways to install [giter8 here](https://github.com/n8han/giter8)

Then run

    g8 leon/play-salat.g8

It will ask you a couple of questions, and your ready to rock 'n roll.

### Manual installation
Start by adding the plugin, in your `project/Build.scala`

    val appDependencies = Seq(
      "se.radley" %% "play-plugins-salat" % "1.0.3"
    )

Then we can add the implicit converstions to and from ObjectId by adding to the routesImport and add ObjectId to all the templates

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId"
    )

We now need to register the plugin, this is done by creating(or appending) to the `conf/play.plugins` file

    500:se.radley.plugin.salat.SalatPlugin

We continue to edit the `conf/application.conf` file. We need to disable some plugins that we don't need.
Add these lines:

    dbplugin = disabled
    evolutionplugin = disabled
    ehcacheplugin = disabled

## Configuration
now we need to setup our connections. The plugin is modeled after how plays DB plugin is built.

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

Then when you call `mongoCollection("collectionname", "myotherdb")` you specify the name of the source

Check out the [sample directory](https://github.com/leon/play-salat/tree/master/sample) and the [wiki](https://github.com/leon/play-salat/wiki)
