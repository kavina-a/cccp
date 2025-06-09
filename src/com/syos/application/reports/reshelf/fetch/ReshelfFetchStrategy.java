package com.syos.application.reports.reshelf.fetch;

import com.syos.application.reports.data.ReshelfReportDTO;

import java.sql.SQLException;
import java.util.List;

public interface ReshelfFetchStrategy {
    List<ReshelfReportDTO> fetch() throws SQLException;
}