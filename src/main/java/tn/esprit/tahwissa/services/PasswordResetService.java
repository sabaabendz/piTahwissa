package tn.esprit.tahwissa.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PasswordResetService {

    public static final class ResetToken {
        private final String code;
        private final Instant expiresAt;

        public ResetToken(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }

        public String getCode() {
            return code;
        }

        public Instant getExpiresAt() {
            return expiresAt;
        }
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<String, ResetToken> TOKENS = new ConcurrentHashMap<>();

    public ResetToken createResetToken(String email) {
        String code = generateCode();
        ResetToken token = new ResetToken(code, Instant.now().plusSeconds(15 * 60));
        TOKENS.put(email.toLowerCase(), token);
        return token;
    }

    public ResetToken getToken(String email) {
        if (email == null) {
            return null;
        }
        return TOKENS.get(email.toLowerCase());
    }

    private String generateCode() {
        int value = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(value);
    }
}


