package com.syos.application.reports.reshelf.fetch;

import com.syos.application.reports.data.ReshelfReportDTO;
import com.syos.data.dao.interfaces.ShelfDao;
import com.syos.data.dao.interfaces.WebInventoryDao;

import java.sql.SQLException;
import java.util.List;

public class ShelfReshelfFetcher implements ReshelfFetchStrategy {

    private final ShelfDao shelfDao;

    public ShelfReshelfFetcher(ShelfDao shelfDao) {
        this.shelfDao = shelfDao;
    }

    @Override
    public List<ReshelfReportDTO> fetch() throws SQLException {
        return shelfDao.fetchItemsPendingReshelving().stream()
                .map(entry -> new ReshelfReportDTO(
                        entry.getItemCode(),
                        entry.getItemName(),
                        entry.getShelfCapacity(),
                        entry.getCurrentQuantity(),
                        entry.getExpiredQuantity(),
                        entry.getSoldQuantity(),
                        entry.getToBeReshelved(),
                        entry.getLastMovedDate().toString()
                ))
                .toList();
    }
}