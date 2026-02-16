package com.tahwissa.controller;

import com.tahwissa.entity.Utilisateur;
import com.tahwissa.service.AuthService;
import com.tahwissa.utils.Validator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label messageLabel;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRegister;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (!validateInputs(email, password)) {
            return;
        }

        try {
            Utilisateur user = authService.login(email, password);
            
            if (user != null) {
                showSuccess("Connexion réussie!");
                
                // Rediriger vers le dashboard approprié
                redirectToDashboard(user);
            } else {
                showError("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            showError("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/RegisterView.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
        } catch (Exception e) {
            showError("Erreur lors de l'ouverture de l'inscription");
            e.printStackTrace();
        }
    }

    private boolean validateInputs(String email, String password) {
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
        return true;
    }

    private void redirectToDashboard(Utilisateur user) {
        try {
            String fxmlFile = user.getRole().equalsIgnoreCase("ADMIN") 
                ? "/view/AdminDashboard.fxml" 
                : "/view/UserDashboard.fxml";
            
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 800));
        } catch (Exception e) {
            showError("Erreur lors du chargement du dashboard");
            e.printStackTrace();
        }
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
