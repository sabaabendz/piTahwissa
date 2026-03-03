package tn.esprit.tahwissa.controllers.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import tn.esprit.tahwissa.models.User;
import tn.esprit.tahwissa.utils.SceneNavigator;
import tn.esprit.tahwissa.utils.SessionManager;

import java.io.IOException;

public class ClientLayoutController {
    @FXML
    private AnchorPane eventContentContainer;
    @FXML private Label navUserName;
    @FXML private Label navUserEmail;
    @FXML private Label navAvatarLabel;

    // Module nav pills
    @FXML private Button navReservation;
    @FXML private Button navTransport;
    @FXML private Button navEvenement;
    @FXML private Button navPoints;  // "Points d'intérêt" dans le menu

    @FXML private Button btnClientLogout;

    // Reference to the nested ClientDashboard controller
    @FXML private ClientDashboardController clientDashboardController;

    private void loadEventView(String path) {
        try {
            AnchorPane view = FXMLLoader.load(getClass().getResource(path));
            eventContentContainer.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            navUserName.setText(user.getFirstName() + " " + user.getLastName());
            navUserEmail.setText(user.getEmail());
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                navAvatarLabel.setText(user.getFirstName().substring(0, 1).toUpperCase());
            }
        }
        // Réservation is the default active module
        setActivePill(navReservation);
    }

    // ── Module Navigation ──────────────────────────────────────────────

    @FXML
    private void handleNavReservation() {
        setActivePill(navReservation);
        navigateToTab(3); // Onglet Offres & Voyages (index 3)
    }

    @FXML
    private void handleNavTransport() {
        setActivePill(navTransport);
        showComingSoon("Gestion du Transport");
    }

    @FXML
    private void handleNavEvenement() {
        setActivePill(navEvenement);
        navigateToTab(7); // Onglet Événements (index 7)
    }

    @FXML
    private void handleNavPoints() {
        setActivePill(navPoints);
        // Au lieu de "Coming Soon", on navigue vers l'onglet Points d'Intérêt
        navigateToTab(2); // Onglet Points d'Intérêt (index 2)
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneNavigator.navigate("/fxml/login.fxml", "Tahwissa – Connexion");
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void navigateToTab(int index) {
        if (clientDashboardController != null) {
            TabPane tabPane = clientDashboardController.getClientTabPane();
            if (tabPane != null && index < tabPane.getTabs().size()) {
                tabPane.getSelectionModel().select(index);
            }
        }
    }

    private void setActivePill(Button active) {
        Button[] pills = {
                navReservation, navTransport, navEvenement, navPoints
        };
        for (Button pill : pills) {
            if (pill != null) pill.getStyleClass().remove("active");
        }
        if (active != null && !active.getStyleClass().contains("active")) {
            active.getStyleClass().add("active");
        }
    }

    private void showComingSoon(String moduleName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(moduleName);
        alert.setHeaderText("🚧 Module en cours d'intégration");
        alert.setContentText("Le module \"" + moduleName + "\" sera disponible lors de l'intégration de l'équipe.\n\nRestez connecté !");
        alert.showAndWait();
        // Reset to Réservation after dismissing
        setActivePill(navReservation);
    }

    @FXML
    private void showEventList() {
        loadEventView("/fxml/event/EventList.fxml");
    }

    @FXML
    private void showReservationList() {
        loadEventView("/fxml/event/ReservationList.fxml");
    }

    @FXML
    private void showReclamationList() {
        loadEventView("/fxml/event/ReclamationList.fxml");
    }

    @FXML
    private void showCreateEvent() {
        loadEventView("/fxml/event/EventForm.fxml");
    }
}