package com.syos.domain.service;

import com.syos.application.dto.LowStockEventDTO;
import com.syos.application.dto.ShelfDTO;
import com.syos.application.dto.request.CreateShelfRequest;
import com.syos.data.dao.interfaces.ShelfDao;
import com.syos.data.dao.interfaces.StoreDao;
import com.syos.domain.entity.Shelf;
import com.syos.domain.entity.ShelfInventory;
import com.syos.domain.entity.StockStatus;
import com.syos.domain.notifications.StockEventPublisher;
import com.syos.utils.SQLTransactionManager;
import com.syos.utils.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ShelfService {
    private final ShelfDao shelfDao;
    private final StoreDao storeDao;
    private final StockEventPublisher stockEventPublisher;


    public ShelfService(ShelfDao shelfDao, StoreDao storeDao, StockEventPublisher stockEventPublisher) {
        this.shelfDao = shelfDao;
        this.storeDao = storeDao;
        this.stockEventPublisher = stockEventPublisher;
    }

    public ShelfDTO createShelf(CreateShelfRequest request) {
        return SQLTransactionManager.execute(conn -> {

            boolean exists = shelfDao.exists(conn, request.getLocation());
            System.out.println("Checking if location exists: " + request.getLocation() + " => " + exists);
            if (exists) {
                return new ShelfDTO(false, "Shelf ID already exists.", null, null, 0, null);
            }

            Shelf shelf = new Shelf();
            shelf.setShelfId(request.getShelfId());
            shelf.setItemCode(request.getItemCode());
            shelf.setQuantityOnShelf(request.getQuantityOnShelf());
            shelf.setUpdatedAt(LocalDateTime.now());
            shelf.setStatus(StockStatus.ACTIVE);
            shelf.setUpdatedBy(SessionManager.getInstance().getLoggedInEmployee().getEmployeeID());
            shelf.setUpdatedAt(LocalDateTime.now());

            shelfDao.save(conn, shelf);

            return new ShelfDTO(
                    true,
                    "Shelf created successfully.",
                    shelf.getShelfId(),
                    shelf.getItemCode(),
                    shelf.getQuantityOnShelf(),
                    shelf.getUpdatedAt().toString()
            );
        });
    }

    public void deductStock(Connection conn, String itemCode, int quantity) throws SQLException {
        List<ShelfInventory> batches = shelfDao.findActiveBatches(conn, itemCode);

        for (ShelfInventory batch : batches) {

            if (quantity <= 0) break;

            int available = batch.getQuantityRemaining();
            int toDeduct = Math.min(quantity, available);

            batch.setQuantityRemaining(available - toDeduct);

            if (batch.getQuantityRemaining() == 0) {
                batch.setStatus(StockStatus.CONSUMED);
            }

            shelfDao.update(conn, batch);

            quantity -= toDeduct;
        }

        if (quantity > 0) {
            throw new IllegalStateException("Insufficient shelf stock for product: " + itemCode);
        }

        evaluateLowStock(conn, itemCode);

    }

    public void evaluateLowStock(Connection conn, String itemCode) throws SQLException {
        Optional<LowStockEventDTO> dto = shelfDao.getTriggeredLowStockEvent(conn, itemCode);

        if (dto.isPresent()) {
            stockEventPublisher.notifyLowStock(dto);
        }
    }
}

//    public RestockShelfDTO restockShelf(RestockShelfRequest request) {
//        // 1. Fetch available stock for the item
//        request.setAvailableStock(storeDao.findAvailableStockByItemCode(request.getItemCode()));
//
//        if (request.getAvailableStock().isEmpty()) {
//            return new RestockShelfDTO(
//                    request.getItemCode(),
//                    0,
//                    List.of(),
//                    false,
//                    "No available stock to fulfill restocking request"
//            );
//        }
//
//        // 2. Apply the selection strategy
//        AllocatedRestockDTO restockPlan = stockSelectionStrategy.selectStockForShelf(request);
//
//        // 3. Apply the plan: create shelf entries and reduce store stock
//        List<String> batchCodesUsed = new ArrayList<>();
//        int totalRestocked = 0;
//
//        for (AllocatedRestockDTO.AllocatedRestock allocation : restockPlan.getAllocations()) {
//            Shelf shelf = new Shelf();
//            shelf.setItemCode(allocation.getItemCode());
//            shelf.setBatchCode(allocation.getBatchCode());
//            shelf.setQuantity(allocation.getAllocatedQuantity());
//            shelf.setRestockedDate(LocalDateTime.now());
//            shelf.setUpdatedDateTime(LocalDateTime.now());
//            // Optional metadata
//            // shelf.setIsDeleted(false);
//            // shelf.setUpdatedBy("system");
//
//            shelfDao.save(shelf);
//
//            storeDao.reduceStock(
//                    allocation.getItemCode(),
//                    allocation.getBatchCode(),
//                    allocation.getAllocatedQuantity()
//            );
//
//            totalRestocked += allocation.getAllocatedQuantity();
//            batchCodesUsed.add(allocation.getBatchCode());
//        }
//
//        // 4. Build and return response
//        boolean fullyRestocked = totalRestocked == request.getRequiredQuantity();
//        String message = fullyRestocked
//                ? "Shelf restocked successfully."
//                : "Partial restock completed: insufficient stock for full quantity.";
//
//        return new RestockShelfDTO(
//                request.getItemCode(),
//                totalRestocked,
//                batchCodesUsed,
//                true,
//                message
//        );
//    }
