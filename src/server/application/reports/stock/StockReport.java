package server.application.reports.stock;

import server.application.reports.data.StockReportDTO;
import server.application.reports.template.ReportTemplate;
import server.data.dao.interfaces.StoreDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class StockReport extends ReportTemplate<StockReportDTO> {

    private final StoreDao storeDao;
    private int totalBatches;
    private int totalInitialStock;
    private int totalRemainingStock;
    private int expiredCount;

    public StockReport(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Override
    protected List<StockReportDTO> fetchData() throws SQLException {
        return storeDao.fetchBatchStockReport();
    }

    @Override
    protected void aggregate(List<StockReportDTO> data) {
        totalBatches = data.size();
        totalInitialStock = data.stream().mapToInt(StockReportDTO::getInitialStock).sum();
        totalRemainingStock = data.stream().mapToInt(StockReportDTO::getCurrentStock).sum();
        expiredCount = (int) data.stream()
                .filter(dto -> dto.getExpiryDate() != null &&
                        dto.getExpiryDate().isBefore(LocalDate.now()))
                .count();
    }

    @Override
    protected void present(List<StockReportDTO> data) {
        System.out.println("\n📦 STORE STOCK REPORT (Batch-wise)\n");
        String separator = "-".repeat(130);

        System.out.printf("%-15s | %-15s | %-10s | %6s | %6s | %-12s | %-12s | %-10s | %s\n",
                "Item Name", "Item Code", "Batch", "In", "Left", "Received", "Expiry", "Status", "Note");

        System.out.println(separator);

        int totalBatches = 0;
        int totalInitialStock = 0;
        int totalRemainingStock = 0;
        int expiredCount = 0;

        for (StockReportDTO item : data) {
            String itemName = truncateOrPad(item.getItemName(), 15);
            String itemCode = truncateOrPad(item.getItemCode(), 15);
            String batchCode = truncateOrPad(item.getBatchCode(), 10);
            String receivedDate = item.getReceivedDate().toString();
            String expiryDate = item.getExpiryDate() != null ? item.getExpiryDate().toString() : "N/A";
            String statusTag = truncateOrPad(item.getStatus(), 10);

            long daysToExpire = item.getExpiryDate() != null
                    ? ChronoUnit.DAYS.between(LocalDate.now(), item.getExpiryDate())
                    : -1;

            String expiryNote = "";
            if (daysToExpire >= 0 && daysToExpire <= 5) {
                expiryNote = "⚠ Expires Soon!";
            } else if (daysToExpire < 0) {
                expiryNote = "❌ Expired!";
                expiredCount++;
            }

            System.out.printf("%-15s | %-15s | %-10s | %6d | %6d | %-12s | %-12s | %-10s | %s\n",
                    itemName,
                    itemCode,
                    batchCode,
                    item.getInitialStock(),
                    item.getCurrentStock(),
                    receivedDate,
                    expiryDate,
                    statusTag,
                    expiryNote
            );

            totalBatches++;
            totalInitialStock += item.getInitialStock();
            totalRemainingStock += item.getCurrentStock();
        }

        System.out.println("\n📊 Inventory Summary");
        System.out.println(separator);
        System.out.printf("%-25s : %d\n", "Total Batches", totalBatches);
        System.out.printf("%-25s : %d\n", "Total Received Qty", totalInitialStock);
        System.out.printf("%-25s : %d\n", "Total Remaining Qty", totalRemainingStock);
        System.out.printf("%-25s : %d\n", "Expired Batches", expiredCount);
        System.out.println(separator + "\n");
    }

    private String truncateOrPad(String value, int width) {
        if (value == null) return " ".repeat(width);
        return value.length() > width
                ? value.substring(0, width - 1) + "…"
                : String.format("%-" + width + "s", value);
    }
}