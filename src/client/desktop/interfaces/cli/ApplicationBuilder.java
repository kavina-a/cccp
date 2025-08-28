package client.desktop.interfaces.cli;

import server.application.businessDay.BusinessDayOrchestrator;
import server.application.postprocessors.StockDeductionProcessor;
import server.application.reports.service.ReportGenerationService;
import server.data.dao.*;
import server.data.dao.interfaces.*;
import server.domain.command.businessDay.ExpireStockCommand;
import server.domain.command.businessDay.GenerateReportsCommand;
import server.domain.command.businessDay.LogBusinessDayCommand;
import server.domain.command.businessDay.intefaces.BusinessDayCommand;
import server.domain.entity.TransactionType;
import server.domain.notifications.ConsoleNotifier;
import server.domain.notifications.StockEventPublisher;
import server.domain.service.*;
import server.domain.service.interfaces.AllocationService;
import server.domain.strategy.stockselection.FEFOStockSelectionStrategy;
import server.domain.strategy.billing.OtcBillingStrategy;
import server.domain.strategy.billing.WebBillingStrategy;
import server.domain.strategy.billing.interfaces.BillingStrategy;

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
