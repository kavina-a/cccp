package server.application.reports.bill;

import server.application.reports.data.BillReportDTO;
import server.application.reports.template.ReportTemplate;
import server.application.reports.bill.fetch.BillFetchStrategy;
import server.data.dao.interfaces.BillDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class BillReport extends ReportTemplate<BillReportDTO> {

    private final BillDao billDao;
    private final BillFetchStrategy fetchStrategy;

    public BillReport(BillDao billDao, BillFetchStrategy fetchStrategy) {
        this.billDao = billDao;
        this.fetchStrategy = fetchStrategy;
    }

    @Override
    protected List<BillReportDTO> fetchData() throws SQLException {
        return fetchStrategy.fetch(billDao);
    }

    @Override
    protected void present(List<BillReportDTO> data) {
        String separator = "-".repeat(85);
        System.out.println("\n📄 BILL REPORT");
        System.out.println(separator);

        System.out.printf("%-15s | %-15s | %-18s | %-20s\n", "Bill ID", "Customer ID", "Total Amount (Rs.)", "Bill Type");
        System.out.println(separator);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalBills = 0;

        for (BillReportDTO row : data) {
            System.out.printf("%-15s | %-15s | %18s | %-20s\n",
                    row.getBillIdentifier(),
                    row.getCustomerId(),
                    String.format("%,.2f", row.getTotalAmount()),
                    row.getTransactionType());

            totalRevenue = totalRevenue.add(row.getTotalAmount());
            totalBills++;
        }

        System.out.println(separator);
        System.out.printf("%-25s : %d\n", "🧾 Total Bills", totalBills);
        System.out.printf("%-25s : Rs. %,.2f\n", "💰 Total Revenue", totalRevenue);

        if (totalBills > 0) {
            BigDecimal average = totalRevenue.divide(BigDecimal.valueOf(totalBills), 2, RoundingMode.HALF_UP);
            System.out.printf("%-25s : Rs. %,.2f\n", "📈 Average Bill Amount", average);
        } else {
            System.out.printf("%-25s : Rs. 0.00\n", "📈 Average Bill Amount");
        }

        System.out.println(separator + "\n");
    }
}