package server.application.reports.restock.fetch;

import server.application.reports.data.RestockReportDTO;
import server.data.dao.interfaces.StoreDao;

import java.sql.SQLException;
import java.util.List;

public class StoreRestockFetchStrategy implements RestockFetchStrategy {

    private final StoreDao storeDao;

    public StoreRestockFetchStrategy(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Override
    public List<RestockReportDTO> fetch() throws SQLException {
        return storeDao.fetchRestockReport();
    }
}