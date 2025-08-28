package server.domain.service;

import server.application.dto.AllocatedRestockDTO;
import server.application.dto.request.AllocationRequest;
import server.data.dao.interfaces.ShelfDao;
import server.data.dao.interfaces.StoreDao;
import server.data.dao.interfaces.WebInventoryDao;
import server.domain.command.allocation.AllocateToShelfCommand;
import server.domain.command.allocation.AllocateToWebCommand;
import server.domain.service.interfaces.AllocationService;
import server.domain.strategy.stockselection.interfaces.StockSelectionStrategy;
import server.utils.SQLTransactionManager;

import java.util.List;

public class AllocationServiceImpl implements AllocationService {

    private final StockSelectionStrategy strategy;
    private final StoreDao storeDao;
    private final ShelfDao shelfDao;
    private final WebInventoryDao webInventoryDao;

    public AllocationServiceImpl(StockSelectionStrategy strategy,
                                 StoreDao storeDao,
                                 ShelfDao shelfDao,
                                 WebInventoryDao webInventoryDao) {
        this.strategy = strategy;
        this.storeDao = storeDao;
        this.shelfDao = shelfDao;
        this.webInventoryDao = webInventoryDao;
    }


    @Override
    public List<AllocatedRestockDTO> allocateToWeb(AllocationRequest request) {
        return SQLTransactionManager.execute(conn -> {
            AllocateToWebCommand command = new AllocateToWebCommand(
                    request,
                    strategy,
                    storeDao,
                    webInventoryDao
            );
            return command.execute(conn);
        });
    }

    @Override
    public List<AllocatedRestockDTO> allocateToShelf(AllocationRequest request) {
        return SQLTransactionManager.execute(conn -> {
            AllocateToShelfCommand command = new AllocateToShelfCommand(
                    request,
                    strategy,
                    storeDao,
                    shelfDao
            );
            return command.execute(conn);
        });
    }
}