package com.syos.data.dao.interfaces;

import com.syos.application.dto.LowStockEventDTO;
import com.syos.application.dto.ReshelvedItemDTO;
import com.syos.domain.entity.Shelf;
import com.syos.domain.entity.ShelfInventory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShelfDao {
    void save(Connection conn, Shelf shelf) throws SQLException;

    boolean exists(Connection conn, String shelfLocation) throws SQLException;

    void logShelfRestockTransaction(Connection conn, String itemCode, String batchCode, int quantity, String shelfId, String updatedBy) throws SQLException;

    List<ShelfInventory> findActiveBatches(Connection conn, String itemCode) throws SQLException;

    void update(Connection conn, ShelfInventory batch) throws SQLException;

    List<ReshelvedItemDTO> fetchItemsPendingReshelving() throws SQLException;

    Optional<LowStockEventDTO> getTriggeredLowStockEvent(Connection conn, String itemCode) throws SQLException;

}

