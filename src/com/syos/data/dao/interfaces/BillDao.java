package com.syos.data.dao.interfaces;

import com.syos.application.dto.BillReceiptDTO;
import com.syos.application.reports.data.BillReportDTO;
import com.syos.domain.entity.Bill;
import com.syos.domain.entity.BillType;

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

