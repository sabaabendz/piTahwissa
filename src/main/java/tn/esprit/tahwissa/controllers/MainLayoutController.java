
package tn.esprit.tahwissa.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import tn.esprit.tahwissa.controllers.event.EventListController;
import tn.esprit.tahwissa.models.User;
import tn.esprit.tahwissa.utils.SessionManager;
import tn.esprit.tahwissa.utils.SceneNavigator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainLayoutController {
    @FXML
    public StackPane contentArea;

    @FXML private TabPane tabPane;
    @FXML private Button btnReservations;
    @FXML private Button btnDestinations;
    @FXML private Button btnPointsInteret;
    @FXML private Button btnParametres;
    @FXML private Button darkModeBtn;
    @FXML private Button btnClient;
    @FXML private Button btnUsers;
    @FXML private Button btnLogout;
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label userAvatarLabel;
    @FXML private AnchorPane eventListContainer;
    @FXML private AnchorPane reservationListContainer;
    @FXML private AnchorPane reclamationListContainer;
    @FXML private Button btnEventList;
    @FXML private Button btnReservationList;
    @FXML private Button btnReclamationList;
    @FXML private Button btnCreateEvent;
    @FXML private Button btnCreateResEvent;
    @FXML private Button btnStatistique;
    @FXML private Button btnEventMap;
    @FXML
    private Button btnGestionTransport;
    @FXML
    private void showGestionTransport() {
        openModuleInTab("Gestion Transport", "/fxml/transport/home.fxml");
        setActiveButton(btnGestionTransport);
    }
    private boolean isDarkMode = false;
    private List<Tab> allTabs;

    @FXML
    public void initialize() {
        allTabs = new ArrayList<>(tabPane.getTabs());

        darkModeBtn.setOnAction(e -> toggleDarkMode());
        if (btnLogout != null) {
            btnLogout.setOnAction(e -> handleLogout());
        }
        configureRoles();
    }

    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneNavigator.navigate("/fxml/login.fxml", "Tahwissa - Connexion");
    }

    private void configureRoles() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        if (userNameLabel != null) userNameLabel.setText(user.getFirstName() + " " + user.getLastName());
        if (userEmailLabel != null) userEmailLabel.setText(user.getEmail());
        if (userAvatarLabel != null && user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            userAvatarLabel.setText(user.getFirstName().substring(0, 1).toUpperCase());
        }

        String role = user.getRole().toUpperCase();

        tabPane.getTabs().clear();
        btnReservations.setVisible(false);
        btnReservations.setManaged(false);
        btnDestinations.setVisible(false);
        btnDestinations.setManaged(false);
        btnPointsInteret.setVisible(false);
        btnPointsInteret.setManaged(false);
        btnClient.setVisible(false);
        btnClient.setManaged(false);
        btnUsers.setVisible(false);
        btnUsers.setManaged(false);
        setEventButtonsVisible(false, false);

        if (role.equals("ADMIN")) {
            if (allTabs.size() > 1) tabPane.getTabs().add(allTabs.get(1));
            if (allTabs.size() > 3) tabPane.getTabs().add(allTabs.get(3));

            setEventButtonsVisible(true, true);

            btnDestinations.setText("📍 Destinations");
            btnDestinations.setVisible(true);
            btnDestinations.setManaged(true);
            btnDestinations.setOnAction(e -> showDestinations());

            btnPointsInteret.setText("🏖️ Points d'Intérêt");
            btnPointsInteret.setVisible(true);
            btnPointsInteret.setManaged(true);
            btnPointsInteret.setOnAction(e -> showPointsInteret());

            btnUsers.setText("👥 Utilisateurs");
            btnUsers.setVisible(true);
            btnUsers.setManaged(true);
            btnUsers.setOnAction(e -> {
                if (tabPane.getTabs().size() > 1) {
                    tabPane.getSelectionModel().select(1);
                }
                setActiveButton(btnUsers);
            });

            if (!tabPane.getTabs().isEmpty()) {
                tabPane.getSelectionModel().select(0);
            }
            setActiveButton(btnDestinations);

        } else if (role.equals("AGENT")) {
            if (!allTabs.isEmpty()) tabPane.getTabs().add(allTabs.get(0));

            setEventButtonsVisible(true, false);

            btnDestinations.setText("📍 Destinations");
            btnDestinations.setVisible(true);
            btnDestinations.setManaged(true);
            btnDestinations.setOnAction(e -> showDestinations());

            btnPointsInteret.setText("🏖️ Points d'Intérêt");
            btnPointsInteret.setVisible(true);
            btnPointsInteret.setManaged(true);
            btnPointsInteret.setOnAction(e -> showPointsInteret());

            btnReservations.setVisible(false);
            btnReservations.setManaged(false);
            btnClient.setVisible(false);
            btnClient.setManaged(false);
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);

            if (!tabPane.getTabs().isEmpty()) {
                tabPane.getSelectionModel().select(0);
            }
            setActiveButton(btnDestinations);

        } else {
            if (allTabs.size() > 2) tabPane.getTabs().add(allTabs.get(2));

            setEventButtonsVisible(false, false);

            btnDestinations.setText("📍 Destinations");
            btnDestinations.setVisible(true);
            btnDestinations.setManaged(true);
            btnDestinations.setOnAction(e -> showDestinations());

            btnPointsInteret.setText("🏖️ Points d'Intérêt");
            btnPointsInteret.setVisible(true);
            btnPointsInteret.setManaged(true);
            btnPointsInteret.setOnAction(e -> showPointsInteret());

            btnClient.setVisible(true);
            btnClient.setManaged(true);
            btnClient.setOnAction(e -> {
                if (!tabPane.getTabs().isEmpty()) {
                    tabPane.getSelectionModel().select(0);
                }
                setActiveButton(btnClient);
            });

            btnReservations.setVisible(false);
            btnReservations.setManaged(false);
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);

            if (!tabPane.getTabs().isEmpty()) {
                tabPane.getSelectionModel().select(0);
            }
            setActiveButton(btnClient);
        }
    }

    @FXML
    private void showDestinations() {
        openModuleInTab("Destinations", "/fxml/destination/DestinationsView.fxml");
        setActiveButton(btnDestinations);
    }

    @FXML
    private void showPointsInteret() {
        openModuleInTab("Points d'Intérêt", "/fxml/pointinteret/Pointsinteretview.fxml");
        setActiveButton(btnPointsInteret);
    }

    private void openModuleInTab(String title, String fxmlPath) {
        try {
            if (tabPane == null) {
                return;
            }

            for (Tab existingTab : tabPane.getTabs()) {
                if (title.equals(existingTab.getText())) {
                    tabPane.getSelectionModel().select(existingTab);
                    return;
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            if (view instanceof Region region) {
                region.setMaxWidth(Double.MAX_VALUE);
                region.setMaxHeight(Double.MAX_VALUE);
            }

            addTab(title, view, true);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible de charger la vue: " + fxmlPath);
        }
    }

    private void setActiveButton(Button active) {
        btnReservations.getStyleClass().remove("active");
        btnDestinations.getStyleClass().remove("active");
        if (btnPointsInteret != null) btnPointsInteret.getStyleClass().remove("active");
        btnClient.getStyleClass().remove("active");
        btnUsers.getStyleClass().remove("active");
        if (btnGestionTransport != null) btnGestionTransport.getStyleClass().remove("active");
        if (btnEventList != null) btnEventList.getStyleClass().remove("active");
        if (btnReservationList != null) btnReservationList.getStyleClass().remove("active");
        if (btnReclamationList != null) btnReclamationList.getStyleClass().remove("active");
        if (btnCreateEvent != null) btnCreateEvent.getStyleClass().remove("active");
        if (btnCreateResEvent != null) btnCreateResEvent.getStyleClass().remove("active");
        if (btnStatistique != null) btnStatistique.getStyleClass().remove("active");
        if (btnEventMap != null) btnEventMap.getStyleClass().remove("active");

        if (active != null && !active.getStyleClass().contains("active")) {
            active.getStyleClass().add("active");
        }
    }

    private void toggleDarkMode() {
        Scene scene = tabPane.getScene();
        if (scene == null) return;

        if (isDarkMode) {
            scene.getStylesheets().removeIf(ss -> ss.contains("tahwissa-dark.css"));
            darkModeBtn.setText("🌙 Mode Nuit");
        } else {
            String darkCss = getClass().getResource("/css/tahwissa-dark.css").toExternalForm();
            if (darkCss != null) {
                scene.getStylesheets().add(darkCss);
            }
            darkModeBtn.setText("☀️ Mode Jour");
        }
        isDarkMode = !isDarkMode;
    }

    public void addTab(String title, Node content, boolean closable) {
        if (tabPane == null) return;

        BorderPane wrapper = new BorderPane();
        wrapper.setCenter(content);

        Tab tab = new Tab(title, wrapper);
        tab.setClosable(closable);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void loadEventView(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent content = loader.load();

            Object controller = loader.getController();

            if (controller instanceof EventListController) {
                ((EventListController) controller).setMainLayoutController(this);
            }

            addTab(title, content, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showEventList() {
        loadEventView("/fxml/event/EventList.fxml", "Liste Événements");
        setActiveButton(btnEventList);
    }

    @FXML
    private void showReservationList() {
        loadEventView("/fxml/event/ReservationList.fxml", "📝 Réservations");
        setActiveButton(btnReservationList);
    }

    @FXML
    private void showReclamationList() {
        loadEventView("/fxml/event/ReclamationList.fxml", "📣 Réclamations");
        setActiveButton(btnReclamationList);
    }

    @FXML
    private void showCreateEvent() {
        loadEventView("/fxml/event/EventForm.fxml", "➕ Créer Événement");
        setActiveButton(btnCreateEvent);
    }

    @FXML
    private void showCarte() {
        loadEventView("/fxml/event/EventMap.fxml", "🗺 Carte des événements");
        setActiveButton(btnEventMap);
    }

    @FXML
    private void showStatistique() {
        loadEventView("/fxml/event/Statistique.fxml", "📊 Statistiques");
        setActiveButton(btnStatistique);
    }

    public void showCreateresEvent(ActionEvent actionEvent) {
        loadEventView("/fxml/event/ReservationForm.fxml", "➕ Créer Réservation Événement");
        setActiveButton(btnCreateResEvent);
    }

    private void setEventButtonsVisible(boolean visible, boolean includeAdminOnly) {
        setButtonVisible(btnEventList, visible);
        setButtonVisible(btnReservationList, visible);
        setButtonVisible(btnReclamationList, visible);
        setButtonVisible(btnCreateResEvent, visible);
        setButtonVisible(btnEventMap, visible);
        setButtonVisible(btnCreateEvent, visible && includeAdminOnly);
        setButtonVisible(btnStatistique, visible && includeAdminOnly);
    }

    private void setButtonVisible(Button button, boolean visible) {
        if (button == null) {
            return;
        }
        button.setVisible(visible);
        button.setManaged(visible);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}