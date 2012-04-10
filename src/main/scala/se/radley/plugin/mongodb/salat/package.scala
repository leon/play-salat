package se.radley.plugin.mongodb

package object salat {

  type Salat = com.novus.salat.annotations.Salat
  type EnumAs = com.novus.salat.annotations.EnumAs
  type Ignore = com.novus.salat.annotations.Ignore
  type Key = com.novus.salat.annotations.Key
  type Persist = com.novus.salat.annotations.Persist
  type SalatDAO[ObjectType <: AnyRef, ID <: Any] = com.novus.salat.dao.SalatDAO[ObjectType, ID]

  implicit val ctx = {
    import play.api._
    import play.api.Play.current
    import com.novus.salat.Context

    val context = new Context {
      val name = "play-context"
    }
    context.registerClassLoader(Play.classloader)
    context.registerGlobalKeyOverride(remapThis = "id", toThisInstead = "_id")
    context
  }
}
