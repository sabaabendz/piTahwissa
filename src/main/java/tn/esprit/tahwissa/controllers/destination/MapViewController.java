package tn.esprit.tahwissa.controllers.destination;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import tn.esprit.tahwissa.models.User;
import tn.esprit.tahwissa.services.OpenStreetMapService;
import tn.esprit.tahwissa.utils.SessionManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class MapViewController implements Initializable {

    @FXML private WebView webView;
    @FXML private Label lblMapTitle;

    private WebEngine webEngine;
    private OpenStreetMapService mapsService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        webEngine = webView.getEngine();
        mapsService = new OpenStreetMapService();
    }

    public void showDestinationOnMap(String name, BigDecimal latitude, BigDecimal longitude) {
        if (lblMapTitle != null) {
            lblMapTitle.setText("🗺️  " + name);
        }

        String htmlContent = mapsService.generateMapHtml(name, latitude, longitude);
        webEngine.loadContent(htmlContent);
    }

    @FXML
    private void returnToPrevious() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            String role = currentUser != null ? currentUser.getRole().toUpperCase() : "USER";

            String fxmlPath = "";

            // Retourner à la vue appropriée selon le rôle
            if (role.equals("ADMIN") || role.equals("AGENT")) {
                fxmlPath = "/fxml/destination/DestinationsView.fxml";
            } else {
                fxmlPath = "/fxml/client/ClientDashboard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            StackPane contentArea = (StackPane) webView.getScene()
                    .getRoot().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshMap() {
        webEngine.reload();
    }
}