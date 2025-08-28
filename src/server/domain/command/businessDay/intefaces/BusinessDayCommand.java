package server.domain.command.businessDay.intefaces;

import server.application.dto.request.BusinessDayLogRequest;

import java.sql.Connection;
import java.sql.SQLException;

public interface BusinessDayCommand {
    void execute(Connection conn, BusinessDayLogRequest request) throws SQLException;
}