import com.clarkparsia.play.imperium.Imperium;
import com.clarkparsia.play.imperium.ImperiumException;
import play.*;
import play.mvc.Action;
import play.mvc.Http.Request;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

public class Global extends GlobalSettings {

    static boolean AUTO_TX = (System.getProperty("imperium.autotx") != null) ? System.getProperty("imperium.autotx").equalsIgnoreCase("true") : true;

    @Override
    public Action onRequest(Request request, Method actionMethod) {
        startTx();
        Action action = super.onRequest(request, actionMethod);
        closeTx(true);
        return super.onRequest(request, actionMethod);
    }

    public void startTx() {
        play.Logger.info("start tx " + AUTO_TX);
        if (AUTO_TX) {
            if (Imperium.em().getTransaction() != null && Imperium.em().getTransaction().isActive()) {
                Imperium.em().joinTransaction();
            }
            else {
                Imperium.em().getTransaction().begin();
            }
        }
    }

    public void closeTx(boolean theRollback) {
        play.Logger.info("close tx " + theRollback);
        if (Imperium.get() == null) {
            return;
        }

        EntityManager aManager = Imperium.em();

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
                    catch (Throwable t)  {
                        throw new ImperiumException("Cannot commit", "Cannot commit", t);
                    }
                }
            }
        }

    }


}