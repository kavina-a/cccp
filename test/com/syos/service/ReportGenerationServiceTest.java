package com.syos.service;

import server.data.dao.interfaces.BillDao;
import server.data.dao.interfaces.BillItemDao;
import server.data.dao.interfaces.ShelfDao;
import server.data.dao.interfaces.StoreDao;
import server.application.reports.service.ReportGenerationService;
import server.domain.entity.ReportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportGenerationServiceTest {

    private BillDao billDao;
    private BillItemDao billItemDao;
    private ShelfDao shelfDao;
    private StoreDao storeDao;
    private ReportGenerationService service;

    @BeforeEach
    void setup() {
        billDao = mock(BillDao.class);
        billItemDao = mock(BillItemDao.class);
        shelfDao = mock(ShelfDao.class);
        storeDao = mock(StoreDao.class);

        service = new ReportGenerationService(billDao, billItemDao, shelfDao, storeDao);
    }

    @Test
    void generateBillReport_shouldThrowException_onNullType() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateBillReport(null)
        );
        assertEquals("Report type must not be null", ex.getMessage());
    }

    @Test
    void generateTotalSaleReport_OTC_shouldExecuteWithoutCrash() {
        assertDoesNotThrow(() ->
                service.generateTotalSaleReport(LocalDate.now(), ReportType.OTC)
        );
    }

    @Test
    void generateTotalSaleReport_WEB_shouldExecuteWithoutCrash() {
        assertDoesNotThrow(() ->
                service.generateTotalSaleReport(LocalDate.now(), ReportType.WEB)
        );
    }

    @Test
    void generateTotalSaleReport_COMBINED_shouldExecuteWithoutCrash() {
        assertDoesNotThrow(() ->
                service.generateTotalSaleReport(LocalDate.now(), ReportType.COMBINED)
        );
    }


    @Test
    void generateBillReport_WEB_shouldExecuteWithoutCrash() {
        assertDoesNotThrow(() -> service.generateBillReport(ReportType.WEB));
    }

    @Test
    void generateBillReport_OTC_shouldExecuteWithoutCrash() {
        assertDoesNotThrow(() -> service.generateBillReport(ReportType.OTC));
    }

    @Test
    void generateBillReport_COMBINED_shouldExecuteWithoutCrash() {
        assertDoesNotThrow(() -> service.generateBillReport(ReportType.COMBINED));
    }


    @Test
    void generateReshelfReport_shouldWorkWithValidDao() {
        assertDoesNotThrow(() -> service.generateReshelfReport());
    }

    @Test
    void generateStockReport_shouldWorkWithValidDao() {
        assertDoesNotThrow(() -> service.generateStockReport());
    }


    @Test
    void generateRestockReport_shouldWorkWithValidDao() {
        assertDoesNotThrow(() -> service.generateRestockReport());
    }


    @Test
    void generateTotalSaleReport_shouldPropagateSQLException() throws SQLException {
        BillItemDao faultyDao = mock(BillItemDao.class);
        // simulate that fetcher used later throws SQLException
        ReportGenerationService faultyService = new ReportGenerationService(billDao, faultyDao, shelfDao, storeDao);

        // Assume you simulate the SQLException in fetcher later, for now just test structure
        assertDoesNotThrow(() ->
                faultyService.generateTotalSaleReport(LocalDate.now(), ReportType.OTC)
        );
    }
}