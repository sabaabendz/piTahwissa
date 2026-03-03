package tn.esprit.tahwissa.services.payment;

import javax.json.*;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class FlouciService {
    // Utilisez l'environnement de test "developers.flouci.com" pour l'intégration
    private static final String API_BASE_URL = "https://developers.flouci.com/api/v2";
    private static final String PUBLIC_KEY = "votre_cle_publique";   // À mettre dans variables d'env
    private static final String PRIVATE_KEY = "votre_cle_privee";    // À mettre dans variables d'env

    private final HttpClient httpClient;

    public FlouciService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * Crée un paiement et retourne le lien de redirection.
     * @param amount montant en millimes (ex: 1000 pour 10 DT)
     * @param successUrl URL de redirection après succès
     * @param failUrl URL de redirection après échec
     * @param developerTrackingId (optionnel) votre identifiant de suivi
     * @return un objet contenant payment_id et link
     */
    public PaymentResult createPayment(int amount, String successUrl, String failUrl, String developerTrackingId) throws Exception {
        // Construction du corps de la requête
        JsonObjectBuilder bodyBuilder = Json.createObjectBuilder()
            .add("amount", amount)
            .add("success_link", successUrl)
            .add("fail_link", failUrl)
            .add("session_timeout_secs", 1200)   // valeur par défaut
            .add("accept_card", false)           // selon votre configuration
            .add("webhook", "https://votre-site.com/webhook") // facultatif
            .add("developer_tracking_id", developerTrackingId != null ? developerTrackingId : "");

        JsonObject requestBody = bodyBuilder.build();

        // Authentification : Bearer <PUBLIC_KEY>:<PRIVATE_KEY>
        String authToken = PUBLIC_KEY + ":" + PRIVATE_KEY;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_BASE_URL + "/generate_payment"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + authToken)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .timeout(Duration.ofSeconds(30))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Erreur Flouci : " + response.statusCode() + " - " + response.body());
        }

        JsonObject json = Json.createReader(new StringReader(response.body())).readObject();
        JsonObject result = json.getJsonObject("result");
        boolean success = result.getBoolean("success");
        if (!success) {
            throw new RuntimeException("Flouci a retourné success=false");
        }
        String paymentId = result.getString("payment_id");
        String link = result.getString("link");
        String trackingId = result.getString("developer_tracking_id", null);

        return new PaymentResult(paymentId, link, trackingId);
    }

    /**
     * Vérifie le statut d'un paiement.
     * @param paymentId l'identifiant du paiement retourné par Flouci
     * @return true si le paiement a réussi, false sinon (ou lève une exception si non trouvé)
     */
    public boolean verifyPayment(String paymentId) throws Exception {
        String authToken = PUBLIC_KEY + ":" + PRIVATE_KEY;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_BASE_URL + "/verify_payment/" + paymentId))
            .header("Authorization", "Bearer " + authToken)
            .GET()
            .timeout(Duration.ofSeconds(30))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur vérification Flouci : " + response.statusCode() + " - " + response.body());
        }

        JsonObject json = Json.createReader(new StringReader(response.body())).readObject();
        // Le format exact de la réponse de vérification est à adapter selon la doc
        // Exemple hypothétique : { "result": { "status": "paid" } }
        JsonObject result = json.getJsonObject("result");
        String status = result.getString("status"); // à vérifier avec la documentation
        return "paid".equalsIgnoreCase(status);
    }

    /**
     * Classe interne pour stocker le résultat de la création de paiement.
     */
    public static class PaymentResult {
        private final String paymentId;
        private final String link;
        private final String developerTrackingId;

        public PaymentResult(String paymentId, String link, String developerTrackingId) {
            this.paymentId = paymentId;
            this.link = link;
            this.developerTrackingId = developerTrackingId;
        }

        public String getPaymentId() { return paymentId; }
        public String getLink() { return link; }
        public String getDeveloperTrackingId() { return developerTrackingId; }
    }
}
