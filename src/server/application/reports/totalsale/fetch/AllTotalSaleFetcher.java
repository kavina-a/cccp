package server.application.reports.totalsale.fetch;

import server.application.reports.data.TotalSaleReportDTO;
import server.data.dao.interfaces.BillItemDao;

import java.time.LocalDate;
import java.util.List;

public class AllTotalSaleFetcher implements TotalSaleFetchStrategy {

    private final BillItemDao billItemDao;

    public AllTotalSaleFetcher(BillItemDao billItemDao) {
        this.billItemDao = billItemDao;
    }

    @Override
    public List<TotalSaleReportDTO> fetch(LocalDate date) {
        return billItemDao.getTotalSalesByDate(date);

    }

}

