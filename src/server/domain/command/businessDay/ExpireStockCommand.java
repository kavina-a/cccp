package server.domain.command.businessDay;

import server.application.dto.request.BusinessDayLogRequest;
import server.data.dao.interfaces.StoreDao;
import server.domain.command.businessDay.intefaces.BusinessDayCommand;

import java.sql.Connection;
import java.sql.SQLException;

public class ExpireStockCommand implements BusinessDayCommand {

    private final StoreDao storeDao;

    public ExpireStockCommand(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Override
    public void execute(Connection connection, BusinessDayLogRequest request) throws SQLException {
        storeDao.expireBatches(connection);
        System.out.println(" Expired all batches that reached or passed their expiry date.");
    }
}