package org.example;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public final class AuthService {
    private static final File BASE_DIR = new File(System.getProperty("user.home"), "filemanager");
    private static final File HOMES_DIR = new File(BASE_DIR, "homes");

    private AuthService() {
    }

    public static void ensureStorage() {
        if (!HOMES_DIR.exists()) {
            HOMES_DIR.mkdirs();
        }
    }

    public static String register(String login, String password, String email) throws IOException {
        ensureStorage();
        ensureDatabase();

        String normalizedLogin = normalize(login);
        String normalizedPassword = normalize(password);
        String normalizedEmail = normalize(email);

        if (!normalizedLogin.matches("[a-zA-Z0-9._-]{3,32}")) {
            return "Логин: 3-32 символа, только буквы/цифры/._-";
        }
        if (normalizedPassword.length() < 4) {
            return "Пароль должен быть не короче 4 символов";
        }
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            return "Введите корректный email";
        }

        String passwordHash = PasswordService.hash(normalizedPassword);

        boolean created;
        try {
            created = UserRepository.createUser(normalizedLogin, normalizedEmail, passwordHash);
        } catch (SQLException e) {
            throw new IOException("Failed to save user", e);
        }
        if (!created) {
            return "Пользователь с таким логином или email уже существует";
        }

        File userHome = userHome(normalizedLogin);
        if (!userHome.exists()) {
            userHome.mkdirs();
        }
        return null;
    }

    public static boolean authenticate(String login, String password) throws IOException {
        ensureStorage();
        ensureDatabase();
        String normalizedLogin = normalize(login);
        String normalizedPassword = normalize(password);
        if (normalizedLogin.isBlank() || normalizedPassword.isBlank()) {
            return false;
        }

        boolean authenticated;
        try {
            authenticated = UserRepository.authenticate(normalizedLogin, normalizedPassword);
        } catch (SQLException e) {
            throw new IOException("Failed to authenticate user", e);
        }
        if (authenticated) {
            File userHome = userHome(normalizedLogin);
            if (!userHome.exists()) {
                userHome.mkdirs();
            }
            return true;
        }

        return false;
    }

    public static File userHome(String login) throws IOException {
        ensureStorage();
        File home = new File(HOMES_DIR, normalize(login));
        if (!home.exists()) {
            home.mkdirs();
        }
        return home.getCanonicalFile();
    }

    public static File resolveInsideHome(String login, String path) throws IOException {
        File home = userHome(login);

        File target;
        if (path == null || path.isBlank()) {
            target = home;
        } else {
            target = new File(path).getCanonicalFile();
        }

        if (!isInsideHome(home, target)) {
            return null;
        }

        return target;
    }

    public static boolean isInsideHome(File home, File target) throws IOException {
        if (target == null) {
            return false;
        }
        String homePath = home.getCanonicalPath();
        String targetPath = target.getCanonicalPath();
        return targetPath.equals(homePath) || targetPath.startsWith(homePath + File.separator);
    }

    private static void ensureDatabase() throws IOException {
        try {
            UserRepository.ensureSchema();
        } catch (SQLException e) {
            throw new IOException("Failed to initialize database schema", e);
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
