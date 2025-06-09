package com.syos.domain.notifications;

import com.syos.application.dto.LowStockEventDTO;

import java.util.Optional;

public interface StockObserver {
    void onLowStock(Optional<LowStockEventDTO> event);
}
