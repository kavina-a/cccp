package com.syos.application.reports.service;

import com.syos.application.reports.bill.BillReport;
import com.syos.application.reports.bill.fetch.*;
import com.syos.application.reports.data.*;
import com.syos.application.reports.reshelf.ReshelfReport;
import com.syos.application.reports.reshelf.fetch.ShelfReshelfFetcher;
import com.syos.application.reports.restock.RestockReport;
import com.syos.application.reports.restock.fetch.StoreRestockFetchStrategy;
import com.syos.application.reports.stock.StockReport;
import com.syos.application.reports.template.ReportTemplate;
import com.syos.application.reports.totalsale.TotalSaleReport;
import com.syos.application.reports.totalsale.fetch.*;
import com.syos.data.dao.interfaces.*;
import com.syos.domain.entity.ReportType;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportGenerationService {

    private final BillDao billDao;
    private final BillItemDao billItemDao;
    private final ShelfDao shelfDao;
    private final StoreDao storeDao;

    public ReportGenerationService(BillDao billDao, BillItemDao billItemDao, ShelfDao shelfDao, StoreDao storeDao) {
        this.billDao = billDao;
        this.billItemDao = billItemDao;
        this.shelfDao = shelfDao;
        this.storeDao = storeDao;
    }

    // 1. Total Sale Report
    public void generateTotalSaleReport(LocalDate date, ReportType type) throws SQLException {
        if (type == null) {
            throw new IllegalArgumentException("Report type must not be null");
        }
        TotalSaleFetchStrategy fetchStrategy = switch (type) {
            case OTC -> new OTCTotalSaleFetcher(billItemDao);
            case WEB -> new WEBTotalSaleFetcher(billItemDao);
            case COMBINED -> new AllTotalSaleFetcher(billItemDao);
            default -> throw new IllegalArgumentException("Invalid type for Total Sale Report");
        };
        ReportTemplate<TotalSaleReportDTO> report = new TotalSaleReport(fetchStrategy, date);
        report.generateReport();
    }

    public void generateBillReport(ReportType type) throws SQLException {
        if (type == null) {
            throw new IllegalArgumentException("Report type must not be null");
        }
        BillFetchStrategy fetchStrategy = switch (type) {
            case OTC -> new OTCBillFetcher();
            case WEB -> new WebBillFetcher();
            case COMBINED -> new AllBillFetcher();
            default -> throw new IllegalArgumentException("Invalid type for Bill Report");
        };
        ReportTemplate<BillReportDTO> report = new BillReport(billDao, fetchStrategy);
        report.generateReport();
    }

    // 3. Reshelving Report
    public void generateReshelfReport() throws SQLException {
        ReportTemplate<ReshelfReportDTO> report = new ReshelfReport(new ShelfReshelfFetcher(shelfDao));
        report.generateReport();
    }

    // 4. Stock Report
    public void generateStockReport() throws SQLException {
        ReportTemplate<StockReportDTO> report = new StockReport(storeDao);
        report.generateReport();
    }

    public void generateRestockReport() throws SQLException {
        RestockReport report = new RestockReport(new StoreRestockFetchStrategy(storeDao));
        report.generateReport();
    }
}