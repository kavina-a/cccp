package com.syos.application.reports.bill.fetch;

import com.syos.application.reports.data.BillReportDTO;
import com.syos.data.dao.interfaces.BillDao;

import java.sql.SQLException;
import java.util.List;

public interface BillFetchStrategy {
    List<BillReportDTO> fetch(BillDao billDao) throws SQLException;
}