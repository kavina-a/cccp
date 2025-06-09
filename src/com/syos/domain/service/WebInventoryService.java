package com.syos.domain.service;

import com.syos.application.dto.WebProductDTO;
import com.syos.application.dto.request.CreateWebInventoryRequest;
import com.syos.data.dao.interfaces.WebInventoryDao;
import com.syos.domain.entity.ProductStatus;
import com.syos.domain.entity.StockStatus;
import com.syos.domain.entity.WebInventory;
import com.syos.domain.entity.WebProduct;
import com.syos.utils.SQLTransactionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WebInventoryService {

    private final WebInventoryDao webInventoryDao;

    public WebInventoryService(WebInventoryDao webInventoryDao) {
        this.webInventoryDao = webInventoryDao;
    }

    public WebProductDTO createWebProduct(CreateWebInventoryRequest request) {
        return SQLTransactionManager.execute(conn -> {
            WebProduct product = new WebProduct();
            product.setItemCode(request.getItemCode());
            product.setItemName(request.getItemName());
            product.setPrice(request.getPrice());
            product.setStatus(ProductStatus.ACTIVE);
            product.setLastUpdated(LocalDateTime.now());
            webInventoryDao.save(conn, product);
            return null;
        });
    }

    public List<WebProductDTO> getAllWebItems() {
        return SQLTransactionManager.execute(connection ->
                webInventoryDao.findAll(connection).stream()
                        .map(this::mapToDTO)
                        .collect(Collectors.toList())
        );
    }

    public Optional<WebProductDTO> getAvailableProduct(String itemCode) {
        return SQLTransactionManager.execute(conn ->
                webInventoryDao.findByItemCode(conn, itemCode)
                        .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                        .map(this::mapToDTO) // convert to DTO
        );
    }

    public int getAvailableQuantity(String itemCode) {
        return SQLTransactionManager.execute(conn -> webInventoryDao.getAvailableQuantity(conn, itemCode));
    }

    public void deductStock(Connection conn, String itemCode, int quantity) throws SQLException {
        List<WebInventory> batches = webInventoryDao.findActiveBatches(conn, itemCode);

        for (WebInventory batch : batches) {
            if (quantity <= 0) break;

            int available = batch.getQuantityRemaining();
            int toDeduct = Math.min(quantity, available);

            batch.setQuantityRemaining(available - toDeduct);

            if (batch.getQuantityRemaining() == 0) {
                batch.setStatus(StockStatus.CONSUMED);
            }

            webInventoryDao.update(conn, batch);

            quantity -= toDeduct;
        }

    }


    private WebProductDTO mapToDTO(WebProduct entity) {
        return new WebProductDTO(
                entity.getItemCode(),
                entity.getItemName(),
                entity.getPrice(),
                entity.getLastUpdated()
        );
    }
}


    // TO DO - implement update method
//    public WebInventoryDTO updateWebItem(CreateWebInventoryRequest request) {
//        return SQLTransactionManager.execute(connection -> {
//            Optional<WebInventory> optional = webInventoryDao.findByItemCode(connection, request.getItemCode());
//            if (optional.isEmpty()) {
//                throw new IllegalArgumentException("Item not found: " + request.getItemCode());
//            }
//
//            WebInventory item = optional.get();
//            item.setItemName(request.getItemName());
//            item.setPrice(request.getPrice());
//            item.setQuantity(request.getQuantity());
//            item.setUpdatedDateTime(LocalDateTime.now());
////            item.setLastUpdatedBy(request.getUpdatedBy());
//
//            webInventoryDao.update(connection, item);
//            return mapToDTO(item);
//        });


