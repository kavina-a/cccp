package com.syos.data.dao;

import com.syos.application.reports.data.TotalSaleReportDTO;
import com.syos.domain.entity.BillType;
import com.syos.utils.SQLTransactionManager;
import com.syos.data.dao.interfaces.BillItemDao;
import com.syos.domain.entity.BillItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillItemDaoImpl implements BillItemDao {

    @Override
    public void save(Connection conn, BillItem item) throws SQLException {
        String sql = "INSERT INTO BillItem (itemCode, quantity, price, billId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getProduct().getItemCode());
            stmt.setInt(2, item.getQuantity());
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setInt(4, item.getBill().getId()); // FK to Bill
            stmt.executeUpdate();
        }
    }

    @Override
    public List<BillItem> findByBillId(Connection conn, int billId) throws SQLException {
        String sql = "SELECT * FROM BillItem WHERE billId = ?";
        List<BillItem> items = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BillItem item = new BillItem();
                    item.setId(rs.getLong("id"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setPrice(rs.getBigDecimal("Price"));
                    // Product reference should be resolved separately
                    items.add(item);
                }
            }
        }
        return items;
    }


    @Override
    public List<TotalSaleReportDTO> getTotalSalesByDate(LocalDate date) {
        return SQLTransactionManager.execute(conn -> {
            String sql = """
                        SELECT bi.itemCode, p.itemName, SUM(bi.Quantity), CAST(SUM(bi.Price * bi.Quantity) AS DECIMAL(10,2)) AS TotalRevenue
                        FROM BillItem bi
                        JOIN Bill b ON bi.billId = b.id
                        JOIN Product p ON bi.itemCode = p.itemCode
                        WHERE DATE(b.CreatedAt) = ?
                        GROUP BY bi.itemCode, p.itemName
                        ORDER BY TotalRevenue DESC
                    """;

            List<TotalSaleReportDTO> result = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(date));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(new TotalSaleReportDTO(
                                rs.getString(1),   // itemCode
                                rs.getString(2),   // itemName
                                rs.getInt(3),      // totalQuantity
                                rs.getBigDecimal(4) // totalRevenue
                        ));
                    }
                }
            }
            return result;
        });
    }

    @Override
    public List<TotalSaleReportDTO> getTotalSalesByDateAndType(LocalDate date, BillType type) {
        return SQLTransactionManager.execute(conn -> {
            String sql = """
                        SELECT bi.itemCode, p.itemName, SUM(bi.Quantity), CAST(SUM(bi.Price * bi.Quantity) AS DECIMAL(10,2)) AS TotalRevenue
                        FROM BillItem bi
                        JOIN Bill b ON bi.billId = b.id
                        JOIN Product p ON bi.itemCode = p.itemCode
                        WHERE DATE(b.CreatedAt) = ? AND b.billType = ?
                        GROUP BY bi.itemCode, p.itemName
                        ORDER BY TotalRevenue DESC
                    """;

            List<TotalSaleReportDTO> result = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(date));
                stmt.setString(2, type.name()); // Ensure enum matches DB value
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(new TotalSaleReportDTO(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getBigDecimal(4)
                        ));
                    }
                }
            }
            return result;
        });
    }
}