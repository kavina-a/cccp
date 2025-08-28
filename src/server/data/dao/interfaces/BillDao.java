package server.data.dao.interfaces;

import server.application.dto.BillReceiptDTO;
import server.application.reports.data.BillReportDTO;
import server.domain.entity.Bill;
import server.domain.entity.BillType;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillDao {

    void save(Connection conn, Bill bill) throws SQLException;

    Optional<Bill> findByBillDateAndSerialNumber(Connection conn, LocalDate billDate, int serialNumber) throws Exception;

    List<BillReceiptDTO> findBillReceipt(Connection conn, LocalDate billDate, int serialNumber) throws Exception;

    int getNextSerialNumber(Connection conn, LocalDate billDate) throws SQLException;

    List<Bill> findByType(Connection conn, BillType transactionType) throws Exception;

    List<Bill> findAll(Connection conn) throws Exception;

    List<BillReportDTO> fetchBillReports() throws SQLException;

    List<BillReportDTO> fetchBillsByType(BillType type) throws SQLException;

}

