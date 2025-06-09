package com.syos.data.dao;

import com.syos.application.dto.LowStockEventDTO;
import com.syos.application.dto.ReshelvedItemDTO;
import com.syos.data.dao.interfaces.ShelfDao;
import com.syos.domain.entity.Shelf;
import com.syos.domain.entity.ShelfInventory;
import com.syos.domain.entity.StockStatus;
import com.syos.utils.SQLTransactionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShelfDaoImpl implements ShelfDao {

    @Override
    public void save(Connection conn, Shelf shelf) throws SQLException {
        String sql = "INSERT INTO Shelf (shelfId, itemCode, quantityOnShelf, location, status, updatedBy, updatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shelf.getShelfId());
            stmt.setString(2, shelf.getItemCode());
            stmt.setInt(3, shelf.getQuantityOnShelf());
            stmt.setString(4, shelf.getLocation());
            stmt.setString(5, shelf.getStatus().name());
            stmt.setString(6, shelf.getUpdatedBy());
            stmt.setObject(7, shelf.getUpdatedAt());
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean exists(Connection conn, String location) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Shelf WHERE location = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, location);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public void logShelfRestockTransaction(Connection conn, String itemCode, String batchCode, int quantity, String shelfId, String updatedBy) throws SQLException {
        String sql = "INSERT INTO ShelfInventory (ItemCode, BatchCode, ShelfId, QuantityTransferred, QuantityRemaining, MovedDate, Status, UpdatedBy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            stmt.setString(2, batchCode);
            stmt.setString(3, shelfId);
            stmt.setInt(4, quantity);
            stmt.setInt(5, quantity);
            stmt.setObject(6, LocalDateTime.now());
            stmt.setString(7, StockStatus.ACTIVE.name());
            stmt.setString(8, updatedBy);
            stmt.executeUpdate();
        }

    }

    @Override
    public List<ShelfInventory> findActiveBatches(Connection conn, String itemCode) throws SQLException {
        String sql = "SELECT * FROM ShelfInventory " +
                "WHERE itemCode = ? AND status = 'ACTIVE'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                List<ShelfInventory> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapResultSetToShelfInventory(rs));
                }
                return result;
            }
        }
    }

    @Override
    public Optional<LowStockEventDTO> getTriggeredLowStockEvent(Connection conn, String itemCode) throws SQLException {
        String sql = """
                    SELECT 
                        p.itemCode,
                        p.itemName,
                        COALESCE(SUM(CASE WHEN si.status = 'ACTIVE' THEN si.quantityRemaining ELSE 0 END), 0) AS currentQuantity
                    FROM 
                        Product p
                    LEFT JOIN 
                        ShelfInventory si ON p.itemCode = si.itemCode
                    WHERE 
                        p.itemCode = ?
                    GROUP BY 
                        p.itemCode, p.itemName, p.reorderLevel
                    HAVING 
                        currentQuantity < p.reorderLevel
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new LowStockEventDTO(
                            rs.getString("itemCode"),
                            rs.getString("itemName"),
                            rs.getInt("currentQuantity")
                    ));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public void update(Connection conn, ShelfInventory batch) throws SQLException {
        String sql = "UPDATE ShelfInventory SET quantityRemaining = ?, status = ? " +
                "WHERE itemCode = ? AND batchCode = ? AND shelfId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, batch.getQuantityRemaining());
            stmt.setString(2, batch.getStatus().name());
            stmt.setString(3, batch.getItemCode());
            stmt.setString(4, batch.getBatchCode());
            stmt.setString(5, batch.getShelfId());
            stmt.executeUpdate();
        }
    }

    private ShelfInventory mapResultSetToShelfInventory(ResultSet rs) throws SQLException {
        ShelfInventory inventory = new ShelfInventory();
        inventory.setItemCode(rs.getString("itemCode"));
        inventory.setBatchCode(rs.getString("batchCode"));
        inventory.setShelfId(rs.getString("shelfId"));
        inventory.setQuantityRemaining(rs.getInt("quantityRemaining"));
        inventory.setStatus(StockStatus.valueOf(rs.getString("status")));
        return inventory;
    }

    @Override
    public List<ReshelvedItemDTO> fetchItemsPendingReshelving() throws SQLException {
        return SQLTransactionManager.execute(conn -> {
            String sql = """
                        SELECT 
                            si.itemCode,
                            p.itemName,
                            sh.quantityOnShelf AS shelfCapacity,
                            SUM(CASE WHEN si.status = 'ACTIVE' THEN si.quantityRemaining ELSE 0 END) AS currentQuantity,
                            SUM(CASE WHEN si.status = 'EXPIRED' THEN si.quantityRemaining ELSE 0 END) AS expiredQuantity,
                            SUM(si.quantityTransferred - si.quantityRemaining) AS soldQuantity,
                            (sh.quantityOnShelf - SUM(CASE WHEN si.status = 'ACTIVE' THEN si.quantityRemaining ELSE 0 END)) AS toBeReshelved,
                            MAX(si.movedDate) AS lastMovedDate
                        FROM 
                            ShelfInventory si
                        JOIN 
                            Shelf sh ON si.shelfId = sh.shelfId
                        JOIN 
                            Product p ON si.itemCode = p.itemCode
                        GROUP BY 
                            si.itemCode, p.itemName, sh.quantityOnShelf
                        HAVING 
                            toBeReshelved > 0
                        ORDER BY 
                            toBeReshelved DESC
                    """;

            List<ReshelvedItemDTO> results = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ReshelvedItemDTO item = new ReshelvedItemDTO();
                    item.setItemCode(rs.getString("itemCode"));
                    item.setItemName(rs.getString("itemName"));
                    item.setShelfCapacity(rs.getInt("shelfCapacity"));
                    item.setCurrentQuantity(rs.getInt("currentQuantity"));
                    item.setExpiredQuantity(rs.getInt("expiredQuantity"));
                    item.setSoldQuantity(rs.getInt("soldQuantity"));
                    item.setToBeReshelved(rs.getInt("toBeReshelved"));
                    item.setLastMovedDate(rs.getDate("lastMovedDate").toLocalDate());

                    results.add(item);
                }
            }

            return results;
        });
    }
}