package com.syos.data.dao;

import com.syos.data.dao.interfaces.ProductDao;
import com.syos.domain.entity.Product;
import com.syos.domain.entity.ProductStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDaoImpl implements ProductDao {

    @Override
    public void save(Connection conn, Product product) throws SQLException {

        String sql = "INSERT INTO Product (ItemCode, ItemName, Price, UpdatedDateTime, UpdatedBy, Status, ReorderLevel) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getItemCode());
            stmt.setString(2, product.getItemName());
            stmt.setDouble(3, product.getPrice());
            stmt.setTimestamp(4, Timestamp.valueOf(product.getUpdatedDateTime()));
            stmt.setString(5, product.getUpdatedBy());
            stmt.setString(6, ProductStatus.ACTIVE.name());

            if (product.getReorderLevel() != null) {
                stmt.setInt(7, product.getReorderLevel());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Product> findByItemCode(Connection conn, String itemCode) throws SQLException {
        String sql = "SELECT * FROM Product WHERE ItemCode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToEntity(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Product> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Product";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToEntity(rs));
            }
        }
        return products;
    }

    @Override
    public void update(Connection conn, Product product) throws SQLException {
        String sql = "UPDATE Product SET ItemName = ?, Price = ?, UpdatedDateTime = ?, UpdatedBy = ?, Status = ? WHERE ItemCode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getItemName());
            stmt.setDouble(2, product.getPrice());
            stmt.setTimestamp(3, product.getUpdatedDateTime() != null ? Timestamp.valueOf(product.getUpdatedDateTime()) : null);
            stmt.setString(4, product.getUpdatedBy());
            stmt.setString(5, product.getStatus().name());
            stmt.setString(6, product.getItemCode());
            stmt.executeUpdate();
        }
    }

// TO - DO: Implement soft delete functionality
//    @Override
//    public void deleteByItemCode(Connection conn, String itemCode) throws SQLException {
//        String sql = "UPDATE Product SET Status = ProductStatus.DELETED";
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, itemCode);
//            stmt.executeUpdate();
//        }
//    }

    private Product mapResultSetToEntity(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setItemCode(rs.getString("ItemCode"));
        product.setItemName(rs.getString("ItemName"));
        product.setPrice(rs.getDouble("Price"));
        Timestamp ts = rs.getTimestamp("UpdatedDateTime");
        product.setUpdatedDateTime(ts != null ? ts.toLocalDateTime() : null);
        product.setUpdatedBy(rs.getString("UpdatedBy"));
        product.setStatus(ProductStatus.valueOf(rs.getString("Status")));
        return product;
    }
}
