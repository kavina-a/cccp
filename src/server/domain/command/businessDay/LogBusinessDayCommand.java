package server.domain.command.businessDay;

import server.application.dto.request.BusinessDayLogRequest;
import server.data.dao.interfaces.BusinessDayLogDao;
import server.domain.command.businessDay.intefaces.BusinessDayCommand;

import java.sql.Connection;
import java.sql.SQLException;

public class LogBusinessDayCommand implements BusinessDayCommand {

    private final BusinessDayLogDao businessDayLogDao;

    public LogBusinessDayCommand(BusinessDayLogDao businessDayLogDao) {
        this.businessDayLogDao = businessDayLogDao;
    }

    @Override
    public void execute(Connection connection, BusinessDayLogRequest request) throws SQLException {
        businessDayLogDao.logBusinessDay(connection, request);
    }
}
