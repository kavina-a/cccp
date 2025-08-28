package server.application.reports.restock.fetch;

import server.application.reports.data.RestockReportDTO;

import java.sql.SQLException;
import java.util.List;

public interface RestockFetchStrategy {
    List<RestockReportDTO> fetch() throws SQLException;
}