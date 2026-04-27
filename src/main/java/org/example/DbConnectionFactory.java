package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnectionFactory {
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

    private DbConnectionFactory() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
