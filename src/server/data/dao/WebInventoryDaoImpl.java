package server.data.dao;

import server.data.dao.interfaces.WebInventoryDao;
import server.domain.entity.ProductStatus;
import server.domain.entity.StockStatus;
import server.domain.entity.WebInventory;
import server.domain.entity.WebProduct;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WebInventoryDaoImpl implements WebInventoryDao {

    @Override
    public void save(Connection conn, WebProduct webProduct) throws SQLException {
        String sql = "INSERT INTO WebProduct (itemCode, itemName, price, status, lastUpdated) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, webProduct.getItemCode());
            stmt.setString(2, webProduct.getItemName());
            stmt.setBigDecimal(3, webProduct.getPrice());
            //            stmt.setInt(4, webProduct.getAvailableQuantity());
            stmt.setString(4, webProduct.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(webProduct.getLastUpdated()));
//            stmt.setString(7, webProduct.getLastUpdatedBy());
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<WebProduct> findByItemCode(Connection conn, String itemCode) throws SQLException {
        String sql = "SELECT * FROM WebProduct WHERE itemCode = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public List<WebProduct> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM WebProduct";
        List<WebProduct> products = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToEntity(rs));
            }
        }
        return products;
    }

    @Override
    public int getAvailableQuantity(Connection conn, String itemCode) throws SQLException {
        String sql = "SELECT SUM(quantityRemaining) AS availableQty " +
                "FROM WebInventory WHERE itemCode = ? AND status = 'ACTIVE'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("availableQty");
                }
            }
        }
        return 0;
    }

    @Override
    public List<WebInventory> findActiveBatches(Connection conn, String itemCode) throws SQLException {
        String sql = "SELECT * FROM WebInventory WHERE itemCode = ? AND status = 'ACTIVE' ORDER BY transferredDate ASC";
        List<WebInventory> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToWebInventory(rs));
                }
            }
        }

        return list;
    }

    @Override
    public void update(Connection conn, WebInventory webInventory) throws SQLException {
        System.out.printf("🔧 Updating WebInventory - ItemCode: %s, BatchCode: %s, New Qty: %d, Status: %s\n",
                webInventory.getItemCode(),
                webInventory.getBatchCode(),
                webInventory.getQuantityRemaining(),
                webInventory.getStatus());

        String sql = "UPDATE WebInventory SET quantityRemaining = ?, status = ? WHERE itemCode = ? AND batchCode = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, webInventory.getQuantityRemaining());
            stmt.setString(2, webInventory.getStatus().name());
            stmt.setString(3, webInventory.getItemCode());
            stmt.setString(4, webInventory.getBatchCode());
            int affectedRows = stmt.executeUpdate();

            System.out.println("✅ Rows affected: " + affectedRows);
        }
    }

    private WebProduct mapResultSetToEntity(ResultSet rs) throws SQLException {
        WebProduct product = new WebProduct();
        product.setItemCode(rs.getString("ItemCode"));
        product.setItemName(rs.getString("ItemName"));
        product.setPrice(rs.getBigDecimal("Price"));
        product.setStatus(ProductStatus.valueOf(rs.getString("Status")));
        product.setLastUpdated(rs.getTimestamp("LastUpdated").toLocalDateTime());
        return product;
    }

    private WebInventory mapResultSetToWebInventory(ResultSet rs) throws SQLException {
        WebInventory wi = new WebInventory();
        wi.setItemCode(rs.getString("itemCode"));
        wi.setBatchCode(rs.getString("batchCode"));
        wi.setQuantityRemaining(rs.getInt("quantityRemaining"));
        wi.setQuantityTransferred(rs.getInt("quantityTransferred"));
        wi.setTransferredDate(rs.getTimestamp("transferredDate").toLocalDateTime());
        wi.setStatus(StockStatus.valueOf(rs.getString("status")));
        // Optional fields
        Timestamp updated = rs.getTimestamp("updatedDateTime");
        if (updated != null) {
            wi.setUpdatedDateTime(updated.toLocalDateTime());
        }

        Object updatedBy = rs.getObject("lastUpdatedBy");
        if (updatedBy != null) {
            wi.setLastUpdatedBy((Integer) updatedBy);
        }

        return wi;
    }


    @Override
    public void logWebRestockTransaction(Connection conn, String itemCode, String batchCode, int quantityRestocked) throws SQLException {
        String sql = """
                    INSERT INTO WebInventory (
                        itemCode, batchCode, quantityRemaining, quantityTransferred,
                        transferredDate, status , updatedDateTime, lastUpdatedBy
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            stmt.setString(2, batchCode);
            stmt.setInt(3, quantityRestocked);
            stmt.setInt(4, quantityRestocked);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(6, StockStatus.ACTIVE.name());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setObject(8, null);
            stmt.executeUpdate();

            System.out.println("✅ Web inventory restock transaction logged.");
        } catch (SQLException e) {
            System.err.println("❌ Error inserting WebInventory: " + e.getMessage());
            throw e;
        }

    }
}