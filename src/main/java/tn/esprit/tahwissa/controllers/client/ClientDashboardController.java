package tn.esprit.tahwissa.controllers.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientDashboardController implements Initializable {

    @FXML private TabPane clientTabPane;
    @FXML private VBox destinationsCard;
    @FXML private VBox pointsInteretCard;
    @FXML private VBox voyagesCard;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeCardsClickable();
    }

    private void makeCardsClickable() {
        // Carte Destinations
        if (destinationsCard != null) {
            destinationsCard.setOnMouseClicked(event -> selectTabByTitle("📍 Destinations"));
            destinationsCard.setStyle("-fx-cursor: hand;");
        }

        // Carte Points d'Intérêt
        if (pointsInteretCard != null) {
            pointsInteretCard.setOnMouseClicked(event -> selectTabByTitle("🏖️ Points d'Intérêt"));
            pointsInteretCard.setStyle("-fx-cursor: hand;");
        }

        // Carte Voyages
        if (voyagesCard != null) {
            voyagesCard.setOnMouseClicked(event -> selectTabByTitle("🌍 Offres & Voyages"));
            voyagesCard.setStyle("-fx-cursor: hand;");
        }
    }

    private void selectTabByTitle(String title) {
        if (clientTabPane != null) {
            for (Tab tab : clientTabPane.getTabs()) {
                if (tab.getText().equals(title)) {
                    clientTabPane.getSelectionModel().select(tab);
                    break;
                }
            }
        }
    }

    public void selectDestinationsTab() {
        selectTabByTitle("📍 Destinations");
    }

    // Les méthodes existantes (à garder telles quelles)
    @FXML
    private void handleRefreshVoyages() {
        // À implémenter
    }

    @FXML
    private void handleConfirmReservation() {
        // À implémenter
    }

    @FXML
    private void handleRefreshHistory() {
        // À implémenter
    }

    @FXML
    private void handleRefreshPaiements() {
        // À implémenter
    }

    @FXML
    private void handleSendChatMessage() {
        // À implémenter
    }

    @FXML
    private void showEventList() {
        // À implémenter
    }

    @FXML
    private void showReservationList() {
        // À implémenter
    }

    @FXML
    private void showReclamationList() {
        // À implémenter
    }

    @FXML
    private void showCreateEvent() {
        // À implémenter
    }

    @FXML
    private void showCarte() {
        // À implémenter
    }
    // Dans ClientDashboardController.java
    public TabPane getClientTabPane() {
        return clientTabPane;
    }}