package com.syos.interfaces.cli.utils;

import com.syos.application.dto.StoreStockDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StockStockCliFormatter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void printHeader() {
        System.out.printf("%-15s %-15s %-12s %-12s %-20s %-20s%n",
                "📦 Item Code", "🔢 Batch Code", "🧮 Init", "📊 Current", "⏳ Expiry", "📥 Received");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    public static void print(StoreStockDTO stock) {
        System.out.printf("%-15s %-15s %-12d %-20s %-20s %-20s%n",
                stock.getItemCode(),
                stock.getBatchCode(),
                stock.getInitialStock(),
                stock.getCurrentStock(),
                format(stock.getExpiryDate()),
                format(stock.getReceivedDate()));
    }

    private static String format(java.time.LocalDateTime dateTime) {
        return dateTime != null ? formatter.format(dateTime) : "N/A";
    }

    public static void printList(List<StoreStockDTO> stocks) {
        if (stocks == null || stocks.isEmpty()) {
            System.out.println("🚫 No stock found.");
            return;
        }

        printHeader();
        for (StoreStockDTO stock : stocks) {
            print(stock);
        }

        int totalStock = stocks.stream().mapToInt(StoreStockDTO::getCurrentStock).sum();
        System.out.println("\n🧾 Total Current Stock Across Batches: " + totalStock);
    }
}