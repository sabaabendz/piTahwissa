package tn.esprit.tahwissa.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TestJson {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://mail-sender-api1.p.rapidapi.com/";
        String apiKey = "a2cfe3b631msha4bf68f8b3c67e3p15ee20jsn0926016c2f42";

        JsonObject json = new JsonObject();
        json.addProperty("sendto", "ghorbali02@gmail.com");
        json.addProperty("name", "Tahwissa Reservation");
        json.addProperty("replyTo", "support@tahwissa.com");
        json.addProperty("ishtml", "true");
        json.addProperty("title", "Mise a jour de votre reservation - Tahwissa");
        json.addProperty("body", "<b>Test Email</b>");

        String payload = new Gson().toJson(json);

        System.out.println("Payload: " + payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-rapidapi-host", "mail-sender-api1.p.rapidapi.com")
                .header("x-rapidapi-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }
}
