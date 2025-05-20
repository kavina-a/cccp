package com.syos.data.dao.interfaces;

import com.syos.data.model.Product;

import java.util.Optional;
import java.util.List;

public interface ProductDaoImpl {
    void save(Product product);

    Optional<Product> findByItemCode(String itemCode);

    List<Product> findAll();

    void update(Product product);

    void delete(String itemCode);
}
