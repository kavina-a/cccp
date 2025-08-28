package server.domain.strategy.stockselection.interfaces;

import server.application.dto.AllocatedRestockDTO;
import server.domain.entity.StoreStock;

import java.util.List;

public interface StockSelectionStrategy {
    List<AllocatedRestockDTO> allocateStock(List<StoreStock> availableBatches, int requestedQuantity);
};
