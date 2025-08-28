package server.data.dao.interfaces;

import server.domain.entity.Employee;

import java.sql.Connection;
import java.util.Optional;

public interface EmployeeDao {
    Optional<Employee> findByUsername(Connection conn, String username);
    void save(Connection conn, Employee employee);
}
