package tn.esprit.tahwisa.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import tn.esprit.tahwisa.models.Destination;
import tn.esprit.tahwisa.services.DestinationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateDestinationController implements Initializable {

    @FXML private Label lblFormTitle;
    @FXML private TextField txtNom;
    @FXML private TextField txtPays;
    @FXML private TextField txtVille;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtImageUrl;
    @FXML private TextField txtLatitude;
    @FXML private TextField txtLongitude;
    @FXML private Button btnSave;

    @FXML private Label errNom;
    @FXML private Label errPays;
    @FXML private Label errLatitude;
    @FXML private Label errLongitude;

    private DestinationService destinationService;
    private Destination currentDestination;
    private DestinationViewController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        destinationService = new DestinationService();
        setupValidation();
    }

    public void setParentController(DestinationViewController controller) {
        this.parentController = controller;
    }

    public void setDestination(Destination destination) {
        this.currentDestination = destination;
        lblFormTitle.setText("Modifier la Destination");
        btnSave.setText("💾 Mettre à jour");

        txtNom.setText(destination.getNom());
        txtPays.setText(destination.getPays());
        txtVille.setText(destination.getVille());
        txtDescription.setText(destination.getDescription());
        txtImageUrl.setText(destination.getImageUrl());

        if (destination.getLatitude() != null) {
            txtLatitude.setText(destination.getLatitude().toString());
        }
        if (destination.getLongitude() != null) {
            txtLongitude.setText(destination.getLongitude().toString());
        }
    }

    private void setupValidation() {
        txtNom.textProperty().addListener((obs, old, newVal) -> validateNom());
        txtPays.textProperty().addListener((obs, old, newVal) -> validatePays());
        txtLatitude.textProperty().addListener((obs, old, newVal) -> validateLatitude());
        txtLongitude.textProperty().addListener((obs, old, newVal) -> validateLongitude());
    }

    private boolean validateNom() {
        String nom = txtNom.getText().trim();
        if (nom.isEmpty()) {
            showError(errNom, "Le nom est obligatoire");
            return false;
        }
        if (nom.length() < 3) {
            showError(errNom, "Le nom doit contenir au moins 3 caractères");
            return false;
        }
        hideError(errNom);
        return true;
    }

    private boolean validatePays() {
        String pays = txtPays.getText().trim();
        if (pays.isEmpty()) {
            showError(errPays, "Le pays est obligatoire");
            return false;
        }
        hideError(errPays);
        return true;
    }

    private boolean validateLatitude() {
        String lat = txtLatitude.getText().trim();
        if (!lat.isEmpty()) {
            try {
                BigDecimal value = new BigDecimal(lat);
                if (value.compareTo(new BigDecimal("-90")) < 0 || value.compareTo(new BigDecimal("90")) > 0) {
                    showError(errLatitude, "La latitude doit être entre -90 et 90");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError(errLatitude, "Format invalide");
                return false;
            }
        }
        hideError(errLatitude);
        return true;
    }

    private boolean validateLongitude() {
        String lon = txtLongitude.getText().trim();
        if (!lon.isEmpty()) {
            try {
                BigDecimal value = new BigDecimal(lon);
                if (value.compareTo(new BigDecimal("-180")) < 0 || value.compareTo(new BigDecimal("180")) > 0) {
                    showError(errLongitude, "La longitude doit être entre -180 et 180");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError(errLongitude, "Format invalide");
                return false;
            }
        }
        hideError(errLongitude);
        return true;
    }

    @FXML
    private void saveDestination() {
        if (!validateForm()) {
            return;
        }

        try {
            Destination destination = new Destination(
                    txtNom.getText().trim(),
                    txtPays.getText().trim(),
                    txtVille.getText().trim(),
                    txtDescription.getText().trim(),
                    txtImageUrl.getText().trim(),
                    txtLatitude.getText().isEmpty() ? null : new BigDecimal(txtLatitude.getText().trim()),
                    txtLongitude.getText().isEmpty() ? null : new BigDecimal(txtLongitude.getText().trim())
            );

            if (currentDestination != null) {
                destination.setIdDestination(currentDestination.getIdDestination());
                destinationService.modifierDestination(destination);
                showSuccessAlert("Destination modifiée avec succès");
            } else {
                destinationService.ajouterDestination(destination);
                showSuccessAlert("Destination créée avec succès");
            }

            returnToList();

        } catch (Exception e) {
            showErrorAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void returnToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DestinationsView.fxml"));
            Parent listView = loader.load();

            StackPane contentArea = (StackPane) txtNom.getScene().getRoot().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(listView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        return validateNom() && validatePays() && validateLatitude() && validateLongitude();
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setManaged(true);
        label.setVisible(true);
    }

    private void hideError(Label label) {
        label.setManaged(false);
        label.setVisible(false);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}