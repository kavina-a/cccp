package com.syos.application.reports.restock;

import com.syos.application.reports.data.RestockReportDTO;
import com.syos.application.reports.restock.fetch.RestockFetchStrategy;
import com.syos.application.reports.template.ReportTemplate;

import java.sql.SQLException;
import java.util.List;

public class RestockReport extends ReportTemplate<RestockReportDTO> {

    private final RestockFetchStrategy fetchStrategy;
    private int totalCurrent = 0;
    private int totalReorderLevel = 0;
    private int totalDeficit = 0;

    public RestockReport(RestockFetchStrategy fetchStrategy) {
        this.fetchStrategy = fetchStrategy;
    }

    @Override
    protected List<RestockReportDTO> fetchData() throws SQLException {
        return fetchStrategy.fetch();
    }

    @Override
    protected void aggregate(List<RestockReportDTO> data) {
        for (RestockReportDTO dto : data) {
            totalCurrent += dto.getCurrentStock() != null ? dto.getCurrentStock() : 0;
            if (dto.getReorderLevel() != null) {
                totalReorderLevel += dto.getReorderLevel();
                totalDeficit += Math.max(dto.getReorderLevel() - dto.getCurrentStock(), 0);
            }
        }
    }

    @Override
    protected void present(List<RestockReportDTO> data) {
        System.out.println("\n📦 STORE RESTOCK REPORT");
        String separator = "-".repeat(110);
        System.out.println(separator);

        System.out.printf("%-15s | %-30s | %-14s | %-8s | %-8s | %-20s\n",
                "Item Code", "Item Name", "Reorder Level", "Current", "Deficit", "Last Ordered Qty");

        System.out.println(separator);

        for (RestockReportDTO dto : data) {
            String reorderLevelStr = dto.getReorderLevel() != null ? String.valueOf(dto.getReorderLevel()) : "N/A";
            String lastOrderedStr = dto.getLastOrderedQuantity() != null ? dto.getLastOrderedQuantity() + " (prev)" : "N/A";

            int current = dto.getCurrentStock() != null ? dto.getCurrentStock() : 0;
            int deficit = (dto.getReorderLevel() != null) ? Math.max(dto.getReorderLevel() - current, 0) : 0;

            String name = dto.getItemName();
            if (name.length() > 30) name = name.substring(0, 27) + "...";

            System.out.printf("%-15s | %-30s | %-14s | %-8d | %-8d | %-20s\n",
                    dto.getItemCode(),
                    name,
                    reorderLevelStr,
                    current,
                    deficit,
                    lastOrderedStr
            );
        }

        System.out.println(separator);
        System.out.printf("%-15s | %-30s | %14d | %8d | %8d | %-20s\n",
                "TOTALS →", "", totalReorderLevel, totalCurrent, totalDeficit, "");
        System.out.println(separator);
        System.out.println("Note: 'Deficit' indicates how many more units are needed to meet the reorder level.\n");
    }
}