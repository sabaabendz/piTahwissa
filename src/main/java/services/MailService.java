package services;

import utils.EmailConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class MailService {

    private final HttpClient httpClient;

    public MailService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendPasswordResetEmail(String toEmail, String name, String resetCode) throws IOException, InterruptedException {
        EmailConfig.Config config = EmailConfig.load();
        if (!config.isEnabled()) {
            return;
        }

        String subject = "Reinitialisation de mot de passe - Tahwissa";
        String body = buildBody(name, resetCode);

        String payload = "{" +
                "\"sendto\":" + jsonValue(toEmail) + "," +
                "\"name\":" + jsonValue(config.getFromName()) + "," +
                "\"replyTo\":" + jsonValue(config.getReplyTo()) + "," +
                "\"ishtml\":" + jsonValue("false") + "," +
                "\"title\":" + jsonValue(subject) + "," +
                "\"body\":" + jsonValue(body) +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getApiUrl()))
                .header("Content-Type", "application/json")
                .header("x-rapidapi-host", config.getApiHost())
                .header("x-rapidapi-key", config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Mail API error: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    private String buildBody(String name, String resetCode) {
        String safeName = (name == null || name.isBlank()) ? "" : name.trim();
        return "Bonjour " + safeName + ",\n\n" +
                "Vous avez demande la reinitialisation de votre mot de passe.\n" +
                "Code de verification: " + resetCode + "\n\n" +
                "Si vous n'etes pas a l'origine de cette demande, ignorez ce message.\n\n" +
                "Merci,\nEquipe Tahwissa";
    }

    private String jsonValue(String value) {
        String safe = value == null ? "" : value;
        String escaped = safe
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        return "\"" + escaped + "\"";
    }
}
