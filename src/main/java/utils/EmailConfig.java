package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailConfig {

    public static final class Config {
        private final String apiUrl;
        private final String apiHost;
        private final String apiKey;
        private final String replyTo;
        private final String fromName;
        private final boolean enabled;

        public Config(String apiUrl, String apiHost, String apiKey, String replyTo, String fromName, boolean enabled) {
            this.apiUrl = apiUrl;
            this.apiHost = apiHost;
            this.apiKey = apiKey;
            this.replyTo = replyTo;
            this.fromName = fromName;
            this.enabled = enabled;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public String getApiHost() {
            return apiHost;
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
        String apiHost = trimToNull(System.getenv("RAPIDAPI_HOST"));
        String apiKey = trimToNull(System.getenv("RAPIDAPI_KEY"));
        String replyTo = trimToNull(System.getenv("MAIL_REPLY_TO"));
        String fromName = trimToNull(System.getenv("MAIL_FROM_NAME"));
        Boolean enabled = parseBoolean(System.getenv("MAIL_ENABLED"));

        if (apiUrl == null || apiHost == null || apiKey == null || enabled == null || replyTo == null || fromName == null) {
            Properties props = new Properties();
            File configFile = resolveConfigFile();
            if (configFile != null && configFile.isFile()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                }
                if (apiUrl == null) {
                    apiUrl = trimToNull(props.getProperty("mail.api.url"));
                }
                if (apiHost == null) {
                    apiHost = trimToNull(props.getProperty("mail.api.host"));
                }
                if (apiKey == null) {
                    apiKey = trimToNull(props.getProperty("mail.api.key"));
                }
                if (replyTo == null) {
                    replyTo = trimToNull(props.getProperty("mail.replyTo"));
                }
                if (fromName == null) {
                    fromName = trimToNull(props.getProperty("mail.fromName"));
                }
                if (enabled == null) {
                    enabled = parseBoolean(props.getProperty("mail.enabled"));
                }
            }
        }

        if (apiUrl == null) {
            apiUrl = "https://mail-sender-api1.p.rapidapi.com/";
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

        if (enabled && (apiHost == null || apiKey == null)) {
            throw new IOException("Missing mail API credentials. Set RAPIDAPI_HOST and RAPIDAPI_KEY, provide a config file, or set MAIL_ENABLED=false.");
        }

        return new Config(apiUrl, apiHost, apiKey, replyTo, fromName, enabled);
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
