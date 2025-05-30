package com.syos.data.dao;

import com.syos.data.dao.interfaces.ProductDao;
import com.syos.domain.entity.Product;
import com.syos.utils.HibernateTransactionManager;
import java.util.List;
import java.util.Optional;

public class ProductDaoImpl implements ProductDao {

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
            String hql = "FROM Product p WHERE p.itemCode = :itemCode AND p.isDeleted = false";
            return session.createQuery(hql, Product.class)
                    .setParameter("itemCode", itemCode)
                    .uniqueResultOptional();
        });
    }

    @Override
    public List<Product> findAll() {
        return HibernateTransactionManager.execute(session -> {
            String hql = "FROM Product p WHERE p.isDeleted = false";
            return session.createQuery(hql, Product.class).list();
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
    public void deleteByItemCode(String itemCode) {
        HibernateTransactionManager.execute(session -> {
            String hql = "UPDATE Product p SET p.isDeleted = true WHERE p.itemCode = :itemCode";
            session.createQuery(hql)
                    .setParameter("itemCode", itemCode)
                    .executeUpdate();
            return null;
        });
    }

}
