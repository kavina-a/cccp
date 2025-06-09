package com.syos.application.reports.totalsale.fetch;

import com.syos.application.reports.data.TotalSaleReportDTO;
import com.syos.data.dao.interfaces.BillItemDao;
import com.syos.domain.entity.BillType;

import java.time.LocalDate;
import java.util.List;

public class WEBTotalSaleFetcher implements TotalSaleFetchStrategy {

    private final BillItemDao billItemDao;

    public WEBTotalSaleFetcher(BillItemDao billItemDao) {
        this.billItemDao = billItemDao;
    }

    @Override
    public List<TotalSaleReportDTO> fetch(LocalDate date) {
        return billItemDao.getTotalSalesByDateAndType(date, BillType.WEB_BILL);
    }
}