package server.application.reports.reshelf;

import server.application.reports.data.ReshelfReportDTO;
import server.application.reports.reshelf.fetch.ReshelfFetchStrategy;
import server.application.reports.template.ReportTemplate;

import java.sql.SQLException;
import java.util.List;

public class ReshelfReport extends ReportTemplate<ReshelfReportDTO> {

    private final ReshelfFetchStrategy fetchStrategy;

    private int totalCurrent = 0;
    private int totalSold = 0;
    private int totalExpired = 0;
    private int totalToReshelf = 0;

    public ReshelfReport(ReshelfFetchStrategy fetchStrategy) {
        this.fetchStrategy = fetchStrategy;
    }

    @Override
    protected List<ReshelfReportDTO> fetchData() throws SQLException {
        return fetchStrategy.fetch();
    }

    @Override
    protected void aggregate(List<ReshelfReportDTO> data) {
        for (ReshelfReportDTO dto : data) {
            totalCurrent += dto.getCurrentQuantity();
            totalSold += dto.getSoldQuantity();
            totalExpired += dto.getExpiredQuantity();
            totalToReshelf += dto.getToReshelfQuantity();
        }
    }

    @Override
    protected void present(List<ReshelfReportDTO> data) {
        System.out.println("\n📦 SHELF RESHELVING REPORT");
        String separator = "-".repeat(125);
        System.out.println(separator);

        System.out.printf("%-15s | %-30s | %-10s | %-10s | %-10s | %-10s | %-10s | %-15s\n",
                "Item Code", "Item Name", "ShelfCap", "Current", "Sold", "Expired", "To Fill", "Last Moved");

        System.out.println(separator);

        for (ReshelfReportDTO dto : data) {
            String expiredMarker = dto.getExpiredQuantity() > 0 ? " ⚠️" : "";
            String name = dto.getItemName();
            if (name.length() > 30) name = name.substring(0, 27) + "...";

            System.out.printf("%-15s | %-30s | %-10d | %-10d | %-10d | %-10d | %-10d | %-15s%s\n",
                    dto.getItemCode(),
                    name,
                    dto.getShelfCapacity(),
                    dto.getCurrentQuantity(),
                    dto.getSoldQuantity(),
                    dto.getExpiredQuantity(),
                    dto.getToReshelfQuantity(),
                    dto.getLastMovedDate(),
                    expiredMarker
            );
        }

        System.out.println(separator);
        System.out.printf("TOTALS → Current: %-5d | Sold: %-5d | Expired: %-5d | To Reshelf: %-5d\n",
                totalCurrent, totalSold, totalExpired, totalToReshelf);
        System.out.println("Note: ⚠️ indicates items with expired stock.\n");
    }
}