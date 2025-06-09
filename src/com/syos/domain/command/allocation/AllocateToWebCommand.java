package com.syos.domain.command.allocation;

import com.syos.application.dto.AllocatedRestockDTO;
import com.syos.application.dto.request.AllocationRequest;
import com.syos.data.dao.interfaces.StoreDao;
import com.syos.data.dao.interfaces.WebInventoryDao;
import com.syos.domain.command.allocation.interfaces.AllocationCommand;
import com.syos.domain.entity.StoreStock;
import com.syos.domain.strategy.stockselection.interfaces.StockSelectionStrategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AllocateToWebCommand implements AllocationCommand {

    private final AllocationRequest request;
    private final StockSelectionStrategy strategy;
    private final StoreDao storeDao;
    private final WebInventoryDao webInventoryDao;

    public AllocateToWebCommand(
            AllocationRequest request,
            StockSelectionStrategy strategy,
            StoreDao storeDao,
            WebInventoryDao webInventoryDao
    ) {
        this.request = request;
        this.strategy = strategy;
        this.storeDao = storeDao;
        this.webInventoryDao = webInventoryDao;
    }

    public List<AllocatedRestockDTO> execute(Connection conn) throws SQLException {

        List<StoreStock> availableBatches = storeDao.findByItemCode(conn, request.getItemCode());

        List<AllocatedRestockDTO> selectedAllocations = strategy.allocateStock(availableBatches, request.getRequiredQuantity());

        for (AllocatedRestockDTO allocation : selectedAllocations) {

            // update store stock after allocation
            storeDao.updateStockAfterAllocation(
                    conn,
                    allocation.getItemCode(),
                    allocation.getBatchCode(),
                    allocation.getAllocatedQuantity()
            );

            // log the allocation in web inventory
            webInventoryDao.logWebRestockTransaction(
                    conn,
                    allocation.getItemCode(),
                    allocation.getBatchCode(),
                    allocation.getAllocatedQuantity()
            );
        }

        System.out.println("Allocation process complete.");
        return selectedAllocations;
    }
}