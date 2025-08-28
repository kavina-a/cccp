package server.application.businessDay;

import server.application.dto.request.BusinessDayLogRequest;
import server.domain.command.businessDay.intefaces.BusinessDayCommand;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BusinessDayOrchestrator {
    private final List<BusinessDayCommand> commands;

    public BusinessDayOrchestrator(List<BusinessDayCommand> commands) {
        this.commands = commands;
    }

    public void executeAll(Connection conn, BusinessDayLogRequest request) throws SQLException {
        for (BusinessDayCommand command : commands) {
            command.execute(conn, request);
        }
    }

}