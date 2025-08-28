package server.application.reports.service;

import server.data.dao.interfaces.BillDao;
import server.data.dao.interfaces.BillItemDao;
import server.data.dao.interfaces.ShelfDao;
import server.data.dao.interfaces.StoreDao;
import server.application.reports.bill.BillReport;
import com.syos.application.reports.bill.fetch.*;
import com.syos.application.reports.data.*;
import server.application.reports.bill.fetch.AllBillFetcher;
import server.application.reports.bill.fetch.BillFetchStrategy;
import server.application.reports.bill.fetch.OTCBillFetcher;
import server.application.reports.bill.fetch.WebBillFetcher;
import server.application.reports.data.BillReportDTO;
import server.application.reports.data.ReshelfReportDTO;
import server.application.reports.data.StockReportDTO;
import server.application.reports.data.TotalSaleReportDTO;
import server.application.reports.reshelf.ReshelfReport;
import server.application.reports.reshelf.fetch.ShelfReshelfFetcher;
import server.application.reports.restock.RestockReport;
import server.application.reports.restock.fetch.StoreRestockFetchStrategy;
import server.application.reports.stock.StockReport;
import server.application.reports.template.ReportTemplate;
import server.application.reports.totalsale.TotalSaleReport;
import com.syos.application.reports.totalsale.fetch.*;
import server.domain.entity.ReportType;
import server.application.reports.totalsale.fetch.AllTotalSaleFetcher;
import server.application.reports.totalsale.fetch.OTCTotalSaleFetcher;
import server.application.reports.totalsale.fetch.TotalSaleFetchStrategy;
import server.application.reports.totalsale.fetch.WEBTotalSaleFetcher;

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