import javax.persistence.{PersistenceException, EntityManager}
import play.api.mvc._
import com.clarkparsia.play.imperium._

object Global extends WithFilters(ImperiumFilter)

object ImperiumFilter extends Filter {
  var AUTO_TX = if (System.getProperty("imperium.autotx") != null) System.getProperty("imperium.autotx").equalsIgnoreCase("true") else true

  override def apply(next: RequestHeader => Result)(request: RequestHeader): Result = {
    startTx()
    val result = next(request)
    closeTx(false)
    result
  }

  def startTx() {
    play.Logger.info("start tx " + AUTO_TX)
    if (AUTO_TX) {
      if (Imperium.em().getTransaction() != null && Imperium.em().getTransaction().isActive()) {
        Imperium.em().joinTransaction();
      }
      else {
        Imperium.em().getTransaction().begin();
      }
    }
  }

  def closeTx(theRollback: Boolean) {
    play.Logger.info("close tx " + theRollback)
    if (Imperium.get() == null) {
      return
    }

    val aManager = Imperium.em();

    if (AUTO_TX) {
      if (aManager.getTransaction().isActive()) {
        if (theRollback || aManager.getTransaction().getRollbackOnly()) {
          aManager.getTransaction().rollback();
        }
        else {
          try {
            if (AUTO_TX) {
              aManager.getTransaction().commit();
            }
          }
          catch  {
            case e : Throwable =>
              throw new ImperiumException("Cannot commit", "Cannot commit", e)
          }
        }
      }
    }

  }
}
