package com.syos.application.reports.bill.fetch;

import com.syos.application.reports.data.BillReportDTO;
import com.syos.data.dao.interfaces.BillDao;
import com.syos.domain.entity.BillType;

import java.sql.SQLException;
import java.util.List;

public class OTCBillFetcher implements BillFetchStrategy {

    @Override
    public List<BillReportDTO> fetch(BillDao billDao) throws SQLException {
        return billDao.fetchBillsByType(BillType.OTC_BILL);
    }
}