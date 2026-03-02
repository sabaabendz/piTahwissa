package com.tahwissa.controller;

import com.tahwissa.service.AuthService;
import com.tahwissa.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UserDashboardController {
    @FXML private Label lblUsername;
    @FXML private StackPane contentArea;
    @FXML private Button btnEvenements;
    @FXML private Button btnReservations;
    @FXML private Button btnReclamations;
    @FXML private Button btnMap;
    @FXML private Button btnLogout;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        String username = SessionManager.getInstance().getCurrentUser().getNom();
        lblUsername.setText("Bienvenue, " + username);
        
        // Charger la liste des événements par défaut
        loadEvenements();
    }

    @FXML
    private void loadEvenements() {
        loadContent("/view/EventList.fxml");
        updateActiveButton(btnEvenements);
    }

    @FXML
    private void loadReservations() {
        loadContent("/view/ReservationList.fxml");
        updateActiveButton(btnReservations);
    }

    @FXML
    private void loadReclamations() {
        loadContent("/view/ReclamationList.fxml");
        updateActiveButton(btnReclamations);
    }

    @FXML
    private void loadMap() {
        loadContent("/view/EventMap.fxml");
        updateActiveButton(btnMap);
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateActiveButton(Button activeButton) {
        btnEvenements.getStyleClass().remove("nav-item-active");
        btnReservations.getStyleClass().remove("nav-item-active");
        btnReclamations.getStyleClass().remove("nav-item-active");
        btnMap.getStyleClass().remove("nav-item-active");
        activeButton.getStyleClass().add("nav-item-active");
    }
}
