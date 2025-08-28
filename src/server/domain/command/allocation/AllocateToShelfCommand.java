package server.domain.command.allocation;

import server.application.dto.AllocatedRestockDTO;
import server.application.dto.request.AllocationRequest;
import server.data.dao.interfaces.ShelfDao;
import server.data.dao.interfaces.StoreDao;
import server.domain.command.allocation.interfaces.AllocationCommand;
import server.domain.entity.StoreStock;
import server.domain.strategy.stockselection.interfaces.StockSelectionStrategy;
import server.utils.SessionManager;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AllocateToShelfCommand implements AllocationCommand {

    private final AllocationRequest request;
    private final StockSelectionStrategy strategy;
    private final StoreDao storeDao;
    private final ShelfDao shelfDao;


    public AllocateToShelfCommand(
            AllocationRequest request,
            StockSelectionStrategy strategy,
            StoreDao storeDao,
            ShelfDao shelfDao
    ) {
        this.request = request;
        this.strategy = strategy;
        this.storeDao = storeDao;
        this.shelfDao = shelfDao;
    }

    @Override
    public List<AllocatedRestockDTO> execute(Connection conn) throws SQLException {

        List<StoreStock> availableBatches = storeDao.findByItemCode(conn, request.getItemCode());
        List<AllocatedRestockDTO> selectedAllocations = strategy.allocateStock(availableBatches, request.getRequiredQuantity());

        String updatedBy = SessionManager.getInstance().getLoggedInEmployee().getEmployeeID();

        for (AllocatedRestockDTO allocation : selectedAllocations) {
            storeDao.updateStockAfterAllocation(conn, allocation.getItemCode(), allocation.getBatchCode(), allocation.getAllocatedQuantity());

            if (request.requiresShelfId()) {
                shelfDao.logShelfRestockTransaction(
                        conn,
                        allocation.getItemCode(),
                        allocation.getBatchCode(),
                        allocation.getAllocatedQuantity(),
                        request.getShelfId(),
                        updatedBy
                );
            }
        }
        return selectedAllocations;
    }
}