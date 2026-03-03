package tn.esprit.tahwissa.services;

import okhttp3.*;
import tn.esprit.tahwissa.config.EnvConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HuggingFaceImageService {

    // Lire la clé depuis .env
    private static final String API_TOKEN = EnvConfig.get("HUGGINGFACE_API_KEY");
    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/black-forest-labs/FLUX.1-schnell";

    private final OkHttpClient client;

    public HuggingFaceImageService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();

        if (API_TOKEN == null || API_TOKEN.isEmpty()) {
            System.err.println("⚠️ ATTENTION: HUGGINGFACE_API_KEY non configurée!");
            System.err.println("   Veuillez ajouter HUGGINGFACE_API_KEY dans le fichier .env");
        } else {
            System.out.println("✅ HuggingFaceImageService initialisé avec clé API");
        }
    }

    public byte[] generateImage(String prompt) throws IOException {
        if (API_TOKEN == null || API_TOKEN.isEmpty()) {
            throw new IOException("HUGGINGFACE_API_KEY non configurée dans .env");
        }

        System.out.println("🖼️ Génération d'image avec prompt: " + prompt);
        System.out.println("🔑 Token utilisé: " + API_TOKEN.substring(0, 10) + "...");
        System.out.println("📡 URL API: " + API_URL);

        String json = "{\"inputs\": \"" + prompt + "\"}";
        System.out.println("📤 Requête JSON: " + json);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_TOKEN)
                .post(body)
                .build();

        System.out.println("📡 Envoi de la requête à Hugging Face...");

        try (Response response = client.newCall(request).execute()) {
            System.out.println("📥 Réponse reçue - Code: " + response.code());

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                System.err.println("❌ Corps de l'erreur: " + errorBody);

                if (errorBody.contains("loading")) {
                    throw new IOException("Le modèle est en cours de chargement. Veuillez réessayer dans 20-30 secondes.");
                }
                throw new IOException("Erreur API " + response.code() + ": " + errorBody);
            }

            byte[] imageBytes = response.body().bytes();
            System.out.println("✅ Image générée! Taille: " + imageBytes.length + " octets");
            return imageBytes;
        } catch (Exception e) {
            System.err.println("❌ Exception lors de l'appel API: " + e.getMessage());
            throw e;
        }
    }

    public String generateAndSaveImage(String destinationName, String country) throws IOException {
        String prompt = String.format(
                "Tourist destination, %s, %s, beautiful landscape, " +
                        "high quality, realistic photo, sunny day, 4k, detailed, travel photography",
                destinationName, country
        );

        System.out.println("🎨 Génération pour destination: " + destinationName);
        byte[] imageBytes = generateImage(prompt);

        String safeName = destinationName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_{2,}", "_");

        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "destination_" + safeName + "_" + timestamp + ".png";

        String projectPath = System.getProperty("user.dir");
        String resourcesPath = projectPath + "/src/main/resources/images/destinations/";

        System.out.println("📁 Chemin de sauvegarde: " + resourcesPath);

        File directory = new File(resourcesPath);
        if (!directory.exists()) {
            System.out.println("📁 Création du dossier: " + resourcesPath);
            directory.mkdirs();
        }

        String fullPath = resourcesPath + filename;
        File outputFile = new File(fullPath);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(imageBytes);
        }

        System.out.println("💾 Image sauvegardée: " + fullPath);

        String relativePath = "/images/destinations/" + filename;
        System.out.println("🔗 URL relative: " + relativePath);

        return relativePath;
    }

    public boolean testConnection() {
        if (API_TOKEN == null || API_TOKEN.isEmpty()) {
            System.err.println("❌ Impossible de tester: API_TOKEN manquant");
            return false;
        }

        try {
            System.out.println("🔍 Test de connexion à Hugging Face...");
            String json = "{\"inputs\": \"test\"}";

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("📥 Code réponse test: " + response.code());

                if (response.isSuccessful()) {
                    System.out.println("✅ Connexion réussie!");
                    return true;
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    System.out.println("❌ Échec: " + errorBody);
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Exception test: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}