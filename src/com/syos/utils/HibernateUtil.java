//package com.syos.utils;
//
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//
//public class HibernateUtil {
//    private static final SessionFactory sessionFactory;
//
//    static {
//        try {
//            sessionFactory = new Configuration()
//                    .configure("hibernate.cfg.xml")
//                    .buildSessionFactory();
//        } catch (Throwable ex) {
//            throw new ExceptionInInitializerError("SessionFactory initialization failed: " + ex.getMessage());
//        }
//    }
//
//    public static SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }
//
//    //do we need it ???
//    public static void shutdown() {
//        sessionFactory.close();
//    }
//}
