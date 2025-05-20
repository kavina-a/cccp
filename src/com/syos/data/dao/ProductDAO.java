package com.syos.data.dao;

import com.syos.data.dao.interfaces.ProductDaoImpl;
import com.syos.data.model.Product;
import com.syos.utils.HibernateTransactionManager;

import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ProductDAO implements ProductDaoImpl {

    @Override
    public void save(Product product) {
        HibernateTransactionManager.execute(session -> {
            session.persist(product);
            return null;
        });
    }

    @Override
    public Optional<Product> findByItemCode(String itemCode) {
        return HibernateTransactionManager.execute(session -> {
            Query<Product> query = session.createQuery(
                    "FROM Product WHERE itemCode = :itemCode AND isDeleted = false",
                    Product.class
            );
            query.setParameter("itemCode", itemCode);
            return query.uniqueResultOptional();
        });
    }

    @Override
    public List<Product> findAll() {
        return HibernateTransactionManager.execute(session -> {
            Query<Product> query = session.createQuery(
                    "FROM Product WHERE isDeleted = false",
                    Product.class
            );
            return query.list();
        });
    }

    @Override
    public void update(Product product) {
        HibernateTransactionManager.execute(session -> {
            session.merge(product);
            return null;
        });
    }

    @Override
    public void delete(String itemCode) {
        HibernateTransactionManager.execute(session -> {
            Query<Product> query = session.createQuery(
                    "FROM Product WHERE itemCode = :itemCode",
                    Product.class
            );
            query.setParameter("itemCode", itemCode);
            Optional<Product> result = query.uniqueResultOptional();
            result.ifPresent(product -> {
                product.setDeleted(true);
                session.merge(product);
            });
            return null;
        });
    }
}
