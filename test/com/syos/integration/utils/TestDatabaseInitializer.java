package com.syos.integration.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabaseInitializer {

    public static void initializeSchema(Connection conn) throws SQLException {
        String schemaSql = """
            CREATE TABLE IF NOT EXISTS Customer (
              CustomerID INT AUTO_INCREMENT PRIMARY KEY,
              Name VARCHAR(100) NOT NULL,
              Email VARCHAR(100) NOT NULL UNIQUE,
              Username VARCHAR(50) NOT NULL,
              Address VARCHAR(255),
              RegistrationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
              Password VARCHAR(255) NOT NULL
            );

            CREATE TABLE IF NOT EXISTS Employee (
              EmployeeID INT PRIMARY KEY,
              Email VARCHAR(255) NOT NULL,
              Username VARCHAR(100) NOT NULL UNIQUE,
              Password VARCHAR(255) NOT NULL,
              Role VARCHAR(50) NOT NULL,
              Status VARCHAR(50) DEFAULT 'ACTIVE',
              CreatedAt TIMESTAMP NOT NULL
            );

            CREATE TABLE IF NOT EXISTS Product (
              ItemCode VARCHAR(50) NOT NULL PRIMARY KEY,
              ItemName VARCHAR(100) NOT NULL,
              Price DOUBLE NOT NULL,
              IsActive BOOLEAN DEFAULT TRUE,
              IsDeleted BOOLEAN DEFAULT FALSE,
              UpdatedDateTime TIMESTAMP DEFAULT NULL,
              UpdatedBy INT,
              Status VARCHAR(20) DEFAULT 'ACTIVE',
              ReorderLevel INT,
              FOREIGN KEY (UpdatedBy) REFERENCES Employee(EmployeeID)
            );

            -- Continue with WebProduct, Bill, etc.
        """;

        try (Statement stmt = conn.createStatement()) {
            for (String sql : schemaSql.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }
}