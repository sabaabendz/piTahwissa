package tn.esprit.tahwissa.controllers.event;

import tn.esprit.tahwissa.services.AuthService;
import tn.esprit.tahwissa.utils.Validator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField txtNom;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label messageLabel;
    @FXML private Button btnRegister;
    @FXML private Hyperlink linkLogin;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister() {
        String nom = txtNom.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (!validateInputs(nom, email, password, confirmPassword)) {
            return;
        }

        try {
            boolean success = authService.register(nom, email, password);
            
            if (success) {
                showSuccess("Inscription réussie! Redirection vers la connexion...");
                
                // Attendre un peu avant de rediriger
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(this::handleBackToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                showError("Cet email est déjà utilisé");
            }
        } catch (Exception e) {
            showError("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/event/LoginView.fxml"));
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
        } catch (Exception e) {
            showError("Erreur lors du retour à la connexion");
            e.printStackTrace();
        }
    }

    private boolean validateInputs(String nom, String email, String password, String confirmPassword) {
        if (!Validator.isNotEmpty(nom)) {
            showError("Le nom est requis");
            return false;
        }
        if (!Validator.isValidLength(nom, 2, 100)) {
            showError("Le nom doit contenir entre 2 et 100 caractères");
            return false;
        }
        if (!Validator.isNotEmpty(email)) {
            showError("L'email est requis");
            return false;
        }
        if (!Validator.isValidEmail(email)) {
            showError("Format d'email invalide");
            return false;
        }
        if (!Validator.isNotEmpty(password)) {
            showError("Le mot de passe est requis");
            return false;
        }
        if (!Validator.isValidLength(password, 6, 100)) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return false;
        }
        return true;
    }

    private void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("message-success");
        messageLabel.setVisible(true);
    }

    private void showError(String message) {
        messageLabel.setText("✗ " + message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("message-error");
        messageLabel.setVisible(true);
    }
}
