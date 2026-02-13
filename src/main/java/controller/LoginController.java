package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Label statusLabel;
    @FXML private Label dbStatusLabel;
    @FXML private VBox registerForm;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML
    public void initialize() {
        System.out.println("✅ LoginController initialisé");

        // Valeur par défaut pour le choix du rôle
        if (roleChoiceBox != null) {
            roleChoiceBox.setValue("Voyageur");
        }

        // Tester la connexion DB
        testDatabaseConnection();
    }

    @FXML
    private void handleLogin() {
        String email = emailField != null ? emailField.getText() : "";
        String password = passwordField != null ? passwordField.getText() : "";

        if (email.isEmpty() || password.isEmpty()) {
            showStatus("❌ Veuillez remplir tous les champs", "#e74c3c");
            return;
        }

        // Simuler une connexion réussie
        showStatus("✅ Connexion réussie! Bienvenue " + email, "#27ae60");
    }

    @FXML
    private void handleRegisterToggle() {
        if (registerForm == null) return;

        boolean isVisible = registerForm.isVisible();
        registerForm.setManaged(!isVisible);
        registerForm.setVisible(!isVisible);

        if (loginButton != null) {
            loginButton.setDisable(!isVisible);
        }

        if (registerButton != null) {
            registerButton.setText(isVisible ? "S'inscrire" : "Annuler");
        }

        if (!isVisible) {
            showStatus("📝 Mode inscription activé", "#e67e22");
        } else {
            clearRegisterForm();
            showStatus("", "#666");
        }
    }

    @FXML
    private void handleRegister() {
        // Validation
        if (firstNameField == null || firstNameField.getText().isEmpty() ||
                lastNameField == null || lastNameField.getText().isEmpty()) {
            showStatus("❌ Veuillez remplir tous les champs", "#e74c3c");
            return;
        }

        // Simulation d'inscription réussie
        showStatus("✅ Inscription réussie! Vous pouvez vous connecter.", "#27ae60");

        // Revenir au mode connexion
        handleRegisterToggle();
    }

    @FXML
    private void handleForgotPassword() {
        showStatus("📧 Un email de réinitialisation a été envoyé", "#3498db");
    }

    private void testDatabaseConnection() {
        if (dbStatusLabel != null) {
            try {
                // Test avec ton UserService
                // services.UserService service = new services.UserService();
                // service.read();
                dbStatusLabel.setText("🟢 Connecté à pidev");
                dbStatusLabel.setStyle("-fx-text-fill: #27ae60;");
            } catch (Exception e) {
                dbStatusLabel.setText("🔴 Erreur de connexion");
                dbStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        }
    }

    private void showStatus(String message, String color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }

    private void clearRegisterForm() {
        if (firstNameField != null) firstNameField.clear();
        if (lastNameField != null) lastNameField.clear();
        if (roleChoiceBox != null) roleChoiceBox.setValue("Voyageur");
    }
}