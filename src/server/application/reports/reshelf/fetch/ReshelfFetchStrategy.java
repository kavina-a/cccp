package server.application.reports.reshelf.fetch;

import server.application.reports.data.ReshelfReportDTO;

import java.sql.SQLException;
import java.util.List;

public interface ReshelfFetchStrategy {
    List<ReshelfReportDTO> fetch() throws SQLException;
}