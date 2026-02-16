package controller;

import entities.User;
import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur pour la page "Coming Soon"
 * Affichée pour les utilisateurs normaux (rôle USER)
 */
public class ComingSoonController {

    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private Button logoutBtn;

    @FXML
    public void initialize() {
        System.out.println("✅ ComingSoonController initialisé");

        // Charger les informations de l'utilisateur connecté
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            System.out.println("👤 Utilisateur: " + currentUser.getEmail());

            // Afficher le nom complet
            if (userNameLabel != null) {
                String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                userNameLabel.setText("Bienvenue, " + fullName + " !");
            }

            // Afficher l'email
            if (userEmailLabel != null) {
                userEmailLabel.setText(currentUser.getEmail());
            }
        } else {
            System.out.println("⚠️ Aucun utilisateur dans la session");
            if (userNameLabel != null) {
                userNameLabel.setText("Bienvenue, Visiteur !");
            }
            if (userEmailLabel != null) {
                userEmailLabel.setText("");
            }
        }
    }

    /**
     * Gère la déconnexion de l'utilisateur
     */
    @FXML
    private void handleLogout() {
        System.out.println("🚪 Déconnexion depuis la page Coming Soon...");

        // Nettoyer la session
        SessionManager.getInstance().logout();

        try {
            // Charger la page de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            // Obtenir le stage actuel
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 650));
            stage.setTitle("Tahwissa - Connexion");
            stage.show();

            System.out.println("✅ Retour à la page de connexion");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

