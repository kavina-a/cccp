package server.application.reports.bill.fetch;

import server.application.reports.data.BillReportDTO;
import server.data.dao.interfaces.BillDao;
import server.domain.entity.BillType;

import java.sql.SQLException;
import java.util.List;

public class OTCBillFetcher implements BillFetchStrategy {

    @Override
    public List<BillReportDTO> fetch(BillDao billDao) throws SQLException {
        return billDao.fetchBillsByType(BillType.OTC_BILL);
    }
}