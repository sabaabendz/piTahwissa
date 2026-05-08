package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.utils.EmailConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

        JsonObject sender = new JsonObject();
        sender.addProperty("name", config.getFromName());
        sender.addProperty("email", config.getReplyTo());

        JsonObject to = new JsonObject();
        to.addProperty("email", toEmail);
        JsonArray toArray = new JsonArray();
        toArray.add(to);

        JsonObject json = new JsonObject();
        json.add("sender", sender);
        json.add("to", toArray);
        json.addProperty("subject", subject);
        json.addProperty("textContent", body);

        String payload = new Gson().toJson(json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getApiUrl()))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", config.getApiKey())
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

    public void sendReservationStatusEmail(String toEmail, String clientName, String voyageName, String status, String amount, String date, String reference) throws IOException, InterruptedException {
        EmailConfig.Config config = EmailConfig.load();
        if (!config.isEnabled()) {
            return;
        }

        String subject = "Mise à jour de votre réservation - Tahwissa";
        String body = buildReservationHtmlBody(clientName, voyageName, status, amount, date, reference);

        JsonObject sender = new JsonObject();
        sender.addProperty("name", "Tahwissa");
        sender.addProperty("email", config.getReplyTo());

        JsonObject to = new JsonObject();
        to.addProperty("email", toEmail);
        JsonArray toArray = new JsonArray();
        toArray.add(to);

        JsonObject json = new JsonObject();
        json.add("sender", sender);
        json.add("to", toArray);
        json.addProperty("subject", subject);
        json.addProperty("htmlContent", body);

        String payload = new Gson().toJson(json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getApiUrl()))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Mail API error: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    private String buildReservationHtmlBody(String clientName, String voyageName, String status, String amount, String date, String reference) {
        String safeName = (clientName == null || clientName.isBlank()) ? "Client" : clientName.trim();
        String statusColor = status.equalsIgnoreCase("CONFIRMÉE") || status.equalsIgnoreCase("CONFIRMEE") ? "#10B981" : "#EF4444";
        String statusMessage = status.equalsIgnoreCase("CONFIRMÉE") || status.equalsIgnoreCase("CONFIRMEE") 
                ? "Nous sommes impatients de vous faire voyager ! Préparez vos valises." 
                : "Votre réservation a été annulée. Si c'est une erreur ou si vous avez des questions, n'hésitez pas à nous contacter.";

        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e5e7eb; border-radius: 8px; overflow: hidden;\">" +
                "<div style=\"background-color: #4F46E5; padding: 20px; text-align: center; color: white;\">" +
                "<h1 style=\"margin: 0; font-size: 24px;\">Tahwissa</h1>" +
                "<p style=\"margin: 5px 0 0 0; font-size: 14px;\">Mise à jour de votre réservation</p>" +
                "</div>" +
                "<div style=\"padding: 20px; color: #374151;\">" +
                "<p>Bonjour <strong>" + safeName + "</strong>,</p>" +
                "<p>Le statut de votre réservation pour le voyage <strong>" + voyageName + "</strong> a été mis à jour.</p>" +
                "<div style=\"background-color: #f3f4f6; padding: 15px; border-radius: 6px; margin: 20px 0;\">" +
                "<p style=\"margin: 5px 0;\"><strong>Référence :</strong> " + reference + "</p>" +
                "<p style=\"margin: 5px 0;\"><strong>Date prévue :</strong> " + date + "</p>" +
                "<p style=\"margin: 5px 0;\"><strong>Montant :</strong> " + amount + " DT</p>" +
                "<p style=\"margin: 5px 0;\"><strong>Nouveau statut :</strong> <span style=\"color: " + statusColor + "; font-weight: bold;\">" + status + "</span></p>" +
                "</div>" +
                "<p>" + statusMessage + "</p>" +
                "<p style=\"margin-top: 30px;\">Merci de votre confiance,<br>L'équipe Tahwissa</p>" +
                "</div>" +
                "</div>";
    }

}

