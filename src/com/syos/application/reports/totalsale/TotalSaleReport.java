package com.syos.application.reports.totalsale;

import com.syos.application.reports.template.ReportTemplate;
import com.syos.application.reports.totalsale.fetch.TotalSaleFetchStrategy;
import com.syos.application.reports.data.TotalSaleReportDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TotalSaleReport extends ReportTemplate<TotalSaleReportDTO> {

    private final TotalSaleFetchStrategy fetchStrategy;
    private final LocalDate reportDate;

    private int totalItemsSold;
    private BigDecimal totalRevenue;

    private TotalSaleReportDTO topSeller;
    private TotalSaleReportDTO topRevenueItem;

    public TotalSaleReport(TotalSaleFetchStrategy fetchStrategy, LocalDate reportDate) {
        this.fetchStrategy = fetchStrategy;
        this.reportDate = reportDate;
    }

    @Override
    protected List<TotalSaleReportDTO> fetchData() {
        return fetchStrategy.fetch(reportDate);
    }

    @Override
    protected void aggregate(List<TotalSaleReportDTO> data) {
        totalItemsSold = 0;
        totalRevenue = BigDecimal.ZERO;
        topSeller = null;
        topRevenueItem = null;

        for (TotalSaleReportDTO item : data) {
            totalItemsSold += item.getTotalQuantity();
            totalRevenue = totalRevenue.add(item.getTotalRevenue());

            if (topSeller == null || item.getTotalQuantity() > topSeller.getTotalQuantity()) {
                topSeller = item;
            }

            if (topRevenueItem == null || item.getTotalRevenue().compareTo(topRevenueItem.getTotalRevenue()) > 0) {
                topRevenueItem = item;
            }
        }
    }

    @Override
    protected void present(List<TotalSaleReportDTO> data) {
        String separator = "-".repeat(90);
        System.out.println("\n📊 TOTAL SALES REPORT — " + reportDate);
        System.out.println(separator);
        System.out.printf("%-15s | %-30s | %10s | %-12s\n", "Item Code", "Item Name", "Quantity", "Revenue (Rs.)");
        System.out.println(separator);

        for (TotalSaleReportDTO item : data) {
            String name = item.getItemName();
            if (name.length() > 30) name = name.substring(0, 27) + "...";
            System.out.printf("%-15s | %-30s | %10d | %12.2f\n",
                    item.getItemCode(), name, item.getTotalQuantity(), item.getTotalRevenue());
        }

        System.out.println(separator);
        System.out.printf("%-48s : %10d\n", "Total Items Sold", totalItemsSold);
        System.out.printf("%-48s : Rs. %12.2f\n", "Total Revenue", totalRevenue);
        System.out.println(separator);

        if (topSeller != null) {
            String name = topSeller.getItemName();
            if (name.length() > 30) name = name.substring(0, 27) + "...";
            System.out.printf("Top Seller     : %-30s (%s) - Qty: %4d\n",
                    name, topSeller.getItemCode(), topSeller.getTotalQuantity());
        }

        if (topRevenueItem != null) {
            String name = topRevenueItem.getItemName();
            if (name.length() > 30) name = name.substring(0, 27) + "...";
            System.out.printf("Top Revenue    : %-30s (%s) - Rs. %10.2f\n",
                    name, topRevenueItem.getItemCode(), topRevenueItem.getTotalRevenue());
        }

        System.out.println(separator);
    }
}