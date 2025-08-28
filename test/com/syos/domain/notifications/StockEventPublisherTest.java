package com.syos.domain.notifications;

import server.domain.notifications.StockEventPublisher;
import server.domain.notifications.StockObserver;
import server.application.dto.LowStockEventDTO;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class StockEventPublisherTest {

    @Test
    void shouldNotifyAllObserversWhenLowStockOccurs() {
        StockEventPublisher publisher = new StockEventPublisher();
        AtomicBoolean notified = new AtomicBoolean(false);

        StockObserver testObserver = event -> notified.set(event.isPresent());

        publisher.registerObserver(testObserver);

        LowStockEventDTO event = new LowStockEventDTO("ITEM123", "Test Product", 2);
        publisher.notifyLowStock(Optional.of(event));

        assertTrue(notified.get(), "Observer should be notified with event present");
    }

    @Test
    void shouldHandleNoEventGracefully() {
        StockEventPublisher publisher = new StockEventPublisher();
        AtomicBoolean called = new AtomicBoolean(false);

        StockObserver observer = event -> called.set(true);

        publisher.registerObserver(observer);
        publisher.notifyLowStock(Optional.empty());

        assertTrue(called.get(), "Observer should still be called even with empty event");
    }
}