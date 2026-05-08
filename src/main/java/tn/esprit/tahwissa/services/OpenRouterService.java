package tn.esprit.tahwissa.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Service OpenRouter - Génération de descriptions avec IA
 * Version améliorée avec variations pour éviter les répétitions
 */
public class OpenRouterService {

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    private final Gson gson = new Gson();
    private final Random random = new Random();

    // Différents styles de prompts pour varier les descriptions
    private final List<String> promptTemplates = Arrays.asList(
            "Rédige une description touristique professionnelle de 3-4 phrases pour %s, %s. " +
                    "Mets en avant les atouts touristiques, le patrimoine, et donne envie de visiter. " +
                    "Réponds en français uniquement.",

            "Imagine que tu es un guide touristique. Décris %s, %s de manière passionnante en 3-4 phrases. " +
                    "Parle des lieux incontournables, de l'ambiance et de ce qui rend cette destination unique. " +
                    "Réponds en français.",

            "Écris une courte description attractive de %s, %s pour un site de voyage. " +
                    "3-4 phrases suffisent. Sois créatif et évite les clichés. " +
                    "Réponds en français.",

            "Présente la destination %s, %s à des voyageurs potentiels. " +
                    "Décris les expériences uniques qu'ils peuvent vivre, la culture locale, et les paysages. " +
                    "3-4 phrases en français."
    );

    // Différents angles d'approche pour les points d'intérêt
    private final List<String> pointTemplates = Arrays.asList(
            "Décris en 2-3 phrases le point d'intérêt '%s' (type: %s) situé à %s. " +
                    "Explique pourquoi il est spécial et ce qu'on peut y faire. Réponds en français.",

            "Présente le %s '%s' à %s. Que peut-on y voir ou y faire ? " +
                    "2-3 phrases en français.",

            "En tant que guide local, recommande le %s '%s' à %s. " +
                    "Dis pourquoi les touristes devraient le visiter. 2-3 phrases en français."
    );

    /**
     * Génère une description pour une destination avec variation
     */
    public String generateDestinationDescription(String destinationName, String country, String city) {
        // Choisir un template aléatoire
        String template = promptTemplates.get(random.nextInt(promptTemplates.size()));
        String prompt = String.format(template, destinationName, country);

        // Variation de la température pour plus de diversité
        double temperature = 0.5 + random.nextDouble() * 0.5; // Entre 0.5 et 1.0

        // Variation du nombre de tokens
        int maxTokens = 150 + random.nextInt(100); // Entre 150 et 250

        System.out.println("🎲 Génération avec template " + (promptTemplates.indexOf(template) + 1) +
                ", température: " + String.format("%.2f", temperature) +
                ", maxTokens: " + maxTokens);

        String response = callOpenRouter(prompt, maxTokens, temperature);

        if (response != null && !response.trim().isEmpty()) {
            return response.trim();
        }

        // Fallback avec différentes formulations
        return getFallbackDescription(destinationName, country);
    }

    /**
     * Génère une description pour un point d'intérêt avec variation
     */
    public String generatePointInteretDescription(String pointName, String type, String destinationName) {
        // Choisir un template aléatoire
        String template = pointTemplates.get(random.nextInt(pointTemplates.size()));
        String prompt = String.format(template, pointName, type, destinationName);

        double temperature = 0.6 + random.nextDouble() * 0.4; // Entre 0.6 et 1.0
        int maxTokens = 100 + random.nextInt(80); // Entre 100 et 180

        System.out.println("🎲 Génération point d'intérêt avec template " +
                (pointTemplates.indexOf(template) + 1) + ", température: " +
                String.format("%.2f", temperature));

        String response = callOpenRouter(prompt, maxTokens, temperature);

        if (response != null && !response.trim().isEmpty()) {
            return response.trim();
        }

        return String.format("Le %s %s est un lieu incontournable à %s. " +
                "Il offre une expérience unique aux visiteurs.", type, pointName, destinationName);
    }

    /**
     * Génère une description avec un style spécifique
     */
    public String generateDestinationDescriptionWithStyle(String destinationName, String country,
                                                          String style, String language) {
        String prompt = String.format(
                "Rédige une description touristique de %s, %s dans le style '%s'. " +
                        "Longueur: 3-4 phrases. Langue: %s.",
                destinationName, country, style, language
        );

        String response = callOpenRouter(prompt, 250, 0.9);
        return response != null ? response.trim() : generateDestinationDescription(destinationName, country, "");
    }

    /**
     * Appel à l'API OpenRouter avec paramètres personnalisables
     */
    private String callOpenRouter(String prompt, int maxTokens, double temperature) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);

            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + API_KEY);
            request.setHeader("HTTP-Referer", "http://localhost:8080");
            request.setHeader("X-Title", "Tahwissa Travel");

            JsonObject body = new JsonObject();
            body.addProperty("model", "google/gemini-2.0-flash-exp:free");
            body.addProperty("max_tokens", maxTokens);
            body.addProperty("temperature", temperature);

            // Ajouter un peu de randomisation supplémentaire
            body.addProperty("top_p", 0.9);
            body.addProperty("frequency_penalty", 0.3);
            body.addProperty("presence_penalty", 0.3);

            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);

            com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
            messages.add(message);
            body.add("messages", messages);

            request.setEntity(new StringEntity(body.toString(), "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String jsonResponse = EntityUtils.toString(response.getEntity());

                if (statusCode == 200) {
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    if (jsonObject.has("choices")) {
                        return jsonObject.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("message")
                                .get("content").getAsString();
                    }
                } else {
                    System.err.println("❌ Erreur API (code " + statusCode + "): " + jsonResponse);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur OpenRouter: " + e.getMessage());
        }
        return null;
    }

    /**
     * Descriptions de secours variées
     */
    private String getFallbackDescription(String destinationName, String country) {
        String[][] fallbacks = {
                {"%s est une destination de rêve en %s. Entre patrimoine historique et paysages à couper le souffle, elle séduit tous les voyageurs.",
                        "Découvrez %s, joyau de %s. Ses ruelles pittoresques et son atmosphère unique vous enchanteront."},
                {"%s vous attend en %s pour des vacances inoubliables. Nature, culture et gastronomie sont au rendez-vous.",
                        "Partez à la découverte de %s, perle de %s. Une expérience authentique vous y attend."},
                {"%s, située en %s, est une destination qui émerveille par sa beauté et sa richesse culturelle.",
                        "Laissez-vous séduire par %s en %s. Ses trésors cachés n'attendent que vous."}
        };

        int category = random.nextInt(fallbacks.length);
        int variant = random.nextInt(fallbacks[category].length);

        return String.format(fallbacks[category][variant], destinationName, country);
    }

    /**
     * Teste la connexion avec différents paramètres
     */
    public boolean testConnection() {
        try {
            String response = callOpenRouter("Réponds juste 'OK' en français", 10, 0.1);
            return response != null && response.contains("OK");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Génère une description avec un focus spécifique
     */
    public String generateDestinationDescriptionWithFocus(String destinationName, String country,
                                                          String focus) {
        String prompt = String.format(
                "Décris %s, %s en mettant l'accent sur %s. " +
                        "3-4 phrases en français, captivantes et informatives.",
                destinationName, country, focus
        );

        String response = callOpenRouter(prompt, 200, 0.8);
        return response != null ? response.trim() : generateDestinationDescription(destinationName, country, "");
    }
}