package tn.esprit.tahwissa.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        // Symfony uses bcrypt hashes (often prefixed with $2y$).
        if (hashedPassword.startsWith("$2")) {
            try {
                String normalizedHash = hashedPassword.startsWith("$2y$")
                        ? "$2a$" + hashedPassword.substring(4)
                        : hashedPassword;
                return BCrypt.checkpw(password, normalizedHash);
            } catch (Exception ignored) {
                // Fall through to legacy checks.
            }
        }

        // Legacy SHA-256 support (older Java-side accounts).
        if (hashPassword(password).equals(hashedPassword)) {
            return true;
        }

        // Plain text fallback for existing non-hashed records.
        return password.equals(hashedPassword);
    }
}
