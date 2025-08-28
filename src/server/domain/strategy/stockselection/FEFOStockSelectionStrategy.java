package server.domain.strategy.stockselection;

import server.application.dto.AllocatedRestockDTO;
import server.domain.entity.StoreStock;
import server.domain.strategy.stockselection.interfaces.StockSelectionStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FEFOStockSelectionStrategy implements StockSelectionStrategy {

    /**
     * FEFO = First Expired, First Out
     * Select stock from available store batches based on earliest expiry first.
     * Supports multi-batch fulfillment and ensures minimal waste.
     */

    @Override
    public List<AllocatedRestockDTO> allocateStock(List<StoreStock> availableBatches, int requestedQuantity) {
        availableBatches = new ArrayList<>(availableBatches);
        availableBatches.sort(Comparator.comparing(StoreStock::getExpiryDate));

        List<AllocatedRestockDTO> allocations = new ArrayList<>();
        int remainingQuantity = requestedQuantity;

        for (StoreStock batch : availableBatches) {
            if (remainingQuantity <= 0) break;
            if (batch.getCurrentStock() <= 0) continue;

            int availableQty = batch.getCurrentStock();
            int allocatedQty = Math.min(availableQty, remainingQuantity);

            allocations.add(new AllocatedRestockDTO(
                    batch.getItemCode(),
                    batch.getBatchCode(),
                    allocatedQty
            ));

            remainingQuantity -= allocatedQty;
        }

        if (remainingQuantity > 0) {
            throw new IllegalArgumentException("Insufficient stock to fulfill request of " + requestedQuantity);
        }

        return allocations;
    }
}
