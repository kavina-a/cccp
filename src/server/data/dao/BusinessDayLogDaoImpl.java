package server.data.dao;

import server.application.dto.request.BusinessDayLogRequest;
import server.data.dao.interfaces.BusinessDayLogDao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BusinessDayLogDaoImpl implements BusinessDayLogDao {

    @Override
    public void logBusinessDay(Connection conn, BusinessDayLogRequest request) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (!request.getIsClosed()) {
            String sql = """
                INSERT INTO BusinessDayLog (businessDate, startTime, isClosed, openedById)
                VALUES (?, ?, ?, ?)
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(today));
                stmt.setTimestamp(2, Timestamp.valueOf(now));
                stmt.setBoolean(3, false);
                stmt.setString(4, request.getInitiatedBy());
                stmt.executeUpdate();
            }
        } else {
            String sql = """
                UPDATE BusinessDayLog
                SET endTime = ?, isClosed = ?, closedById = ?
                WHERE businessDate = ?
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, Timestamp.valueOf(now));
                stmt.setBoolean(2, true);
                stmt.setString(3, request.getInitiatedBy());
                stmt.setDate(4, Date.valueOf(today));
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public boolean isDayStartedForToday(Connection conn) throws SQLException {
        String sql = """
        SELECT COUNT(*) FROM BusinessDayLog 
        WHERE BusinessDate = CURRENT_DATE AND IsClosed = FALSE
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
