package server.domain.command.businessDay;

import server.application.dto.request.BusinessDayLogRequest;
import server.application.reports.service.ReportGenerationService;
import server.domain.command.businessDay.intefaces.BusinessDayCommand;
import server.domain.entity.ReportType;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class GenerateReportsCommand implements BusinessDayCommand {

    private final ReportGenerationService reportService;

    public GenerateReportsCommand(ReportGenerationService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void execute(Connection connection, BusinessDayLogRequest request) throws SQLException {

        if (!request.getIsClosed()) {
            // If the business day is not closed, we cannot generate reports
            // This is a safeguard added for now to prevent report generation during an open business day
            // Later, we might make two separate commands for generating reports at the start and end of the day
            return;
        }

        System.out.println("\n📊 GENERATING END-OF-DAY REPORTS...");
        reportService.generateTotalSaleReport(LocalDate.now(), ReportType.COMBINED);
        System.out.println();

        reportService.generateReshelfReport();
        System.out.println();

        reportService.generateRestockReport();
        System.out.println();

        System.out.println("All reports generated successfully.");
    }
}