package server.domain.notifications;

import server.application.dto.LowStockEventDTO;

import java.util.Optional;

public interface StockObserver {
    void onLowStock(Optional<LowStockEventDTO> event);
}
