package com.syos.integration;

import server.data.dao.ProductDaoImpl;
import server.domain.entity.Product;
import server.domain.entity.ProductStatus;
import com.syos.integration.utils.TestDatabaseInitializer;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductDaoIntegrationTest {

    private static Connection conn;
    private static ProductDaoImpl productDao;

    @BeforeAll
    static void setup() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        TestDatabaseInitializer.initializeSchema(conn);
        productDao = new ProductDaoImpl();

        // Insert dummy employee required for FK on Product
        try (var stmt = conn.prepareStatement("""
            INSERT INTO Employee (EmployeeID, Email, Username, Password, Role, Status, CreatedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "admin@example.com");
            stmt.setString(3, "admin");
            stmt.setString(4, "password123");
            stmt.setString(5, "ADMIN");
            stmt.setString(6, "ACTIVE");
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    @Test
    @Order(1)
    void shouldInsertAndFetchProduct() throws SQLException {
        Product product = new Product();
        product.setItemCode("ITEM001");
        product.setItemName("Item One");
        product.setPrice(100.0);
        product.setUpdatedDateTime(LocalDateTime.now());
        product.setUpdatedBy("1");
        product.setStatus(ProductStatus.ACTIVE);
        product.setReorderLevel(50);
        productDao.save(conn, product);

        Optional<Product> fetched = productDao.findByItemCode(conn, "ITEM001");
        assertTrue(fetched.isPresent());
        assertEquals("Item One", fetched.get().getItemName());
        assertEquals(100.0, fetched.get().getPrice());
    }

    @Test
    @Order(2)
    void shouldUpdateProduct() throws SQLException {
        Product product = productDao.findByItemCode(conn, "ITEM001").orElseThrow();
        product.setItemName("Updated Name");
        product.setPrice(199.99);
        product.setStatus(ProductStatus.ACTIVE);
        productDao.update(conn, product);

        Product updated = productDao.findByItemCode(conn, "ITEM001").orElseThrow();
        assertEquals("Updated Name", updated.getItemName());
        assertEquals(199.99, updated.getPrice());
    }

    @Test
    @Order(3)
    void shouldFetchAllProducts() throws SQLException {
        List<Product> products = productDao.findAll(conn);
        assertFalse(products.isEmpty());
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}