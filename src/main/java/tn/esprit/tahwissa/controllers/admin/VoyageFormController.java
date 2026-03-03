package tn.esprit.tahwissa.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.tahwissa.models.Voyage;
import tn.esprit.tahwissa.services.VoyageService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class VoyageFormController {

    @FXML private Label formTitle;
    @FXML private TextField titreField, destinationField, categorieField, prixField, placesField, imageField;
    @FXML private DatePicker departPicker, retourPicker;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextArea descriptionField;

    // Error labels
    @FXML private Label titreError, destinationError, categorieError, prixError, departError, retourError, placesError, statutError, descriptionError, imageError;

    private Voyage existingVoyage;
    private Runnable onSaveCallback;

    private VoyageService voyageService;

    public void setVoyage(Voyage voyage) {
        this.existingVoyage = voyage;
        if (voyage != null) {
            formTitle.setText("✏️ Modifier Voyage");
            titreField.setText(voyage.getTitre());
            destinationField.setText(voyage.getDestination());
            categorieField.setText(voyage.getCategorie());
            prixField.setText(voyage.getPrixUnitaire().toString());
            departPicker.setValue(voyage.getDateDepart());
            retourPicker.setValue(voyage.getDateRetour());
            placesField.setText(String.valueOf(voyage.getPlacesDisponibles()));
            statutCombo.setValue(voyage.getStatut());
            descriptionField.setText(voyage.getDescription());
            imageField.setText(voyage.getImageUrl());
        } else {
            formTitle.setText("➕ Nouveau Voyage");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        try {
            voyageService = new VoyageService();
            statutCombo.getItems().setAll("ACTIF", "INACTIF", "COMPLET");
            statutCombo.setValue("ACTIF");
        } catch (SQLException e) {
            showError("Service error", e.getMessage());
        }
    }

    private boolean validateInputs() {
        clearErrors();
        boolean valid = true;

        // Titre
        if (titreField.getText().trim().isEmpty()) {
            titreError.setText("Titre requis");
            valid = false;
        }

        // Destination
        if (destinationField.getText().trim().isEmpty()) {
            destinationError.setText("Destination requise");
            valid = false;
        }

        // Catégorie
        if (categorieField.getText().trim().isEmpty()) {
            categorieError.setText("Catégorie requise");
            valid = false;
        }

        // Prix
        try {
            new BigDecimal(prixField.getText().trim());
        } catch (NumberFormatException e) {
            prixError.setText("Prix invalide");
            valid = false;
        }

        // Dates
        if (departPicker.getValue() == null) {
            departError.setText("Date départ requise");
            valid = false;
        }
        if (retourPicker.getValue() == null) {
            retourError.setText("Date retour requise");
            valid = false;
        }
        if (departPicker.getValue() != null && retourPicker.getValue() != null &&
                retourPicker.getValue().isBefore(departPicker.getValue())) {
            retourError.setText("Retour doit être après départ");
            valid = false;
        }

        // Places
        try {
            Integer.parseInt(placesField.getText().trim());
        } catch (NumberFormatException e) {
            placesError.setText("Places doit être un nombre");
            valid = false;
        }

        // Statut
        if (statutCombo.getValue() == null) {
            statutError.setText("Statut requis");
            valid = false;
        }

        return valid;
    }

    private void clearErrors() {
        titreError.setText("");
        destinationError.setText("");
        categorieError.setText("");
        prixError.setText("");
        departError.setText("");
        retourError.setText("");
        placesError.setText("");
        statutError.setText("");
        descriptionError.setText("");
        imageError.setText("");
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) return;

        try {
            Voyage v = existingVoyage != null ? existingVoyage : new Voyage();
            v.setTitre(titreField.getText().trim());
            v.setDestination(destinationField.getText().trim());
            v.setCategorie(categorieField.getText().trim());
            v.setPrixUnitaire(new BigDecimal(prixField.getText().trim()));
            v.setDateDepart(departPicker.getValue());
            v.setDateRetour(retourPicker.getValue());
            v.setPlacesDisponibles(Integer.parseInt(placesField.getText().trim()));
            v.setStatut(statutCombo.getValue());
            v.setDescription(descriptionField.getText().trim());
            v.setImageUrl(imageField.getText().trim());

            boolean success;
            if (existingVoyage == null) {
                success = voyageService.addVoyage(v);
            } else {
                success = voyageService.updateVoyage(v);
            }

            if (success) {
                closeTab();
                if (onSaveCallback != null) onSaveCallback.run();
            } else {
                showError("Erreur", "Échec de l'enregistrement.");
            }
        } catch (Exception e) {
            showError("Erreur", "Vérifiez les données saisies.");
        }
    }

    @FXML
    private void handleCancel() {
        closeTab();
    }

    private void closeTab() {
        TabPane tabPane = (TabPane) titreField.getScene().lookup("#tabPane");
        if (tabPane != null) {
            tabPane.getTabs().removeIf(tab -> tab.getContent() == titreField.getScene().getRoot());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}