package server.domain.notifications;

import server.application.dto.LowStockEventDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockEventPublisher {
    private final List<StockObserver> observers = new ArrayList<>();

    public void registerObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void notifyLowStock(Optional<LowStockEventDTO> event) {
        for (StockObserver observer : observers) {
            observer.onLowStock(event);
        }
    }
}