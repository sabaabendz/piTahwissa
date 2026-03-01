package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class HomeController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label userLabel;

    @FXML
    private Button transportBtn;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Initialiser les labels
        titleLabel.setText("Bienvenue dans Voyage Loisir");
        userLabel.setText("Administrateur");
        
        // Load transport list by default
        loadView("/transport_list.fxml");
    }

    @FXML
    private void showTransports() {
        loadView("/transport_list.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Pass contentArea to controller if it's a TransportController
            Object controller = loader.getController();
            if (controller instanceof TransportController) {
                ((TransportController) controller).setContentArea(contentArea);
            } else if (controller instanceof ReservationTransportController) {
                ((ReservationTransportController) controller).setContentArea(contentArea);
            } else if (controller instanceof TransportCatalogController) {
                ((TransportCatalogController) controller).setContentArea(contentArea);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
