package server.application.reports.template;

import java.sql.SQLException;
import java.util.List;

public abstract class ReportTemplate<T> {

    public final void generateReport() throws SQLException {
        List<T> reportData = fetchData();
        aggregate(reportData);
        present(reportData);
    }

    protected abstract List<T> fetchData() throws SQLException;

    protected void aggregate(List<T> reportData) { }  // optional override

    protected abstract void present(List<T> reportData);
}