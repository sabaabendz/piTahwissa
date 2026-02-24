package tn.esprit.tahwisa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML private StackPane contentArea;

    @FXML private HBox btnEmail; // ← HBox OK

    @FXML private Button btnReservations;
    @FXML private Button btnDestinations;
    @FXML private Button btnPointsInteret;
    @FXML private Button btnTransports;
    @FXML private Button btnEvenements;
    @FXML private Button btnParametres;

    @FXML private Label lblStatus;
    @FXML private Label lblDate;
    @FXML private Label lblMode;

    // 🔥 On change Button[] → Node[]
    private Node[] allMenuItems;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        allMenuItems = new Node[]{
                btnReservations,
                btnDestinations,
                btnPointsInteret,
                btnTransports,
                btnEvenements,
                btnEmail,           // maintenant accepté
                btnParametres
        };

        updateDateTime();
        showDestinations();
    }

    // ================= EMAIL =================
    @FXML
    public void showEmail() {
        loadView("/fxml/EmailView.fxml");
        setActiveItem(btnEmail);   // ✔ plus d'erreur
        updateMode("Mode: Emails");
    }

    // ================= DESTINATIONS =================
    @FXML
    public void showDestinations() {
        loadView("/fxml/DestinationsView.fxml"); // ⚠ j’ai corrigé le chemin
        setActiveItem(btnDestinations);
        updateMode("Mode: Liste");
    }

    @FXML
    public void showReservations() {
        System.out.println("Affichage des réservations");
    }

    @FXML
    public void showPointsInteret() {
        loadView("/fxml/Pointsinteretview.fxml");
        setActiveItem(btnPointsInteret);
        updateMode("Mode: Liste");
    }

    // ================= LOAD VIEW =================
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

    // 🔥 Remplace setActiveButton
    private void setActiveItem(Node activeItem) {
        for (Node item : allMenuItems) {
            if (item != null) {
                item.getStyleClass().remove("active");
            }
        }

        if (activeItem != null) {
            activeItem.getStyleClass().add("active");
        }
    }

    private void updateMode(String mode) {
        if (lblMode != null) lblMode.setText(mode);
    }

    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (lblDate != null)
            lblDate.setText(LocalDateTime.now().format(formatter));
    }
}