//package com.clarkparsia.play.imperium;
//
//import play.api.mvc.EssentialAction;
//import play.api.mvc.EssentialFilter;
//import play.api.mvc.Filter;
//import play.api.mvc.RequestHeader;
//import play.mvc.Result;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceException;
//
///**
// * Created with IntelliJ IDEA.
// * User: liam
// * Date: 9/08/13
// * Time: 2:03 PM
// * To change this template use File | Settings | File Templates.
// */
//public class ImperiumFilter extends Filter {
//    public static boolean AUTO_TX = System.getProperty("imperium.autotx") != null ? System.getProperty("imperium.autotx").equalsIgnoreCase("true") : true;
//
//    @Override
//    public Object $init$() {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public EssentialAction apply(EssentialAction next) {
//        startTx();
//        EssentialAction action = next.apply();
//        closeTx(false);
//        return action;
//    }
//
//    /*
//    * Start a transaction
//    */
//    public static void startTx() {
//        if (AUTO_TX) {
//            if (Imperium.em().getTransaction() != null && Imperium.em().getTransaction().isActive()) {
//                Imperium.em().joinTransaction();
//            }
//            else {
//                Imperium.em().getTransaction().begin();
//            }
//        }
//    }
//
//    /**
//     * Close the current transaction
//     * @param theRollback if current transaction be committed (false) or cancelled (true)
//     */
//    public static void closeTx(boolean theRollback) {
//        if (Imperium.get() == null) {
//            return;
//        }
//
//        EntityManager aManager = Imperium.em();
//
//        if (AUTO_TX) {
//            if (aManager.getTransaction().isActive()) {
//                if (theRollback || aManager.getTransaction().getRollbackOnly()) {
//                    aManager.getTransaction().rollback();
//                }
//                else {
//                    try {
//                        if (AUTO_TX) {
//                            aManager.getTransaction().commit();
//                        }
//                    }
//                    catch (Throwable e) {
//                        for (int i = 0; i < 10; i++) {
//                            if (e instanceof PersistenceException && e.getCause() != null) {
//                                e = e.getCause();
//                                break;
//                            }
//
//                            e = e.getCause();
//                            if (e == null) {
//                                break;
//                            }
//                        }
//                        throw new ImperiumException("Cannot commit", "Cannot commit", e);
//                    }
//                }
//            }
//        }
//    }
//}
