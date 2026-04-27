package org.example;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordService {
    private PasswordService() {
    }

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank() || !isBcryptHash(storedPassword)) {
            return false;
        }

        try {
            return BCrypt.checkpw(rawPassword, storedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
