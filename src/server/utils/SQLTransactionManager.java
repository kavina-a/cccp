package server.utils;

import java.sql.Connection;

public class SQLTransactionManager {

    @FunctionalInterface
    public interface TransactionAction<R> {
        R perform(Connection conn) throws Exception;
    }

    public static <R> R execute(TransactionAction<R> action) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                R result = action.perform(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) throw (RuntimeException) e;
                throw new RuntimeException("Transaction failed", e);
            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) throw (RuntimeException) ex;
            throw new RuntimeException("Connection failed", ex);
        }
    }
}