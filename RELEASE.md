# v1.0.4

 * Added play classloader to the context. According to some it might help when adding new fields <https://github.com/novus/salat/issues/32#issuecomment-5871621>
 * MongoURI can be used thanks to opyate
 * More specs
 * Replicasets can now be used, see README for more info
 * the underlaying connection is exposed on a MongoSource
 * WriteConcern is exposed in configuration

# v1.0.3

 * Fixed bug in authentication thanks to Dennis Keller
 * toString now works properly

# v1.0.2

 * Simplified plugin alot making it faster
 * Added some specs to test the plugin
 * By implementing ModelCompanion as proposed by Rose Toomey due to the problems outlines here <https://groups.google.com/forum/?fromgroups#!topic/play-framework/75wJs17NKNQ> we were able to get around the class loading problems.

# v1.0.1

 * Better error handling
 * Bugfixes

# v1.0

 * First draft of play plugin.
