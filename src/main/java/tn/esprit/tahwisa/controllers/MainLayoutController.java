package tn.esprit.tahwisa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Button btnReservations;
    @FXML private Button btnDestinations;
    @FXML private Button btnPointsInteret;
    @FXML private Button btnTransports;
    @FXML private Button btnEvenements;
    @FXML private Button btnParametres;
    @FXML private Label lblStatus;
    @FXML private Label lblDate;
    @FXML private Label lblMode;

    private Button[] allButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allButtons = new Button[]{
                btnReservations, btnDestinations, btnPointsInteret,
                btnTransports, btnEvenements, btnParametres
        };

        updateDateTime();
        showDestinations();
    }

    @FXML
    public void showDestinations() {
        loadView("/DestinationsView.fxml");
        setActiveButton(btnDestinations);
        updateMode("Mode: Liste");
    }

    @FXML
    public void showReservations() {
        // À implémenter ultérieurement
        System.out.println("Affichage des réservations");
    }

    @FXML
    public void showPointsInteret() {
        loadView("/fxml/Pointsinteretview.fxml");
        setActiveButton(btnPointsInteret);
        updateMode("Mode: Liste");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("❌ Erreur chargement vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeBtn) {
        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active");
            }
        }

        if (activeBtn != null) {
            activeBtn.getStyleClass().add("active");
        }
    }

    private void updateMode(String mode) {
        if (lblMode != null) lblMode.setText(mode);
    }

    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (lblDate != null) lblDate.setText(LocalDateTime.now().format(formatter));
    }
}