package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public final class UserRepository {
    private static volatile boolean schemaChecked;

    private UserRepository() {
    }

    public static void ensureSchema() throws SQLException {
        if (schemaChecked) {
            return;
        }
        synchronized (UserRepository.class) {
            if (schemaChecked) {
                return;
            }

            String sql = """
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        login VARCHAR(32) NOT NULL UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
                    )
                    """;

            try (Connection connection = DbConnectionFactory.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
            schemaChecked = true;
        }
    }

    public static boolean createUser(String login, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (login, email, password) VALUES (?, ?, ?)";

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }
    }

    public static boolean authenticate(String login, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE login = ?";

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }
                String storedPassword = resultSet.getString("password");
                return PasswordService.matches(password, storedPassword);
            }
        }
    }
}
