package server.data.dao.interfaces;

import server.domain.entity.Customer;

import java.sql.Connection;
import java.util.Optional;

public interface CustomerDao {
    void save(Connection conn, Customer customer) ;
    Optional<Customer> findByEmail(Connection conn, String email) ;
    Optional<Customer> findById(Connection conn, String id);
}