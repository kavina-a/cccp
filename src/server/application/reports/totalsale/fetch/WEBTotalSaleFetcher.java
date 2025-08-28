package server.application.reports.totalsale.fetch;

import server.application.reports.data.TotalSaleReportDTO;
import server.data.dao.interfaces.BillItemDao;
import server.domain.entity.BillType;

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