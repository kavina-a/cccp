package server.data.dao.interfaces;

import server.application.reports.data.RestockReportDTO;
import server.application.reports.data.StockReportDTO;
import server.domain.entity.StoreStock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StoreDao {

    void save(Connection conn, StoreStock storeStock) throws SQLException;

    List<StoreStock> findAvailableStockByItemCode(Connection conn, String itemCode) throws SQLException;

    void updateStockAfterAllocation(Connection conn, String itemCode, String batchCode, int quantityToReduce) throws SQLException;

    List<StoreStock> findByItemCode(Connection conn, String itemCode) throws SQLException;

    Optional<StoreStock> findByItemCodeAndBatchCode(Connection conn, String itemCode, String batchCode) throws SQLException;

    List<StockReportDTO> fetchBatchStockReport() throws SQLException;

    List<RestockReportDTO> fetchRestockReport() throws SQLException;

    void expireBatches(Connection conn) throws SQLException;

//    void adjustStock(String itemCode, String batchCode, int adjustmentQuantity, StoreDaoImpl.StockAdjustmentStrategy strategy);


}