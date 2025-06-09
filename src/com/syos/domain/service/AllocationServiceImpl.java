package com.syos.domain.service;

import com.syos.application.dto.AllocatedRestockDTO;
import com.syos.application.dto.request.AllocationRequest;
import com.syos.data.dao.interfaces.ShelfDao;
import com.syos.data.dao.interfaces.StoreDao;
import com.syos.data.dao.interfaces.WebInventoryDao;
import com.syos.domain.command.allocation.AllocateToShelfCommand;
import com.syos.domain.command.allocation.AllocateToWebCommand;
import com.syos.domain.service.interfaces.AllocationService;
import com.syos.domain.strategy.stockselection.interfaces.StockSelectionStrategy;
import com.syos.utils.SQLTransactionManager;

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