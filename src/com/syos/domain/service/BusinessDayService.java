package com.syos.domain.service;

import com.syos.application.businessDay.BusinessDayOrchestrator;
import com.syos.application.dto.request.BusinessDayLogRequest;
import com.syos.data.dao.interfaces.BusinessDayLogDao;
import com.syos.utils.SQLTransactionManager;
import com.syos.utils.SessionManager;

import java.sql.SQLException;

public class BusinessDayService {

    private final BusinessDayOrchestrator orchestrator;
    private final BusinessDayLogDao businessDayLogDao;

    public BusinessDayService(BusinessDayOrchestrator orchestrator, BusinessDayLogDao businessDayLogDao) {
        this.orchestrator = orchestrator;
        this.businessDayLogDao = businessDayLogDao;
    }

    public void runDayStart() throws SQLException {
        String userId = SessionManager.getInstance().getLoggedInEmployee().getEmployeeID();

        if (userId == null) {
            throw new NullPointerException("Employee ID cannot be null");
        }

        BusinessDayLogRequest request = new BusinessDayLogRequest(false, userId);

        SQLTransactionManager.execute(conn -> {
            orchestrator.executeAll(conn, request);
            return null;
        });
    }

    public void runDayEnd() throws SQLException {
        String userId = SessionManager.getInstance().getLoggedInEmployee().getEmployeeID();

        if (userId == null) {
            throw new NullPointerException("Employee ID cannot be null");
        }

        BusinessDayLogRequest request = new BusinessDayLogRequest(true, userId);

        SQLTransactionManager.execute(conn -> {
            System.out.println("🌙 Ending business day...");
            orchestrator.executeAll(conn, request);
            System.out.println("✅ Day end process completed.");
            return null;
        });
    }

    public boolean isBusinessDayStarted() throws SQLException {
        return SQLTransactionManager.execute(conn -> {
            return businessDayLogDao.isDayStartedForToday(conn);
        });
    }
}
