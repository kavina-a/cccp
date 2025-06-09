package com.syos.domain.strategy.stockselection.interfaces;

import com.syos.application.dto.AllocatedRestockDTO;
import com.syos.domain.entity.StoreStock;

import java.util.List;

public interface StockSelectionStrategy {
    List<AllocatedRestockDTO> allocateStock(List<StoreStock> availableBatches, int requestedQuantity);
};
