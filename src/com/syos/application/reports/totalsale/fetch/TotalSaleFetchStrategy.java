package com.syos.application.reports.totalsale.fetch;

import com.syos.application.reports.data.TotalSaleReportDTO;

import java.time.LocalDate;
import java.util.List;

public interface TotalSaleFetchStrategy {
    List<TotalSaleReportDTO> fetch(LocalDate date);
}
