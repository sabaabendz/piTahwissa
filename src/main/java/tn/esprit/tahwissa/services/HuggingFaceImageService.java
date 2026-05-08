package tn.esprit.tahwissa.services;

import okhttp3.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class HuggingFaceImageService {

    // ⚠️ Votre clé API
    
    private static final String API_TOKEN = System.getenv("HUGGINGFACE_API_KEY");

    // URL correcte
    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/black-forest-labs/FLUX.1-schnell";

    private final OkHttpClient client;

    public HuggingFaceImageService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Génère une image à partir d'un prompt
     */
    public byte[] generateImage(String prompt) throws IOException {
        System.out.println("🖼️ Génération d'image avec prompt: " + prompt);

        String json = "{\"inputs\": \"" + prompt + "\"}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_TOKEN)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                System.err.println("❌ Erreur API: " + errorBody);

                if (errorBody.contains("loading")) {
                    throw new IOException("Le modèle est en cours de chargement. Veuillez réessayer dans 20-30 secondes.");
                }
                throw new IOException("Erreur API " + response.code() + ": " + errorBody);
            }

            byte[] imageBytes = response.body().bytes();
            System.out.println("✅ Image générée! Taille: " + imageBytes.length + " octets");
            return imageBytes;
        }
    }

    /**
     * Génère une image pour une destination
     */
    public byte[] generateDestinationImage(String destinationName, String country) throws IOException {
        String prompt = String.format(
                "Tourist destination, %s, %s, beautiful landscape, high quality, realistic photo, sunny day, 4k, detailed",
                destinationName, country
        );
        return generateImage(prompt);
    }

    /**
     * Génère une image pour un point d'intérêt
     */
    public byte[] generatePointInteretImage(String pointName, String type) throws IOException {
        String prompt = String.format(
                "%s, %s, tourist attraction, beautiful, high quality, realistic photo, detailed, 4k",
                pointName, type
        );
        return generateImage(prompt);
    }

    /**
     * UNE SEULE MÉTHODE - Génère une image et la sauvegarde
     * @param destinationName Nom de la destination
     * @param country Pays
     * @return Le chemin relatif de l'image
     */
    public String generateAndSaveImage(String destinationName, String country) throws IOException {
        String prompt = String.format(
                "Tourist destination, %s, %s, beautiful landscape, high quality, realistic photo",
                destinationName, country
        );

        byte[] imageBytes = generateImage(prompt);

        String safeName = destinationName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        String filename = "destination_" + safeName + "_" + System.currentTimeMillis() + ".png";

        String projectPath = System.getProperty("user.dir");
        String resourcesPath = projectPath + "/src/main/resources/images/destinations/";

        Files.createDirectories(Paths.get(resourcesPath));

        String fullPath = resourcesPath + filename;
        Files.write(Paths.get(fullPath), imageBytes);

        System.out.println("💾 Image sauvegardée: " + fullPath);

        return "/images/destinations/" + filename;
    }
}