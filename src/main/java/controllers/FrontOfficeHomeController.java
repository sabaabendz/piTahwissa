package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class FrontOfficeHomeController {

    @FXML
    private Label userLabel;

    @FXML
    private Button catalogBtn;

    @FXML
    private Button reservationsBtn;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Load catalog by default
        loadView("/frontoffice_catalog.fxml");
    }

    @FXML
    private void showCatalog() {
        setActiveButton(catalogBtn);
        loadView("/frontoffice_catalog.fxml");
    }

    @FXML
    private void showReservations() {
        setActiveButton(reservationsBtn);
        loadView("/frontoffice_reservations.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Pass contentArea to controller
            Object controller = loader.getController();
            if (controller instanceof FrontOfficeCatalogController) {
                ((FrontOfficeCatalogController) controller).setContentArea(contentArea);
            } else if (controller instanceof FrontOfficeReservationsController) {
                ((FrontOfficeReservationsController) controller).setContentArea(contentArea);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeButton) {
        catalogBtn.getStyleClass().remove("active");
        reservationsBtn.getStyleClass().remove("active");

        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
