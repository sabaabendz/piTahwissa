package tn.esprit.tahwissa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailConfig {

    public static final class Config {
        private final String apiUrl;
        private final String apiKey;
        private final String replyTo;
        private final String fromName;
        private final boolean enabled;

        public Config(String apiUrl, String apiKey, String replyTo, String fromName, boolean enabled) {
            this.apiUrl = apiUrl;
            this.apiKey = apiKey;
            this.replyTo = replyTo;
            this.fromName = fromName;
            this.enabled = enabled;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getReplyTo() {
            return replyTo;
        }

        public String getFromName() {
            return fromName;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    private EmailConfig() {
    }

    public static Config load() throws IOException {
        String apiUrl = trimToNull(System.getenv("MAIL_SENDER_URL"));
        String apiKey = trimToNull(System.getenv("MAIL_API_KEY"));
        String replyTo = trimToNull(System.getenv("MAIL_REPLY_TO"));
        String fromName = trimToNull(System.getenv("MAIL_FROM_NAME"));
        Boolean enabled = parseBoolean(System.getenv("MAIL_ENABLED"));

        if (apiUrl == null || apiKey == null || enabled == null || replyTo == null || fromName == null) {
            Properties props = new Properties();
            File configFile = resolveConfigFile();
            
            boolean loaded = false;

            // 1. Try to load from external file
            if (configFile != null && configFile.isFile()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                    loaded = true;
                }
            } 
            
            // 2. Fallback to classpath config.properties that contains Stripe/Grok keys
            if (!loaded) {
                try (InputStream is = EmailConfig.class.getResourceAsStream("/config.properties")) {
                    if (is != null) {
                        props.load(is);
                        loaded = true;
                    }
                }
            }

            if (loaded) {
                if (apiUrl == null) apiUrl = trimToNull(props.getProperty("mail.api.url"));
                if (apiKey == null) apiKey = trimToNull(props.getProperty("mail.api.key"));
                if (replyTo == null) replyTo = trimToNull(props.getProperty("mail.replyTo"));
                if (fromName == null) fromName = trimToNull(props.getProperty("mail.fromName"));
                if (enabled == null) enabled = parseBoolean(props.getProperty("mail.enabled"));
            }
        }

        if (apiUrl == null) {
            apiUrl = "https://api.brevo.com/v3/smtp/email";
        }
        if (replyTo == null) {
            replyTo = "support@tahwissa.com";
        }
        if (fromName == null) {
            fromName = "Tahwissa";
        }
        if (enabled == null) {
            enabled = true;
        }

        // If enabled but keys are missing, just warn and disable so it doesn't crash the UI flow
        if (enabled && apiKey == null) {
            System.err.println("⚠️ Warning: Missing mail API credentials. Email notifications are disabled.");
            enabled = false;
        }

        return new Config(apiUrl, apiKey, replyTo, fromName, enabled);
    }

    private static File resolveConfigFile() {
        String configPath = trimToNull(System.getenv("MAIL_CONFIG"));
        if (configPath != null) {
            return new File(configPath);
        }

        File localConfig = new File("config", "mail.properties");
        if (localConfig.isFile()) {
            return localConfig;
        }

        File userConfig = new File(new File(System.getProperty("user.home"), ".tahwissa"), "mail.properties");
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

    private static Boolean parseBoolean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return Boolean.parseBoolean(trimmed);
    }
}

