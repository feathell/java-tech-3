package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class AuthService {
    private static final File BASE_DIR = new File(System.getProperty("user.home"), "filemanager");
    private static final File USERS_DIR = new File(BASE_DIR, "users");
    private static final File HOMES_DIR = new File(BASE_DIR, "homes");

    private AuthService() {
    }

    public static void ensureStorage() {
        if (!USERS_DIR.exists()) {
            USERS_DIR.mkdirs();
        }
        if (!HOMES_DIR.exists()) {
            HOMES_DIR.mkdirs();
        }
    }

    public static String register(String login, String password, String email) throws IOException {
        ensureStorage();

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

        File userFile = userFile(normalizedLogin);
        if (userFile.exists()) {
            return "Пользователь уже существует";
        }

        Properties props = new Properties();
        props.setProperty("login", normalizedLogin);
        props.setProperty("email", normalizedEmail);
        props.setProperty("password", normalizedPassword);

        try (FileOutputStream out = new FileOutputStream(userFile)) {
            props.store(out, null);
        }

        File userHome = userHome(normalizedLogin);
        if (!userHome.exists()) {
            userHome.mkdirs();
        }
        return null;
    }

    public static boolean authenticate(String login, String password) throws IOException {
        ensureStorage();
        String normalizedLogin = normalize(login);
        String normalizedPassword = normalize(password);
        if (normalizedLogin.isBlank() || normalizedPassword.isBlank()) {
            return false;
        }

        File userFile = userFile(normalizedLogin);
        if (!userFile.exists()) {
            return false;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(userFile)) {
            props.load(in);
        }

        String storedPassword = props.getProperty("password", "");
        if (storedPassword.equals(normalizedPassword)) {
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

    private static File userFile(String login) {
        return new File(USERS_DIR, login + ".properties");
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
