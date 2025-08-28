package com.syos.service;

import server.application.businessDay.BusinessDayOrchestrator;
import server.data.dao.interfaces.BusinessDayLogDao;
import server.domain.entity.Employee;
import server.domain.service.BusinessDayService;
import server.utils.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessDayServiceTest {

    private BusinessDayOrchestrator orchestrator;
    private BusinessDayLogDao businessDayLogDao;
    private BusinessDayService service;

    @BeforeEach
    void setup() {
        orchestrator = mock(BusinessDayOrchestrator.class);
        businessDayLogDao = mock(BusinessDayLogDao.class);
        service = new BusinessDayService(orchestrator, businessDayLogDao);
    }

    @Test
    void runDayStart_shouldCallOrchestratorWithCorrectRequest() throws SQLException {
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager mockSession = mock(SessionManager.class);
            Employee mockEmployee = mock(Employee.class);
            when(mockEmployee.getEmployeeID()).thenReturn("EMP123");
            when(mockSession.getLoggedInEmployee()).thenReturn(mockEmployee);
            sessionMock.when(SessionManager::getInstance).thenReturn(mockSession);

            service.runDayStart();

            verify(orchestrator, times(1)).executeAll(any(Connection.class),
                    argThat(req -> !req.getIsClosed() && "EMP123".equals(req.getInitiatedBy())));
        }
    }

    @Test
    void runDayEnd_shouldCallOrchestratorWithCorrectRequest() throws SQLException {
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager mockSession = mock(SessionManager.class);
            Employee mockEmployee = mock(Employee.class);
            when(mockEmployee.getEmployeeID()).thenReturn("EMP321");
            when(mockSession.getLoggedInEmployee()).thenReturn(mockEmployee);
            sessionMock.when(SessionManager::getInstance).thenReturn(mockSession);

            service.runDayEnd();

            verify(orchestrator, times(1)).executeAll(any(Connection.class),
            argThat(req -> req.getIsClosed() && "EMP321".equals(req.getInitiatedBy())));
        }
    }

    @Test
    void isBusinessDayStarted_shouldReturnDaoResult() throws SQLException {
        when(businessDayLogDao.isDayStartedForToday(any())).thenReturn(true);

        boolean result = service.isBusinessDayStarted();
        assertTrue(result);

        verify(businessDayLogDao).isDayStartedForToday(any());
    }

    // 🧪 Edge Case: DAO returns false
    @Test
    void isBusinessDayStarted_shouldReturnFalseWhenDaoReturnsFalse() throws SQLException {
        when(businessDayLogDao.isDayStartedForToday(any())).thenReturn(false);

        boolean result = service.isBusinessDayStarted();
        assertFalse(result);
    }

    // 🧪 Edge Case: SessionManager returns null employee
    @Test
    void runDayStart_shouldThrow_whenSessionReturnsNullEmployee() {
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager mockSession = mock(SessionManager.class);
            when(mockSession.getLoggedInEmployee()).thenReturn(null);
            sessionMock.when(SessionManager::getInstance).thenReturn(mockSession);

            assertThrows(NullPointerException.class, () -> service.runDayStart());
        }
    }

    // 🧪 Edge Case: Employee ID is null
    @Test
    void runDayEnd_shouldThrow_whenEmployeeIDIsNull() {
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager mockSession = mock(SessionManager.class);
            Employee mockEmployee = mock(Employee.class);
            when(mockEmployee.getEmployeeID()).thenReturn(null);  // force null
            when(mockSession.getLoggedInEmployee()).thenReturn(mockEmployee);
            sessionMock.when(SessionManager::getInstance).thenReturn(mockSession);

            assertThrows(NullPointerException.class, () -> service.runDayEnd());
        }
    }

    // 🧪 Edge Case: Orchestrator throws SQLException
    @Test
    void runDayStart_shouldWrapSQLExceptionInRuntimeException() throws SQLException {
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager mockSession = mock(SessionManager.class);
            Employee mockEmployee = mock(Employee.class);
            when(mockEmployee.getEmployeeID()).thenReturn("EMP999");
            when(mockSession.getLoggedInEmployee()).thenReturn(mockEmployee);
            sessionMock.when(SessionManager::getInstance).thenReturn(mockSession);

            doThrow(new SQLException("Orchestration failure"))
                    .when(orchestrator).executeAll(any(), any());

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.runDayStart());
            assertTrue(ex.getCause() instanceof SQLException);
            assertEquals("Orchestration failure", ex.getCause().getMessage());
        }
    }
}