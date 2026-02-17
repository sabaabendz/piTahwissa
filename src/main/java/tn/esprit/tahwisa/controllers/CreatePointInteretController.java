package tn.esprit.tahwisa.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import tn.esprit.tahwisa.models.Destination;
import tn.esprit.tahwisa.models.PointInteret;
import tn.esprit.tahwisa.services.DestinationService;
import tn.esprit.tahwisa.services.PointInteretService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreatePointInteretController implements Initializable {

    @FXML private Label lblFormTitle;
    @FXML private TextField txtNom;
    @FXML private ComboBox<String> cmbType;
    @FXML private ComboBox<Destination> cmbDestination;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtImageUrl;
    @FXML private Button btnSave;
    @FXML private Label errNom;
    @FXML private Label errType;
    @FXML private Label errDestination;

    private PointInteretService pointInteretService;
    private DestinationService destinationService;
    private PointInteret currentPointInteret;
    private PointInteretViewController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pointInteretService = new PointInteretService();
        destinationService = new DestinationService();
        setupTypes();
        loadDestinations();
        setupValidation();
    }

    private void setupTypes() {
        cmbType.setItems(FXCollections.observableArrayList(
                "monument", "plage", "musée", "restaurant", "parc", "hôtel", "autre"
        ));
    }

    private void loadDestinations() {
        try {
            List<Destination> destinations = destinationService.afficherDestinations();
            cmbDestination.setItems(FXCollections.observableArrayList(destinations));
            cmbDestination.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Destination item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom() + " (" + item.getPays() + ")");
                }
            });
            cmbDestination.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Destination item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom() + " (" + item.getPays() + ")");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupValidation() {
        txtNom.textProperty().addListener((obs, old, newVal) -> validateNom());
        cmbType.valueProperty().addListener((obs, old, newVal) -> validateType());
        cmbDestination.valueProperty().addListener((obs, old, newVal) -> validateDestination());
    }

    public void setParentController(PointInteretViewController controller) {
        this.parentController = controller;
    }

    public void setPointInteret(PointInteret pi) {
        this.currentPointInteret = pi;
        lblFormTitle.setText("Modifier le Point d'Intérêt");
        btnSave.setText("💾  Mettre à jour");
        txtNom.setText(pi.getNom());
        cmbType.setValue(pi.getType());
        txtDescription.setText(pi.getDescription());
        txtImageUrl.setText(pi.getImageUrl());
        cmbDestination.getItems().stream()
                .filter(d -> d.getIdDestination() == pi.getDestinationId())
                .findFirst()
                .ifPresent(d -> cmbDestination.setValue(d));
    }

    private boolean validateNom() {
        String nom = txtNom.getText().trim();
        if (nom.isEmpty()) { showFieldError(errNom, "Le nom est obligatoire"); return false; }
        if (nom.length() < 3) { showFieldError(errNom, "Minimum 3 caractères"); return false; }
        hideFieldError(errNom);
        return true;
    }

    private boolean validateType() {
        if (cmbType.getValue() == null) { showFieldError(errType, "Le type est obligatoire"); return false; }
        hideFieldError(errType);
        return true;
    }

    private boolean validateDestination() {
        if (cmbDestination.getValue() == null) { showFieldError(errDestination, "La destination est obligatoire"); return false; }
        hideFieldError(errDestination);
        return true;
    }

    @FXML
    private void savePointInteret() {
        if (!validateNom() | !validateType() | !validateDestination()) return;
        try {
            Destination dest = cmbDestination.getValue();
            PointInteret pi = new PointInteret(
                    txtNom.getText().trim(), cmbType.getValue(),
                    txtDescription.getText().trim(), txtImageUrl.getText().trim(),
                    dest.getIdDestination()
            );
            if (currentPointInteret != null) {
                pi.setIdPointInteret(currentPointInteret.getIdPointInteret());
                pointInteretService.modifierPointInteret(pi);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Point d'intérêt modifié !");
            } else {
                pointInteretService.ajouterPointInteret(pi);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Point d'intérêt créé !");
            }
            returnToList();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void returnToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PointsInteretView.fxml"));
            Parent listView = loader.load();
            StackPane contentArea = (StackPane) txtNom.getScene().getRoot().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(listView);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showFieldError(Label label, String msg) { label.setText(msg); label.setManaged(true); label.setVisible(true); }
    private void hideFieldError(Label label) { label.setManaged(false); label.setVisible(false); }
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setContentText(msg); alert.showAndWait();
    }
}