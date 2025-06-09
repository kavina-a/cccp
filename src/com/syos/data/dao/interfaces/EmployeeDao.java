package com.syos.data.dao.interfaces;

import com.syos.domain.entity.Employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface EmployeeDao {
    Optional<Employee> findByUsername(Connection conn, String username);
    void save(Connection conn, Employee employee);
}
