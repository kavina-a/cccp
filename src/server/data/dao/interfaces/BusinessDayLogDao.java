package server.data.dao.interfaces;

import server.application.dto.request.BusinessDayLogRequest;

import java.sql.Connection;
import java.sql.SQLException;

public interface BusinessDayLogDao {
    void logBusinessDay(Connection conn, BusinessDayLogRequest request) throws SQLException;
    boolean isDayStartedForToday(Connection connection) throws SQLException;
}