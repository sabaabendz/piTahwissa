package tn.esprit.tahwisa.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.tahwisa.models.Destination;
import tn.esprit.tahwisa.models.PointInteret;
import tn.esprit.tahwisa.services.DestinationService;
import tn.esprit.tahwisa.services.EmailService;
import tn.esprit.tahwisa.services.PointInteretService;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EmailController implements Initializable {

    @FXML private TextField txtTo;
    @FXML private TextField txtSubject;
    @FXML private TextArea txtMessage;
    @FXML private TextField txtAutoEmail;
    @FXML private TextArea txtHistory;
    @FXML private Button btnSendCustom;
    @FXML private Button btnTestConnection;

    private EmailService emailService;
    private DestinationService destinationService;
    private PointInteretService pointInteretService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emailService = new EmailService();
        destinationService = new DestinationService();
        pointInteretService = new PointInteretService();

        txtAutoEmail.setText("admin@tahwissa.com");
        addToHistory("✅ Module Email initialisé");
    }

    // ==================== TEST CONNEXION ====================
    @FXML
    private void testConnection() {
        btnTestConnection.setDisable(true);
        btnTestConnection.setText("⏳ Test en cours...");

        new Thread(() -> {
            boolean success = emailService.testConnection();

            Platform.runLater(() -> {
                if (success) {
                    showSuccess("Connexion SMTP", "✅ Connexion réussie !");
                    addToHistory("✅ Test de connexion SMTP réussi");
                } else {
                    showError("Connexion SMTP", "❌ Échec de la connexion. Vérifiez la configuration.");
                    addToHistory("❌ Test de connexion SMTP échoué");
                }
                btnTestConnection.setDisable(false);
                btnTestConnection.setText("🔌 Tester Connexion");
            });
        }).start();
    }

    // ==================== ENVOYER EMAIL PERSONNALISÉ ====================
    @FXML
    private void sendCustomEmail() {
        String to = txtTo.getText().trim();
        String subject = txtSubject.getText().trim();
        String message = txtMessage.getText().trim();

        if (to.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            showWarning("Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (!isValidEmail(to)) {
            showWarning("Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        btnSendCustom.setDisable(true);
        btnSendCustom.setText("📤 Envoi...");

        new Thread(() -> {
            try {
                emailService.sendSimpleEmail(to, subject, message);

                Platform.runLater(() -> {
                    showSuccess("Email envoyé", "✅ Email envoyé avec succès à " + to);
                    addToHistory(String.format("📧 Email envoyé à %s - Sujet: %s", to, subject));
                    clearForm();
                    btnSendCustom.setDisable(false);
                    btnSendCustom.setText("📤 Envoyer");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur d'envoi", "❌ Impossible d'envoyer l'email : " + e.getMessage());
                    addToHistory("❌ Échec envoi email : " + e.getMessage());
                    btnSendCustom.setDisable(false);
                    btnSendCustom.setText("📤 Envoyer");
                });
            }
        }).start();
    }

    // ==================== EMAILS AUTOMATIQUES ====================

    @FXML
    private void sendTestDestinationEmail() {
        String to = txtAutoEmail.getText().trim();
        if (to.isEmpty() || !isValidEmail(to)) {
            showWarning("Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        new Thread(() -> {
            try {
                // Créer une destination de test
                Destination testDest = new Destination();
                testDest.setIdDestination(999);
                testDest.setNom("Djerba");
                testDest.setPays("Tunisie");
                testDest.setVille("Houmt Souk");
                testDest.setDescription("Île paradisiaque avec plages magnifiques et culture berbère");

                emailService.sendDestinationCreatedEmail(to, testDest);

                Platform.runLater(() -> {
                    showSuccess("Email envoyé", "✅ Email de test envoyé : Destination créée");
                    addToHistory("📍 Email de test (Destination) envoyé à " + to);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur", "❌ " + e.getMessage());
                    addToHistory("❌ Échec email destination test : " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void sendTestPointInteretEmail() {
        String to = txtAutoEmail.getText().trim();
        if (to.isEmpty() || !isValidEmail(to)) {
            showWarning("Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        new Thread(() -> {
            try {
                PointInteret testPoint = new PointInteret();
                testPoint.setIdPointInteret(888);
                testPoint.setNom("Plage de Sidi Mahrez");
                testPoint.setType("plage");
                testPoint.setDescription("Magnifique plage de sable blanc avec eaux turquoise");
                testPoint.setDestinationId(999);

                emailService.sendPointInteretCreatedEmail(to, testPoint, "Djerba");

                Platform.runLater(() -> {
                    showSuccess("Email envoyé", "✅ Email de test envoyé : Point d'intérêt créé");
                    addToHistory("🏖️ Email de test (Point d'intérêt) envoyé à " + to);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur", "❌ " + e.getMessage());
                    addToHistory("❌ Échec email point d'intérêt test : " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void sendWeeklyReport() {
        String to = txtAutoEmail.getText().trim();
        if (to.isEmpty() || !isValidEmail(to)) {
            showWarning("Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        new Thread(() -> {
            try {
                List<Destination> destinations = destinationService.afficherDestinations();
                List<PointInteret> points = pointInteretService.afficherPointsInteret();

                List<String> recentDest = destinations.stream()
                        .limit(5)
                        .map(d -> d.getNom() + " (" + d.getPays() + ")")
                        .toList();

                emailService.sendWeeklyReport(to, destinations.size(), points.size(), recentDest);

                Platform.runLater(() -> {
                    showSuccess("Rapport envoyé", "✅ Rapport hebdomadaire envoyé avec succès !");
                    addToHistory(String.format("📊 Rapport hebdomadaire envoyé à %s - %d destinations, %d points",
                            to, destinations.size(), points.size()));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur", "❌ " + e.getMessage());
                    addToHistory("❌ Échec envoi rapport : " + e.getMessage());
                });
            }
        }).start();
    }

    // ==================== HELPERS ====================

    @FXML
    private void clearForm() {
        txtTo.clear();
        txtSubject.clear();
        txtMessage.clear();
    }

    private void addToHistory(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String entry = String.format("[%s] %s\n", timestamp, message);
        txtHistory.appendText(entry);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}