package controller;

import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public class DashboardController {

    @FXML private Label roleLabel;
    @FXML private Label warningLabel;
    @FXML private VBox mainContent;

    private static final Set<String> ALLOWED = Set.of("ADMIN", "AGENT");
    private String currentRole;

    @FXML
    public void initialize() {
        System.out.println("✅ DashboardController initialisé");
        
        // Récupérer automatiquement le rôle depuis la session
        String role = SessionManager.getInstance().getCurrentRole();
        if (role != null) {
            System.out.println("🔑 Rôle récupéré depuis la session: " + role);
            setUserRole(role);
        } else {
            System.out.println("⚠️ Aucun rôle dans la session");
            setUserRole(null); // default state
        }
    }

    /**
     * Call this from the caller once you know the connected user's role.
     */
    public void setUserRole(String role) {
        this.currentRole = role;
        String normalized = role != null ? role.toUpperCase() : "";
        boolean allowed = ALLOWED.contains(normalized);

        System.out.println("🔑 Rôle défini dans le dashboard: " + role + " (Autorisé: " + allowed + ")");

        if (roleLabel != null) {
            roleLabel.setText("Rôle: " + (role == null ? "N/A" : role));
        }
        if (warningLabel != null) {
            warningLabel.setText(allowed ? "✅ Accès autorisé" : "❌ Accès réservé aux rôles ADMIN et AGENT");
            warningLabel.setStyle(allowed
                    ? "-fx-text-fill: #27ae60; -fx-font-size: 12px; -fx-font-weight: bold;"
                    : "-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
        if (mainContent != null) {
            mainContent.setDisable(!allowed);
            mainContent.setOpacity(allowed ? 1.0 : 0.5);
        }
    }

    @FXML
    private void handleLogout() {
        System.out.println("🚪 Déconnexion...");
        
        // Nettoyer la session
        SessionManager.getInstance().logout();
        
        try {
            // Charger la page de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            // Obtenir le stage actuel
            Stage stage = (Stage) roleLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 650));
            stage.setTitle("Tahwissa - Connexion");
            stage.show();

            System.out.println("✅ Retour à la page de connexion");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenUserList() {
        System.out.println("📋 Ouverture de la liste des utilisateurs...");
        try {
            // Charger la page user-list
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user/user-list.fxml"));
            Parent root = loader.load();

            // Obtenir le stage actuel
            Stage stage = (Stage) roleLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("Tahwissa - Gestion des Utilisateurs");
            stage.show();

            System.out.println("✅ Liste des utilisateurs affichée");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de la liste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

