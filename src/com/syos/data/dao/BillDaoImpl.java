package com.syos.data.dao;

import com.syos.application.dto.BillReceiptDTO;
import com.syos.application.reports.data.BillReportDTO;
import com.syos.data.dao.interfaces.BillDao;
import com.syos.domain.entity.Bill;
import com.syos.domain.entity.BillType;
import com.syos.utils.SQLTransactionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillDaoImpl implements BillDao {

    @Override
    public void save(Connection conn, Bill bill) throws SQLException {
        String sql = "INSERT INTO Bill (serialNumber, billDate, totalAmount, cashTendered, changeAmount, createdAt, billType, customerID, paymentMethod) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bill.getSerialNumber());
            stmt.setDate(2, Date.valueOf(bill.getBillDate()));
            stmt.setBigDecimal(3, bill.getTotalAmount());
            stmt.setBigDecimal(4, bill.getCashTendered());
            stmt.setBigDecimal(5, bill.getChangeAmount());
            stmt.setTimestamp(6, Timestamp.valueOf(bill.getCreatedAt()));
            stmt.setString(7, bill.getBillType().name());
            stmt.setString(8, bill.getCustomer() != null ? bill.getCustomer().getCustomerID() : "WALK-IN");
            stmt.setString(9, bill.getPaymentMethod().name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    bill.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<BillReceiptDTO> findBillReceipt(Connection conn, LocalDate billDate, int serialNumber) throws SQLException {
        String sql = """
                    SELECT
                      b.serialNumber,
                      b.billDate,
                      b.totalAmount,
                      b.cashTendered,
                      b.changeAmount,
                      i.itemCode,
                      p.itemName,
                      i.quantity,
                      i.price,
                      (i.quantity * i.price) AS totalItemPrice
                    FROM Bill b
                    JOIN BillItem i ON i.billId = b.id
                    JOIN Product p ON p.itemCode = i.itemCode
                    WHERE b.billDate = ? AND b.serialNumber = ?
                """;

        List<BillReceiptDTO> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(billDate));
            stmt.setInt(2, serialNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BillReceiptDTO dto = new BillReceiptDTO();
                    dto.setSerialNumber(rs.getInt("serialNumber"));
                    dto.setBillDate(rs.getDate("billDate").toLocalDate());
                    dto.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    dto.setCashTendered(rs.getBigDecimal("cashTendered"));
                    dto.setChangeAmount(rs.getBigDecimal("changeAmount"));
                    dto.setItemCode(rs.getString("itemCode"));
                    dto.setItemName(rs.getString("itemName"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setPrice(rs.getBigDecimal("price"));
                    dto.setTotalItemPrice(rs.getBigDecimal("totalItemPrice"));
                    result.add(dto);
                }
            }
        }
        return result;
    }


    @Override
    public Optional<Bill> findByBillDateAndSerialNumber(Connection conn, LocalDate billDate, int serialNumber) throws SQLException {
        String sql = "SELECT * FROM Bill WHERE billDate = ? AND serialNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(billDate));
            stmt.setInt(2, serialNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill();
                    bill.setId(rs.getInt("id"));
                    bill.setSerialNumber(rs.getInt("serialNumber"));
                    bill.setBillDate(rs.getDate("billDate").toLocalDate());
                    bill.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    bill.setCashTendered(rs.getBigDecimal("cashTendered"));
                    bill.setChangeAmount(rs.getBigDecimal("changeAmount"));
                    bill.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                    bill.setBillType(BillType.valueOf(rs.getString("billType")));
                    return Optional.of(bill);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public int getNextSerialNumber(Connection conn, LocalDate billDate) throws SQLException {
        String sql = "SELECT MAX(serialNumber) FROM Bill WHERE billDate = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(billDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) + 1;
                }
            }
        }
        return 1;
    }

    @Override
    public List<Bill> findByType(Connection conn, BillType billType) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM Bill WHERE billType = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billType.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill();
                    bill.setId(rs.getInt("id"));
                    bill.setSerialNumber(rs.getInt("serialNumber"));
                    bill.setBillDate(rs.getDate("billDate").toLocalDate());
                    bill.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    bill.setBillType(BillType.valueOf(rs.getString("billType")));
                    bills.add(bill);
                }
            }
        }
        return bills;
    }

    @Override
    public List<Bill> findAll(Connection conn) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM Bill";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill();
                    bill.setId(rs.getInt("id"));
                    bill.setSerialNumber(rs.getInt("serialNumber"));
                    bill.setBillDate(rs.getDate("billDate").toLocalDate());
                    bill.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    bill.setBillType(BillType.valueOf(rs.getString("billType")));
                    bills.add(bill);
                }
            }
        }
        return bills;
    }

    @Override
    public List<BillReportDTO> fetchBillReports() throws SQLException {
        return SQLTransactionManager.execute(conn -> {
            String sql = """
                        SELECT 
                            b.serialNumber,
                            b.billDate,
                            b.customerID,
                            b.billType AS transactionType,
                            SUM(bi.quantity) AS totalItems,
                            b.totalAmount,
                            b.cashTendered,
                            b.changeAmount
                        FROM 
                            Bill b
                        LEFT JOIN 
                            BillItem bi ON b.id = bi.billId
                        GROUP BY 
                            b.id
                    
                    """;

            List<BillReportDTO> result = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String billIdentifier = rs.getInt("serialNumber") + "-" + rs.getDate("billDate").toLocalDate();
                    result.add(new BillReportDTO(
                            billIdentifier,
                            rs.getString("customerID"),
                            rs.getString("transactionType"),
                            rs.getInt("totalItems"),
                            rs.getBigDecimal("totalAmount"),
                            rs.getBigDecimal("cashTendered"),
                            rs.getBigDecimal("changeAmount")
                    ));
                }
            }

            return result;
        });
    }

    @Override
    public List<BillReportDTO> fetchBillsByType(BillType type) throws SQLException {
        return SQLTransactionManager.execute(conn -> {
            String sql = """
                        SELECT 
                            b.serialNumber,
                            b.billDate,
                            b.customerID,
                            b.billType AS transactionType,
                            SUM(bi.quantity) AS totalItems,
                            b.totalAmount,
                            b.cashTendered,
                            b.changeAmount
                        FROM 
                            Bill b
                        LEFT JOIN 
                            BillItem bi ON b.id = bi.billId
                        WHERE 
                            b.billType = ?
                        GROUP BY 
                            b.id
                    """;

            List<BillReportDTO> result = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, type.name());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String billIdentifier = rs.getInt("serialNumber") + "-" + rs.getDate("billDate").toLocalDate();
                        result.add(new BillReportDTO(
                                billIdentifier,
                                rs.getString("customerID"),
                                rs.getString("transactionType"),
                                rs.getInt("totalItems"),
                                rs.getBigDecimal("totalAmount"),
                                rs.getBigDecimal("cashTendered"),
                                rs.getBigDecimal("changeAmount")
                        ));
                    }
                }
            }

            return result;
        });
    }
}

