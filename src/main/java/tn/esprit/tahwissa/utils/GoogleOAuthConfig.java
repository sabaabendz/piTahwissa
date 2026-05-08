package tn.esprit.tahwissa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GoogleOAuthConfig {

    public static final class Config {
        private final String clientId;
        private final String clientSecret;
        private final int redirectPort;

        public Config(String clientId, String clientSecret, int redirectPort) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectPort = redirectPort;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public int getRedirectPort() {
            return redirectPort;
        }
    }

    private GoogleOAuthConfig() {
    }

    public static Config load() throws IOException {
        String envClientId = System.getenv("GOOGLE_CLIENT_ID");
        String envClientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
        String envPort = System.getenv("GOOGLE_OAUTH_PORT");

        String clientId = (envClientId != null && !envClientId.isBlank()) ? envClientId.trim() : null;
        String clientSecret = (envClientSecret != null && !envClientSecret.isBlank()) ? envClientSecret.trim() : null;
        Integer port = null;

        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {
                port = null;
            }
        }

        if (clientId == null || clientSecret == null) {
            Properties props = new Properties();
            File configFile = resolveConfigFile();
            if (configFile != null && configFile.isFile()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                }
                if (clientId == null) {
                    clientId = trimToNull(props.getProperty("google.client.id"));
                }
                if (clientSecret == null) {
                    clientSecret = trimToNull(props.getProperty("google.client.secret"));
                }
                if (port == null) {
                    String value = trimToNull(props.getProperty("google.redirect.port"));
                    if (value != null) {
                        try {
                            port = Integer.parseInt(value);
                        } catch (NumberFormatException ignored) {
                            port = null;
                        }
                    }
                }
            }
        }

        if (clientId == null || clientSecret == null) {
            throw new IOException("Missing Google OAuth credentials. Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET or provide a config file.");
        }

        int redirectPort = (port != null && port > 0) ? port : 8888;
        return new Config(clientId, clientSecret, redirectPort);
    }

    private static File resolveConfigFile() {
        String configPath = trimToNull(System.getenv("GOOGLE_OAUTH_CONFIG"));
        if (configPath != null) {
            return new File(configPath);
        }

        File localConfig = new File("config", "google-oauth.properties");
        if (localConfig.isFile()) {
            return localConfig;
        }

        File userConfig = new File(new File(System.getProperty("user.home"), ".tahwissa"), "google-oauth.properties");
        if (userConfig.isFile()) {
            return userConfig;
        }

        return localConfig;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


