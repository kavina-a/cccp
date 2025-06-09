package com.syos.application.reports.restock.fetch;

import com.syos.application.reports.data.RestockReportDTO;

import java.sql.SQLException;
import java.util.List;

public interface RestockFetchStrategy {
    List<RestockReportDTO> fetch() throws SQLException;
}