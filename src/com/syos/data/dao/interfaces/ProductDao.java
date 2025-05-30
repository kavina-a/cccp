package com.syos.data.dao.interfaces;

import com.syos.domain.entity.Product;

import java.util.Optional;
import java.util.List;

public interface ProductDao {
    void save(Product product);
    Optional<Product> findByItemCode(String itemCode);

    List<Product> findAll();

    void update(Product product);

    void deleteByItemCode(String itemCode);
}
