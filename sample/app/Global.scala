import play.api._
import models._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    if (User.all.count == 0) {
      Logger.info("Loading Testdata")

      User.save(User(
        username = "leon",
        password = "1234",
        address = Some(Address("Örebro", "703 54", "Sweden"))
      ))

      User.save(User(
        username = "guillaume",
        password = "1234",
        address = Some(Address("Paris", "75000", "France"))
      ))
    }
  }

}