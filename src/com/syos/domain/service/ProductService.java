package com.syos.domain.service;

import com.syos.application.dto.ProductDTO;
import com.syos.application.dto.request.CreateProductRequest;
import com.syos.data.dao.interfaces.ProductDao;
import com.syos.domain.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Double getProductPriceByCode(String itemCode) {
        return productDao.findByItemCode(itemCode)
                .map(Product::getPrice)
                .orElse(null);
    }

    public Product getProductNameByCode(String itemCode) {
        return productDao.findByItemCode(itemCode)
                .orElse(null);
    }

    public Optional<Product> getProductByCode(String itemCode) {
        return productDao.findByItemCode(itemCode);
    }

    public void createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setItemCode(request.getItemCode());
        product.setItemName(request.getName());
//        product.setCategory(request.getCategory());  -- do we need this ?
        product.setPrice(request.getPrice());

        productDao.save(product);
    }

    public List<ProductDTO> getAllProducts() {
        return productDao.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductByItemCode(String itemCode) {
        return productDao.findByItemCode(itemCode).map(this::mapToDTO);
    }

    public Optional<ProductDTO> updateProduct(CreateProductRequest request) {
        Optional<Product> optionalProduct = productDao.findByItemCode(request.getItemCode());
        if (optionalProduct.isEmpty()) {
            return Optional.empty();
        }

        Product product = optionalProduct.get();
        product.setItemName(request.getName());
        product.setPrice(request.getPrice());
        productDao.update(product);

        return Optional.of(mapToDTO(product));
    }

    public boolean deleteProduct(String itemCode) {
        Optional<Product> optionalProduct = productDao.findByItemCode(itemCode);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setDeleted(true);
            productDao.update(product);
            return true;
        }
        return false;
    }

    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getItemCode(),
                product.getItemName(),
                product.getPrice()
        );
    }


}
