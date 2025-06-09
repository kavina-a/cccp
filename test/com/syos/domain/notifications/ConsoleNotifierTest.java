package com.syos.domain.notifications;


import com.syos.application.dto.LowStockEventDTO;
import com.syos.domain.notifications.ConsoleNotifier;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleNotifierTest {

    @Test
    void shouldPrintLowStockAlert() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            ConsoleNotifier notifier = new ConsoleNotifier();
            LowStockEventDTO dto = new LowStockEventDTO("ITEM123", "Sample Item", 5);
            notifier.onLowStock(Optional.of(dto));

            String printed = output.toString();

            assertTrue(printed.contains("LOW STOCK ALERT"));
            assertTrue(printed.contains("ITEM123"));
            assertTrue(printed.contains("Sample Item"));
            assertTrue(printed.contains("5"));
        } finally {
            System.setOut(originalOut); // Restore original
        }
    }

    @Test
    void shouldNotPrintAnythingWhenEventIsEmpty() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            ConsoleNotifier notifier = new ConsoleNotifier();
            notifier.onLowStock(Optional.empty());

            String printed = output.toString();
            assertTrue(printed.isEmpty(), "No output should be printed for empty Optional");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void shouldPrintZeroQuantityAlert() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            ConsoleNotifier notifier = new ConsoleNotifier();
            LowStockEventDTO dto = new LowStockEventDTO("ITEM999", "EdgeCaseItem", 0);
            notifier.onLowStock(Optional.of(dto));

            String printed = output.toString();

            assertTrue(printed.contains("ITEM999"));
            assertTrue(printed.contains("EdgeCaseItem"));
            assertTrue(printed.contains("0"));
        } finally {
            System.setOut(originalOut);
        }
    }
}