package server.data.dao.interfaces;

import server.domain.entity.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

public interface ProductDao {
    void save(Connection conn, Product product) throws SQLException;
    Optional<Product> findByItemCode(Connection conn, String itemCode) throws SQLException;
    List<Product> findAll(Connection conn) throws SQLException;
    void update(Connection conn, Product product) throws SQLException;
//    void deleteByItemCode(Connection conn, String itemCode) throws SQLException;
}


