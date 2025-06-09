package com.syos.domain.command.businessDay;

import com.syos.application.dto.request.BusinessDayLogRequest;
import com.syos.data.dao.interfaces.BusinessDayLogDao;
import com.syos.domain.command.businessDay.intefaces.BusinessDayCommand;

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
