package com.syos.data.dao.interfaces;

import com.syos.domain.entity.WebInventory;
import com.syos.domain.entity.WebProduct;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WebInventoryDao {

    void save(Connection conn, WebProduct webProduct) throws SQLException;

    Optional<WebProduct> findByItemCode(Connection conn, String itemCode) throws SQLException;

    List<WebProduct> findAll(Connection conn) throws SQLException;

    void logWebRestockTransaction(Connection conn, String itemCode, String batchCode, int quantityRestocked) throws SQLException;

    int getAvailableQuantity(Connection conn, String itemCode) throws SQLException;

    List<WebInventory> findActiveBatches(Connection conn, String itemCode) throws SQLException;

    void update(Connection conn, WebInventory webInventory) throws SQLException;
}
