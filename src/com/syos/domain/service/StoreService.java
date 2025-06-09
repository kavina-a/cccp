package com.syos.domain.service;

import com.syos.application.dto.StoreStockDTO;
import com.syos.application.dto.builder.StoreStockBuilder;
import com.syos.application.dto.request.CreateStockRequest;
import com.syos.data.dao.ProductDaoImpl;
import com.syos.data.dao.StoreDaoImpl;
import com.syos.domain.entity.Product;
import com.syos.domain.entity.StoreStock;
import com.syos.utils.SQLTransactionManager;
import com.syos.utils.SessionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class StoreService {
    private final StoreDaoImpl storeDaoImpl;
    private final ProductDaoImpl productDaoImpl;

    public StoreService() {
        this.storeDaoImpl = new StoreDaoImpl();
        this.productDaoImpl = new ProductDaoImpl();
    }

    public StoreService(ProductDaoImpl productDaoImpl, StoreDaoImpl storeDaoImpl) {
        this.productDaoImpl = productDaoImpl;
        this.storeDaoImpl = storeDaoImpl;
    }

    public void insertStoreStock(CreateStockRequest request) {
        validateRequest(request);

        SQLTransactionManager.execute(conn -> {
            Product product = productDaoImpl.findByItemCode(conn, request.getItemCode())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.getItemCode()));

            Optional<StoreStock> existingBatch = storeDaoImpl.findByItemCodeAndBatchCode(
                    conn, request.getItemCode(), request.getBatchCode());

            if (existingBatch.isPresent()) {
                throw new IllegalStateException("Batch already exists: " + request.getBatchCode());
            }

            String updatedBy = SessionManager.getInstance().getLoggedInEmployee().getEmployeeID();

            StoreStock newStock = new StoreStock();
            newStock.setItemCode(request.getItemCode());
            newStock.setBatchCode(request.getBatchCode());
            newStock.setInitialStock(request.getBatchQuantity());
            newStock.setCurrentStock(request.getBatchQuantity());
            newStock.setExpiryDate(request.getExpiryDate());
            newStock.setReceivedDate(LocalDateTime.now());
            newStock.setUpdatedDateTime(LocalDateTime.now());
            newStock.setUpdatedBy(updatedBy);
            storeDaoImpl.save(conn, newStock);

            return null;
        });
    }

    private void validateRequest(CreateStockRequest request) {
        if (request.getItemCode() == null || request.getItemCode().isBlank()) {
            throw new IllegalArgumentException("Item Code is required.");
        }
        if (request.getBatchCode() == null || request.getBatchCode().isBlank()) {
            throw new IllegalArgumentException("Batch Code is required.");
        }
        if (request.getBatchQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (request.getExpiryDate() == null || request.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiry date must be a future date.");
        }
    }

    public List<StoreStockDTO> getStoreStockDetails(String itemCode) {
        return SQLTransactionManager.execute(conn -> {
            List<StoreStock> stocks = storeDaoImpl.findByItemCode(conn, itemCode);
            List<StoreStockDTO> dtos = new ArrayList<>();

            for (StoreStock stock : stocks) {
                StoreStockDTO dto = new StoreStockDTO(
                        stock.getItemCode(),
                        stock.getBatchCode(),
                        stock.getInitialStock(),
                        stock.getCurrentStock(),
                        stock.getExpiryDate(),
                        stock.getReceivedDate()
                );
                dtos.add(dto);
            }

            return dtos;
        });

    }
}