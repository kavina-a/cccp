package server.data.dao;

import server.application.reports.data.RestockReportDTO;
import server.application.reports.data.StockReportDTO;
import server.data.dao.interfaces.StoreDao;
import server.domain.entity.StockStatus;
import server.domain.entity.StoreStock;
import server.utils.SQLTransactionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class StoreDaoImpl implements StoreDao {


    @Override
    public void save(Connection conn, StoreStock stock) throws SQLException {
        String sql = "INSERT INTO StoreStock (ItemCode, BatchCode, InitialStock, CurrentStock, ExpiryDate, ReceivedDate, Status, UpdatedBy, UpdatedDateTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stock.getItemCode());
            stmt.setString(2, stock.getBatchCode());
            stmt.setInt(3, stock.getInitialStock());
            stmt.setInt(4, stock.getCurrentStock());
            stmt.setTimestamp(5, Timestamp.valueOf(stock.getExpiryDate()));
            stmt.setTimestamp(6, Timestamp.valueOf(stock.getReceivedDate()));
            stmt.setString(7, stock.getStatus().name());
            stmt.setString(8, stock.getUpdatedBy());
            stmt.setTimestamp(9, Timestamp.valueOf(stock.getUpdatedDateTime()));
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<StoreStock> findByItemCodeAndBatchCode(Connection conn, String itemCode, String batchCode) throws SQLException {
        String sql = "SELECT * FROM StoreStock WHERE ItemCode = ? AND BatchCode = ? AND Status = 'ACTIVE'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            stmt.setString(2, batchCode);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<StoreStock> findByItemCode(Connection conn, String itemCode) throws SQLException {
        String sql = "SELECT * FROM StoreStock WHERE ItemCode = ? AND CurrentStock > 0 AND Status = 'ACTIVE'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                List<StoreStock> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        }
    }

    @Override
    public List<StoreStock> findAvailableStockByItemCode(Connection conn, String itemCode) throws SQLException {
        String sql = "SELECT * FROM StoreStock WHERE ItemCode = ? AND CurrentStock > 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                List<StoreStock> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        }
    }

    @Override
    public void expireBatches(Connection conn) throws SQLException {
        String sql = """
                    UPDATE StoreStock
                    SET status = 'EXPIRED'
                    WHERE expiryDate <= CURRENT_DATE AND status = 'ACTIVE'
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int updatedRows = stmt.executeUpdate();
            System.out.println("Expired " + updatedRows + " batch(es).");
        }
    }

    @Override
    public void updateStockAfterAllocation(Connection conn, String itemCode, String batchCode, int quantityToReduce) throws SQLException {
        Optional<StoreStock> opt = findByItemCodeAndBatchCode(conn, itemCode, batchCode);
        if (opt.isEmpty())
            throw new IllegalStateException("Stock not found for itemCode: " + itemCode + ", batchCode: " + batchCode);

        StoreStock stock = opt.get();
        int updatedStock = stock.getCurrentStock() - quantityToReduce;
        if (updatedStock < 0) throw new IllegalStateException("Negative stock error for itemCode: " + itemCode);

        stock.setCurrentStock(updatedStock);
        if (updatedStock == 0) stock.setStatus(StockStatus.CONSUMED);
        stock.setUpdatedDateTime(LocalDateTime.now());

        String sql = "UPDATE StoreStock SET CurrentStock = ?, Status = ?, UpdatedDateTime = ? WHERE ItemCode = ? AND BatchCode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stock.getCurrentStock());
            stmt.setString(2, stock.getStatus().name());
            stmt.setTimestamp(3, Timestamp.valueOf(stock.getUpdatedDateTime()));
            stmt.setString(4, itemCode);
            stmt.setString(5, batchCode);
            stmt.executeUpdate();
        }
    }

    private StoreStock map(ResultSet rs) throws SQLException {
        StoreStock stock = new StoreStock();
        stock.setItemCode(rs.getString("ItemCode"));
        stock.setBatchCode(rs.getString("BatchCode"));
        stock.setInitialStock(rs.getInt("InitialStock"));
        stock.setCurrentStock(rs.getInt("CurrentStock"));
        stock.setExpiryDate(rs.getTimestamp("ExpiryDate").toLocalDateTime());
        stock.setReceivedDate(rs.getTimestamp("ReceivedDate").toLocalDateTime());
        stock.setStatus(StockStatus.valueOf(rs.getString("Status")));
        stock.setUpdatedBy(rs.getString("UpdatedBy"));
        stock.setUpdatedDateTime(rs.getTimestamp("UpdatedDateTime").toLocalDateTime());
        return stock;
    }

    @Override
    public List<StockReportDTO> fetchBatchStockReport() throws SQLException {
        return SQLTransactionManager.execute(conn -> {
            String sql = """
                        SELECT 
                            si.itemCode,
                            p.itemName,
                            si.batchCode,
                            si.initialStock,
                            si.currentStock,
                            si.receivedDate,
                            si.expiryDate,
                            si.status AS statusTag
                        FROM 
                            StoreStock si
                        JOIN 
                            Product p ON si.itemCode = p.itemCode
                        ORDER BY 
                            si.receivedDate ASC
                    """;

            List<StockReportDTO> result = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    StockReportDTO dto = new StockReportDTO(
                            rs.getString("itemCode"),
                            rs.getString("itemName"),
                            rs.getString("batchCode"),
                            rs.getInt("initialStock"),
                            rs.getInt("currentStock"),
                            rs.getDate("receivedDate").toLocalDate(),
                            rs.getDate("expiryDate").toLocalDate(),
                            rs.getString("statusTag")
                    );
                    result.add(dto);
                }
            }
            return result;
        });
    }

    @Override
    public List<RestockReportDTO> fetchRestockReport() throws SQLException {
        return SQLTransactionManager.execute(conn -> {
            String sql = """
                    SELECT 
                        t.ItemCode,
                        t.ItemName,
                        t.ReorderLevel,
                        t.CurrentStock,
                        (
                            SELECT s2.InitialStock
                            FROM StoreStock s2
                            WHERE s2.ItemCode = t.ItemCode
                            ORDER BY s2.ReceivedDate DESC
                            LIMIT 1
                        ) AS LastRestockedQty,
                        (t.ReorderLevel - t.CurrentStock) AS ToRestock
                    FROM (
                        SELECT 
                            p.ItemCode,
                            p.ItemName,
                            p.ReorderLevel,
                            SUM(ss.CurrentStock) AS CurrentStock
                        FROM 
                            Product p
                        JOIN 
                            StoreStock ss ON p.ItemCode = ss.ItemCode
                        WHERE 
                            p.ReorderLevel IS NOT NULL
                        GROUP BY 
                            p.ItemCode, p.ItemName, p.ReorderLevel
                    ) AS t
                    WHERE 
                        (t.ReorderLevel - t.CurrentStock) > 0
                    ORDER BY 
                        ToRestock DESC
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                List<RestockReportDTO> result = new ArrayList<>();
                while (rs.next()) {
                    RestockReportDTO dto = new RestockReportDTO();
                    dto.setItemCode(rs.getString("ItemCode"));
                    dto.setItemName(rs.getString("ItemName"));
                    dto.setReorderLevel(rs.getInt("ReorderLevel"));
                    dto.setCurrentStock(rs.getInt("CurrentStock"));
                    dto.setLastOrderedQuantity(rs.getInt("LastRestockedQty"));
                    dto.setToRestockQuantity(rs.getInt("ToRestock"));
                    result.add(dto);
                }

                return result;
            }
        });
    }
}
