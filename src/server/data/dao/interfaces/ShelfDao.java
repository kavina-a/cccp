package server.data.dao.interfaces;

import server.application.dto.LowStockEventDTO;
import server.application.dto.ReshelvedItemDTO;
import server.domain.entity.Shelf;
import server.domain.entity.ShelfInventory;

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

