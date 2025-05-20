package com.syos.utils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;

/**
 * Manages database transactions using the Template Method pattern.
 * This class provides a reusable transaction boundary that can be used
 * with any database operation.
 */
public class HibernateTransactionManager {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    /**
     * Executes the given database action within a transaction boundary.
     *
     * @param <R> The return type of the database operation (can be any type)
     * @param action A function that accepts a Hibernate Session and returns a result
     * @return The result of the database operation
     * @throws RuntimeException If the transaction fails
     */
    public static <R> R execute(TransactionAction<R> action) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            R result = action.perform(session);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Transaction failed", e);
        }
    }

    @FunctionalInterface
    public interface TransactionAction<R> {
        R perform(Session session);
    }
}