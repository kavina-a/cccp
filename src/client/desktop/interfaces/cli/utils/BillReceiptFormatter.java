package client.desktop.interfaces.cli.utils;

import server.application.dto.BillReceiptDTO;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BillReceiptFormatter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final NumberFormat CURRENCY = NumberFormat.getNumberInstance(Locale.US);

    public static void printBillDetails(List<BillReceiptDTO> receipt) {
        if (receipt == null || receipt.isEmpty()) {
            System.out.println("No bill data available.");
            return;
        }

        BillReceiptDTO bill = receipt.get(0);

        System.out.println("=".repeat(50));
        System.out.println("           SYOS POINT-OF-SALE RECEIPT         ");
        System.out.println("=".repeat(50));

        String billNo = String.format("%03d", bill.getSerialNumber());
        String billDate = bill.getBillDate().format(DATE_FORMAT);
        System.out.printf("Bill No: %s/%s%n", billDate, billNo);

        System.out.println("\nItems:");
        System.out.println("-".repeat(50));
        System.out.printf("%-20s %-6s %-10s %-10s%n", "Item Name", "Qty", "Price", "Subtotal");
        System.out.println("-".repeat(50));

        for (BillReceiptDTO item : receipt) {
            String name = item.getItemName();
            int qty = item.getQuantity();
            BigDecimal price = item.getPrice();
            BigDecimal subtotal = item.getTotalItemPrice();

            System.out.printf("%-20s %-6d %-10s %-10s%n",
                    name,
                    qty,
                    formatCurrency(price),
                    formatCurrency(subtotal));
        }

        System.out.println("-".repeat(50));
        System.out.printf("Total:%35s%n", formatCurrency(bill.getTotalAmount()));
        System.out.printf("Cash Tendered:%27s%n", formatCurrency(bill.getCashTendered()));
        System.out.printf("Change:%34s%n", formatCurrency(bill.getChangeAmount()));
        System.out.println("=".repeat(50));
        System.out.println("     Thank you for shopping with us!");
        System.out.println("=".repeat(50));
    }

    private static String formatCurrency(BigDecimal value) {
        return String.format("%.2f", value);
    }
}