//package com.syos.service;
//
//import com.syos.application.reports.data.TotalSaleReportDTO;
//import com.syos.application.reports.template.ReportTemplate;
//import com.syos.application.reports.totalsale.TotalSaleReport;
//import com.syos.application.reports.totalsale.fetch.TotalSaleFetchStrategy;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//        import static org.mockito.Mockito.*;
//
//class TotalReportSaleTest {
//
//    private TotalSaleFetchStrategy fetchStrategy;
//    private TotalSaleReport report;
//
//    @BeforeEach
//    void setup() {
//        fetchStrategy = mock(TotalSaleFetchStrategy.class);
//
//        when(fetchStrategy.fetchData()).thenReturn(List.of(
//                new TotalSaleReportDTO("ITEM001", "Coconut Oil", 5, new BigDecimal("500.00")),
//                new TotalSaleReportDTO("ITEM002", "Soap", 3, new BigDecimal("300.00")),
//                new TotalSaleReportDTO("ITEM003", "Rice", 2, new BigDecimal("200.00"))
//        ));
//
//        report = new TotalSaleReport(fetchStrategy, LocalDate.of(2025, 6, 8));
//    }
//
//    @Test
//    void shouldAggregateCorrectly_andPrintReport() throws Exception {
//        // Capture System.out
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        PrintStream originalOut = System.out;
//        System.setOut(new PrintStream(outStream));
//
//        report.generateReport();
//
//        // Restore original System.out
//        System.setOut(originalOut);
//
//        String output = outStream.toString();
//
//        // Assertions on aggregation
//        assertTrue(output.contains("Total Items Sold"), "Should print total items sold");
//        assertTrue(output.contains("Total Revenue"), "Should print total revenue");
//
//        // Check computed values
//        assertTrue(output.contains("10"), "Total quantity should be 10");
//        assertTrue(output.contains("Rs. 1000.00"), "Total revenue should be Rs. 1000.00");
//    }
//
//    @Test
//    void shouldHandleEmptyReportDataGracefully() throws Exception {
//        when(fetchStrategy.fetchData()).thenReturn(List.of());
//
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        PrintStream originalOut = System.out;
//        System.setOut(new PrintStream(outStream));
//
//        new TotalSaleReport(fetchStrategy, LocalDate.of(2025, 6, 8)).generateReport();
//
//        System.setOut(originalOut);
//
//        String output = outStream.toString();
//        assertTrue(output.contains("Total Items Sold"), "Output should include total summary");
//        assertTrue(output.contains("0"), "Empty data should result in 0 totals");
//    }
//}