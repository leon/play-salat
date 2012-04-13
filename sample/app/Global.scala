import play.api._
import models._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    if (UserDAO.all.count == 0) {
      Logger.info("Loading Testdata")

      UserDAO.save(User(
        username = "leon",
        password = "1234",
        address = Some(Address("Örebro", "703 54", "Sweden"))
      ))

      UserDAO.save(User(
        username = "guillaume",
        password = "1234",
        address = Some(Address("Paris", "75000", "France"))
      ))
    }
  }

}
