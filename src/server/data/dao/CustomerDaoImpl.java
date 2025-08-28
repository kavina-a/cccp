package server.data.dao;

import server.data.dao.interfaces.CustomerDao;
import server.domain.entity.Customer;

import java.sql.*;
import java.util.Optional;

public class CustomerDaoImpl implements CustomerDao {

    @Override
    public void save(Connection conn, Customer customer) {
        String sql = "INSERT INTO Customer (Name, Email, Username, Address, RegistrationDate, Password) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getUsername());
            stmt.setString(4, customer.getAddress());
            stmt.setTimestamp(5, Timestamp.valueOf(customer.getRegistrationDate()));
            stmt.setString(6, customer.getPassword());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setCustomerID(generatedKeys.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save customer: " + customer.getEmail(), e);
        }
    }

    @Override
    public Optional<Customer> findByEmail(Connection conn, String email) {
        String sql = "SELECT * FROM Customer WHERE Email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToEntity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find customer by email: " + email, e);
        }
    }

    @Override
    public Optional<Customer> findById(Connection conn, String id) {
        String sql = "SELECT * FROM Customer WHERE CustomerID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToEntity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find customer by ID: " + id, e);
        }
    }

    private Customer mapResultSetToEntity(ResultSet rs) {
        try {
            Customer customer = new Customer();
            customer.setCustomerID(rs.getString("CustomerID"));
            customer.setName(rs.getString("Name"));
            customer.setEmail(rs.getString("Email"));
            customer.setUsername(rs.getString("Username"));
            customer.setAddress(rs.getString("Address"));
            customer.setRegistrationDate(rs.getTimestamp("RegistrationDate").toLocalDateTime());
            customer.setPassword(rs.getString("Password"));
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to map customer ResultSet", e);
        }
    }
}
