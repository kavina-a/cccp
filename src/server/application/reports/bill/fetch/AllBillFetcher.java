package server.application.reports.bill.fetch;

import server.application.reports.data.BillReportDTO;
import server.data.dao.interfaces.BillDao;

import java.sql.SQLException;
import java.util.List;

public class AllBillFetcher implements BillFetchStrategy {

    @Override
    public List<BillReportDTO> fetch(BillDao billDao) throws SQLException {
        return billDao.fetchBillReports();
    }
}