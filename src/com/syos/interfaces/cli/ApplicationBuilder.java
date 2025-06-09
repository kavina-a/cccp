package com.syos.interfaces.cli;

import com.syos.application.businessDay.BusinessDayOrchestrator;
import com.syos.application.postprocessors.StockDeductionProcessor;
import com.syos.application.reports.service.ReportGenerationService;
import com.syos.data.dao.*;
import com.syos.data.dao.interfaces.*;
import com.syos.domain.command.businessDay.ExpireStockCommand;
import com.syos.domain.command.businessDay.GenerateReportsCommand;
import com.syos.domain.command.businessDay.LogBusinessDayCommand;
import com.syos.domain.command.businessDay.intefaces.BusinessDayCommand;
import com.syos.domain.entity.TransactionType;
import com.syos.domain.notifications.ConsoleNotifier;
import com.syos.domain.notifications.StockEventPublisher;
import com.syos.domain.service.*;
import com.syos.domain.service.interfaces.AllocationService;
import com.syos.domain.strategy.stockselection.FEFOStockSelectionStrategy;
import com.syos.domain.strategy.billing.OtcBillingStrategy;
import com.syos.domain.strategy.billing.WebBillingStrategy;
import com.syos.domain.strategy.billing.interfaces.BillingStrategy;
import com.syos.domain.service.AuthenticationService;

import java.util.List;
import java.util.Map;

public class ApplicationBuilder {
    public static CLISessionController buildCLIApp() {

        // Strategy Registry
        Map<TransactionType, BillingStrategy> strategyMap = Map.of(
                TransactionType.OTC, new OtcBillingStrategy(),
                TransactionType.WEB, new WebBillingStrategy()
        );

        StockEventPublisher publisher = new StockEventPublisher();
        publisher.registerObserver(new ConsoleNotifier());

        // DAOs
        BillDao billDao = new BillDaoImpl();
        BillItemDao billItemDao = new BillItemDaoImpl();
        ProductDaoImpl productDao = new ProductDaoImpl();
        CustomerDaoImpl customerDao = new CustomerDaoImpl();
        EmployeeDaoImpl employeeDao = new EmployeeDaoImpl();
        ShelfDao shelfDao = new ShelfDaoImpl();
        StoreDao storeDao = new StoreDaoImpl();
        WebInventoryDao webInventoryDao = new WebInventoryDaoImpl();
        BusinessDayLogDao businessDayLogDao = new BusinessDayLogDaoImpl();


        // Services
        AuthenticationService authenticationService = new AuthenticationService(customerDao, employeeDao);
        BillItemService billItemService = new BillItemService(productDao);
        ProductService productService = new ProductService(productDao);
        ShelfService shelfService = new ShelfService(shelfDao, storeDao, publisher);
        WebInventoryService webInventoryService = new WebInventoryService(webInventoryDao);
        AllocationService allocationService = new AllocationServiceImpl(new FEFOStockSelectionStrategy(), storeDao, shelfDao, webInventoryDao);
        StockDeductionProcessor stockDeductionProcessor = new StockDeductionProcessor(webInventoryService, shelfService);
        BillingService billingService = new BillingService(billDao, new BillItemDaoImpl(), billItemService, customerDao, strategyMap,  List.of(stockDeductionProcessor));
        ReportGenerationService reportService = new ReportGenerationService(billDao, billItemDao, shelfDao, storeDao);


        // Command
        List<BusinessDayCommand> businessDayCommands = List.of(
                new LogBusinessDayCommand(businessDayLogDao),
                new ExpireStockCommand(storeDao),
                new GenerateReportsCommand(reportService)
        );

        BusinessDayOrchestrator orchestrator = new BusinessDayOrchestrator(businessDayCommands);
        BusinessDayService businessDayService = new BusinessDayService(orchestrator, businessDayLogDao);

        return new CLISessionController(authenticationService, billingService, productService, shelfService, allocationService, webInventoryService, reportService, businessDayService);
    }

}
