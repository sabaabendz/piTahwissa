package tn.esprit.tahwissa.services;

import javax.json.*;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GrokService {
    // URL de l'API OpenRouter (compatible OpenAI)
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    // Votre clé API OpenRouter (gratuite)
    private static final String API_KEY = "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"; // collez votre clé OpenRouter

    private final HttpClient httpClient;

    public GrokService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        System.out.println("OpenRouter API Key: " + API_KEY.substring(0, Math.min(5, API_KEY.length())) + "...");
    }

    public String sendMessage(String userMessage) throws Exception {
        // Modèle gratuit recommandé : "meta-llama/llama-3.2-3b-instruct"
        JsonObject requestBody = Json.createObjectBuilder()
            .add("model", "meta-llama/llama-3.2-3b-instruct")  // modèle gratuit
            .add("messages", Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("role", "system")
                    .add("content", 
                        "Vous êtes l'assistant virtuel officiel de l'agence de voyage 'Tahwissa'. " +
                        "Votre objectif exclusif est d'aider les clients à découvrir nos offres de voyages, " +
                        "répondre à leurs questions sur le tourisme, et les guider dans le processus de réservation. " +
                        "RÈGLE ABSOLUE : Si l'utilisateur pose une question hors-sujet qui n'est pas strictement liée " +
                        "à notre agence de voyage, au tourisme, ou aux vacances (par exemple: les Pokémon, " +
                        "les jeux vidéo, la cuisine générale, le code informatique, etc.), vous DEVEZ poliment " +
                        "refuser de répondre en disant : 'Je suis l'assistant de l'agence de voyage Tahwissa. " +
                        "Je suis programmé pour répondre uniquement aux questions concernant nos voyages et " +
                        "nos destinations.'"
                    ))
                .add(Json.createObjectBuilder()
                    .add("role", "user")
                    .add("content", userMessage)))
            .add("temperature", 0.7)
            .add("max_tokens", 500)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + API_KEY)
            .header("HTTP-Referer", "http://localhost:8080") // optionnel
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .timeout(Duration.ofSeconds(60))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur OpenRouter : " + response.statusCode() + " - " + response.body());
        }

        JsonObject jsonResponse = Json.createReader(new StringReader(response.body())).readObject();
        return jsonResponse.getJsonArray("choices")
            .getJsonObject(0)
            .getJsonObject("message")
            .getString("content");
    }
}
