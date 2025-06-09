package com.syos.data.dao.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.syos.application.reports.data.TotalSaleReportDTO;
import com.syos.domain.entity.BillItem;
import com.syos.domain.entity.BillType;

public interface BillItemDao {

    void save(Connection connection, BillItem item) throws SQLException;

    List<BillItem> findByBillId(Connection connection, int billId) throws SQLException;

    List<TotalSaleReportDTO> getTotalSalesByDate(LocalDate date);
    List<TotalSaleReportDTO> getTotalSalesByDateAndType(LocalDate date, BillType type);
}
