package com.syos.domain.service;

import com.syos.application.dto.request.BillItemRequest;
import com.syos.data.dao.ProductDaoImpl;
import com.syos.data.dao.interfaces.ProductDao;
import com.syos.domain.entity.BillItem;
import com.syos.domain.entity.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillItemService {

    private final ProductDao productDao;

    public BillItemService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<BillItem> createBillItems(Connection conn, List<BillItemRequest> requests) {
        List<BillItem> billItems = new ArrayList<>();

        for (BillItemRequest req : requests) {
            try {
                Optional<Product> productOpt = productDao.findByItemCode(conn, req.getProductCode());
                if (productOpt.isEmpty()) {
                    throw new IllegalArgumentException("Invalid product code: " + req.getProductCode());
                }

                Product product = productOpt.get();

                BillItem item = new BillItem();
                item.setProduct(product);
                item.setQuantity(req.getQuantity());
                item.setPrice(req.getPricePerItem());

                billItems.add(item);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to fetch product for code: " + req.getProductCode(), e);
            }
        }

        return billItems;
    }

}