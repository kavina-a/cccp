package server.data.dao;

import server.data.dao.interfaces.EmployeeDao;
import server.domain.entity.Employee;
import server.domain.entity.EmployeeRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class EmployeeDaoImpl implements EmployeeDao {


    @Override
    public Optional<Employee> findByUsername(Connection conn, String username) {
        String sql = "SELECT * FROM Employee WHERE Username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToEntity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch employee by username: " + username, e);
        }
    }

    @Override
    public void save(Connection conn, Employee employee) {
        String sql = "INSERT INTO Employee (EmployeeID, Email, Username, Password, Role, Status, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getEmployeeID());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getUsername());
            stmt.setString(4, employee.getPassword());
            stmt.setString(5, employee.getRole().toString());
            stmt.setString(6, employee.getStatus());
            stmt.setTimestamp(7, new java.sql.Timestamp(employee.getCreatedAt().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save employee: " + employee.getUsername(), e);
        }
    }

    private Employee mapResultSetToEntity(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeID(rs.getString("EmployeeID"));
        e.setEmail(rs.getString("Email"));
        e.setUsername(rs.getString("Username"));
        e.setPassword(rs.getString("Password"));
        e.setRole(EmployeeRole.valueOf(rs.getString("Role")));
        e.setStatus(rs.getString("Status"));
        e.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return e;
    }
}
