//package com.syos.utils;
//
//import org.hibernate.Transaction;
//import org.hibernate.SessionFactory;
//
///**
// * Manages database transactions using the Template Method pattern.
// * This class provides a reusable transaction boundary that can be used
// * with any database operation.
// */
//
////public class HibernateTransactionManager {
////
////    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
////    /**
////     * Executes the given database action within a transaction boundary.
////     *
////     * @param <R> The return type of the database operation (can be any type)
////     * @param action A function that accepts a Hibernate Session and returns a result
////     * @return The result of the database operation
////     * @throws RuntimeException If the transaction fails
////     */
////    public static <R> R execute(TransactionAction<R> action) {
////        Transaction tx = null;
////        try (Session session = sessionFactory.openSession()) {
////            tx = session.beginTransaction();
////            R result = action.perform(session);
////            tx.commit();
////            return result;
////        } catch (Exception e) {
////            if (tx != null) tx.rollback();
////            throw new RuntimeException("Transaction failed", e);
////        }
////    }
////
////    @FunctionalInterface
////    public interface TransactionAction<R> {
////        R perform(Session session);
////    }
////}
//
//public class HibernateTransactionManager {
//    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//    private static final ThreadLocal<Session> contextSession = new ThreadLocal<>();
//
//    public static <R> R execute(TransactionAction<R> action) {
//        Session session = contextSession.get();
//        boolean isOuterTransaction = (session == null);
//
//        Transaction tx = null;
//        try {
//            if (isOuterTransaction) {
//                session = sessionFactory.openSession();
//                contextSession.set(session);
//                tx = session.beginTransaction();
//            }
//
//            R result = action.perform(session);
//
//            if (isOuterTransaction) {
//                tx.commit();
//            }
//
//            return result;
//        } catch (Exception e) {
//            if (tx != null) tx.rollback();
//            throw new RuntimeException("Transaction failed", e);
//        } finally {
//            if (isOuterTransaction) {
//                contextSession.remove();
//                session.close();
//            }
//        }
//    }
//
//    @FunctionalInterface
//    public interface TransactionAction<R> {
//        R perform(Session session);
//    }
//}
//
////
////public class HibernateTransactionManager {
////
////    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
////    private static final ThreadLocal<Session> contextSession = new ThreadLocal<>();
////
////    public static <R> R execute(TransactionAction<R> action) {
////        Session session = contextSession.get();
////        boolean isOuterTransaction = (session == null);
////
////        Transaction tx = null;
////        try {
////            if (isOuterTransaction) {
////                session = sessionFactory.openSession();
////                contextSession.set(session);
////                tx = session.beginTransaction();
////            }
////
////            R result = action.perform(session);
////
////            if (isOuterTransaction) {
////                tx.commit();
////            }
////
////            return result;
////
////        } catch (Exception e) {
////            if (tx != null) tx.rollback();
////            throw new RuntimeException("Transaction failed", e);
////
////        } finally {
////            if (isOuterTransaction) {
////                contextSession.remove();
////                session.close();
////            }
////        }
////    }
////
////    @FunctionalInterface
////    public interface TransactionAction<R> {
////        R perform(Session session);
////    }
////}