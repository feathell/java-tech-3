package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public final class HibernateUtil {
    private static final String DB_URL = readConfig(
            "explorer.db.url",
            "EXPLORER_DB_URL",
            "jdbc:mysql://localhost:3306/explorer?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );
    private static final String DB_USER = readConfig(
            "explorer.db.user",
            "EXPLORER_DB_USER",
            "root"
    );
    private static final String DB_PASSWORD = readConfig(
            "explorer.db.password",
            "EXPLORER_DB_PASSWORD",
            "root"
    );
    private static final String HBM2DDL = readConfig(
            "explorer.hibernate.hbm2ddl",
            "EXPLORER_HIBERNATE_HBM2DDL",
            "update"
    );
    private static final String SHOW_SQL = readConfig(
            "explorer.hibernate.show_sql",
            "EXPLORER_HIBERNATE_SHOW_SQL",
            "false"
    );

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static Session openSession() {
        return SESSION_FACTORY.openSession();
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            Properties properties = new Properties();

            properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
            properties.put(Environment.URL, DB_URL);
            properties.put(Environment.USER, DB_USER);
            properties.put(Environment.PASS, DB_PASSWORD);
            properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
            properties.put(Environment.HBM2DDL_AUTO, HBM2DDL);
            properties.put(Environment.SHOW_SQL, SHOW_SQL);
            properties.put(Environment.FORMAT_SQL, "true");

            configuration.setProperties(properties);
            configuration.addAnnotatedClass(UserEntity.class);

            return configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Hibernate SessionFactory", e);
        }
    }

    private static String readConfig(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        return defaultValue;
    }
}
