package com.syos;

import com.syos.model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();

        Product p = new Product();
        p.setName("Test Product");
        p.setPrice(42.99);

        session.save(p);

        session.getTransaction().commit();
        session.close();
        factory.close();

        System.out.println("✅ Product saved!");
    }
}