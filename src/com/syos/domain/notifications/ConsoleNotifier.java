package com.syos.domain.notifications;

import com.syos.application.dto.LowStockEventDTO;

import java.util.Optional;

public class ConsoleNotifier implements StockObserver {
    @Override
    public void onLowStock(Optional<LowStockEventDTO> event) {
        event.ifPresent(e -> {
            System.out.println("────────────────────────────────────────────");
            System.out.printf("⚠️  LOW STOCK ALERT%n");
            System.out.printf("Item: %s (%s)%n", e.getItemName(), e.getItemCode());
            System.out.printf("Remaining Quantity: %d%n", e.getCurrentQuantity());
            System.out.println("Please consider restocking soon.");
            System.out.println("────────────────────────────────────────────");
        });
    }
}