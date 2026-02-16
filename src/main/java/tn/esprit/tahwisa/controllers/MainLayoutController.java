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
    @FXML private Button btnDestinations;
    @FXML private Button btnPointsInteret;
    @FXML private Label lblStatus;
    @FXML private Label lblDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateDateTime();
        showDestinations();
    }

    @FXML
    private void showDestinations() {
        loadView("/fxml/DestinationsView.fxml");
        setActiveButton(btnDestinations);
    }

    @FXML
    private void showPointsInteret() {
        loadView("/fxml/PointsInteretView.fxml");
        setActiveButton(btnPointsInteret);
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
        btnDestinations.getStyleClass().remove("active");
        btnPointsInteret.getStyleClass().remove("active");
        activeBtn.getStyleClass().add("active");
    }

    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblDate.setText(LocalDateTime.now().format(formatter));
    }
}