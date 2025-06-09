//package com.syos.domain.service;
//
//import com.syos.application.dto.ProductDTO;
//import com.syos.application.dto.request.CreateProductRequest;
//import com.syos.data.dao.interfaces.ProductDao;
//import com.syos.domain.entity.Product;
//import com.syos.utils.HibernateTransactionManager;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class ProductService {
//    private final ProductDao productDao;
//
//    public ProductService(ProductDao productDao) {
//        this.productDao = productDao;
//    }
//
//    public Double getProductPriceByCode(String itemCode) {
//        return HibernateTransactionManager.execute(session ->
//                productDao.findByItemCode(session, itemCode)
//                        .map(Product::getPrice)
//                        .orElse(null)
//        );
//    }
//
//    public Product getProductNameByCode(String itemCode) {
//        return HibernateTransactionManager.execute(session ->
//                productDao.findByItemCode(session, itemCode)
//                        .orElse(null)
//        );
//    }
//
//    public Optional<Product> getProductByCode(String itemCode) {
//        return HibernateTransactionManager.execute(session ->
//                productDao.findByItemCode(session, itemCode)
//        );
//    }
//
//    public void createProduct(CreateProductRequest request) {
//        HibernateTransactionManager.execute(session -> {
//            Product product = new Product();
//            product.setItemCode(request.getItemCode());
//            product.setItemName(request.getName());
//            // product.setCategory(request.getCategory()); // Uncomment if category is needed
//            product.setPrice(request.getPrice());
//
//            productDao.save(session, product);
//            return null; // why we returning null --
//        });
//    }
//
//    public List<ProductDTO> getAllProducts() {
//        return HibernateTransactionManager.execute(session -> {
//            List<Product> products = productDao.findAll(session);
//            List<ProductDTO> productDTOs = new ArrayList<>();
//
//            for (Product product : products) {
//                productDTOs.add(mapToDTO(product));
//            }
//
//            return productDTOs;
//        });
//    }
//
//    public Optional<ProductDTO> getProductByItemCode(String itemCode) {
//        return HibernateTransactionManager.execute(session ->
//                productDao.findByItemCode(session, itemCode).map(this::mapToDTO)
//        );
//    }
//
//    public Optional<ProductDTO> updateProduct(CreateProductRequest request) {
//        return HibernateTransactionManager.execute(session -> {
//            Optional<Product> optionalProduct = productDao.findByItemCode(session, request.getItemCode());
//            if (optionalProduct.isEmpty()) {
//                return Optional.empty();
//            }
//
//            Product product = optionalProduct.get();
//            product.setItemName(request.getName());
//            product.setPrice(request.getPrice());
//            productDao.update(session, product);
//
//            return Optional.of(mapToDTO(product));
//        });
//    }
//
//    public boolean deleteProduct(String itemCode) {
//        return HibernateTransactionManager.execute(session -> {
//            Optional<Product> optionalProduct = productDao.findByItemCode(session, itemCode);
//            if (optionalProduct.isPresent()) {
//                Product product = optionalProduct.get();
//                product.setDeleted(true);
//                productDao.update(session, product);
//                return true;
//            }
//            return false;
//        });
//    }
//
//    private ProductDTO mapToDTO(Product product) {
//        return new ProductDTO(
//                product.getItemCode(),
//                product.getItemName(),
//                product.getPrice()
//        );
//    }
//
//
//}

package com.syos.domain.service;

import com.syos.application.dto.ProductDTO;
import com.syos.application.dto.request.CreateProductRequest;
import com.syos.data.dao.interfaces.ProductDao;
import com.syos.domain.entity.Product;
import com.syos.utils.SQLTransactionManager;
import com.syos.utils.SessionManager;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Double getProductPriceByCode(String itemCode) {
        return SQLTransactionManager.execute(conn ->
                productDao.findByItemCode(conn, itemCode)
                        .map(Product::getPrice)
                        .orElse(null)
        );
    }

    public Product getProductNameByCode(String itemCode) {
        return SQLTransactionManager.execute(conn ->
                productDao.findByItemCode(conn, itemCode)
                        .orElse(null)
        );
    }

    public Optional<Product> getProductByCode(String itemCode) {
        return SQLTransactionManager.execute(conn ->
                productDao.findByItemCode(conn, itemCode)
        );
    }

    public void createProduct(CreateProductRequest request) {
        SQLTransactionManager.execute(conn -> {
            Product product = new Product();
            product.setItemCode(request.getItemCode());
            product.setItemName(request.getName());
            product.setPrice(request.getPrice());
            product.setReorderLevel(request.getReorderLevel());
            product.setUpdatedDateTime(LocalDateTime.now());


            String updatedBy = SessionManager.getInstance().getLoggedInEmployee().getEmployeeID();
            if (updatedBy == null) {
                throw new NullPointerException("Employee ID cannot be null");
            }
            product.setUpdatedBy(updatedBy);

            productDao.save(conn, product);
            return null;
        });
    }

    public List<ProductDTO> getAllProducts() {
        return SQLTransactionManager.execute(conn -> {
            List<Product> products = productDao.findAll(conn);
            List<ProductDTO> productDTOs = new ArrayList<>();
            for (Product product : products) {
                productDTOs.add(mapToDTO(product));
            }
            return productDTOs;
        });
    }

    public Optional<ProductDTO> getProductByItemCode(String itemCode) {
        return SQLTransactionManager.execute(conn ->
                productDao.findByItemCode(conn, itemCode).map(this::mapToDTO)
        );
    }


    public Optional<ProductDTO> updateProduct(CreateProductRequest request) {
        return SQLTransactionManager.execute(conn -> {
            Optional<Product> optionalProduct = productDao.findByItemCode(conn, request.getItemCode());
            if (optionalProduct.isEmpty()) {
                return Optional.empty();
            }
            Product product = optionalProduct.get();

            boolean updated = false;

            if (request.getName() != null && !request.getName().equals(product.getItemName())) {
                product.setItemName(request.getName());
                updated = true;
            }

//            if (request.getPrice() != null && !request.getPrice().equals(product.getPrice())) {
//                product.setPrice(request.getPrice());
//                updated = true;
//            }

            if (!updated) {
                return Optional.of(mapToDTO(product));
            }

            String updatedBy = SessionManager.getInstance().getLoggedInEmployee().getEmployeeID();

            product.setUpdatedDateTime(LocalDateTime.now());
            product.setUpdatedBy(updatedBy);

            productDao.update(conn, product);
            return Optional.of(mapToDTO(product));
        });
    }

    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getItemCode(),
                product.getItemName(),
                product.getPrice()
        );
    }
}
