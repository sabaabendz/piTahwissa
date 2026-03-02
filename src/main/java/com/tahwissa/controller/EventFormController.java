package com.tahwissa.controller;

import com.tahwissa.entity.Evenement;
import com.tahwissa.service.EvenementService;
import com.tahwissa.utils.EventImageUtils;
import com.tahwissa.utils.Validator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventFormController {
    @FXML private Label formTitle;
    @FXML private TextField txtTitre;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtLieu;
    @FXML private DatePicker dateEvent;
    @FXML private Spinner<Integer> spnHeure;
    @FXML private Spinner<Integer> spnMinute;
    @FXML private TextField txtPrix;
    @FXML private Spinner<Integer> spnPlaces;
    @FXML private ComboBox<String> cmbCategorie;
    @FXML private ComboBox<String> cmbStatut;
    @FXML private Label messageLabel;
    @FXML private Button btnSubmit;
    @FXML private ImageView imgEvent;
    @FXML private Button btnChooseImage;
    @FXML private Label lblImageName;

    private final EvenementService evenementService = new EvenementService();
    private Evenement currentEvenement;
    private Path selectedImagePath;

    @FXML
    public void initialize() {
        // Configurer les spinners
        SpinnerValueFactory<Integer> heureFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        spnHeure.setValueFactory(heureFactory);
        spnHeure.setEditable(true);
        
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        spnMinute.setValueFactory(minuteFactory);
        spnMinute.setEditable(true);
        
        SpinnerValueFactory<Integer> placesFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 100);
        spnPlaces.setValueFactory(placesFactory);
        spnPlaces.setEditable(true);
        
        // Populate category ComboBox
        cmbCategorie.getItems().addAll("Musique", "Culture", "Sport", "Technologie", "Art", "Éducation", "Autre");
        cmbCategorie.setValue("Musique");
        
        // Populate status ComboBox
        cmbStatut.getItems().addAll("DISPONIBLE", "COMPLET", "ANNULE", "REPORTE");
        cmbStatut.setValue("DISPONIBLE");
        
        // Set default date to today
        dateEvent.setValue(LocalDate.now());

        imgEvent.setPreserveRatio(true);
        imgEvent.setFitWidth(140);
        imgEvent.setFitHeight(100);
        imgEvent.setImage(null);
        lblImageName.setText("");
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une image");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.webp")
        );
        java.io.File file = fc.showOpenDialog(btnChooseImage.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.toPath();
            Image img = new Image(file.toURI().toString());
            imgEvent.setImage(img);
            lblImageName.setText(file.getName());
        }
    }

    public void setEvenement(Evenement evenement) {
        this.currentEvenement = evenement;
        formTitle.setText("Modifier l'événement");
        btnSubmit.setText("Mettre à jour");
        fillForm();
    }

    private void fillForm() {
        txtTitre.setText(currentEvenement.getTitre());
        txtDescription.setText(currentEvenement.getDescription());
        txtLieu.setText(currentEvenement.getLieu());
        dateEvent.setValue(currentEvenement.getDateEvent());
        spnHeure.getValueFactory().setValue(currentEvenement.getHeureEvent().getHour());
        spnMinute.getValueFactory().setValue(currentEvenement.getHeureEvent().getMinute());
        txtPrix.setText(String.valueOf(currentEvenement.getPrix()));
        spnPlaces.getValueFactory().setValue(currentEvenement.getNbPlaces());
        cmbCategorie.setValue(currentEvenement.getCategorie());
        cmbStatut.setValue(currentEvenement.getStatut());
        selectedImagePath = null;
        if (currentEvenement.getImageFilename() != null && !currentEvenement.getImageFilename().isBlank()) {
            Image img = EventImageUtils.loadEventImage(currentEvenement.getImageFilename());
            if (img != null) {
                imgEvent.setImage(img);
                lblImageName.setText(currentEvenement.getImageFilename());
            }
        } else {
            imgEvent.setImage(null);
            lblImageName.setText("");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;

        try {
            LocalTime heureEvent = LocalTime.of(spnHeure.getValue(), spnMinute.getValue());
            String imageFilename = null;
            if (selectedImagePath != null) {
                imageFilename = EventImageUtils.saveEventImage(selectedImagePath);
            }

            if (currentEvenement == null) {
                Evenement evenement = new Evenement(
                        txtTitre.getText().trim(),
                        txtDescription.getText().trim(),
                        txtLieu.getText().trim(),
                        dateEvent.getValue(),
                        heureEvent,
                        Double.parseDouble(txtPrix.getText().trim()),
                        spnPlaces.getValue(),
                        cmbCategorie.getValue(),
                        cmbStatut.getValue()
                );
                evenement.setImageFilename(imageFilename);

                if (evenementService.createEvenement(evenement)) {
                    showSuccess("Événement créé avec succès");
                    handleBackToList();
                } else {
                    showError("Erreur lors de la création");
                }
            } else {
                currentEvenement.setTitre(txtTitre.getText().trim());
                currentEvenement.setDescription(txtDescription.getText().trim());
                currentEvenement.setLieu(txtLieu.getText().trim());
                currentEvenement.setDateEvent(dateEvent.getValue());
                currentEvenement.setHeureEvent(heureEvent);
                currentEvenement.setPrix(Double.parseDouble(txtPrix.getText().trim()));
                currentEvenement.setNbPlaces(spnPlaces.getValue());
                currentEvenement.setCategorie(cmbCategorie.getValue());
                currentEvenement.setStatut(cmbStatut.getValue());
                if (selectedImagePath != null && imageFilename != null) {
                    currentEvenement.setImageFilename(imageFilename);
                }

                if (evenementService.updateEvenement(currentEvenement)) {
                    showSuccess("Événement modifié avec succès");
                    handleBackToList();
                } else {
                    showError("Erreur lors de la modification");
                }
            }
        } catch (NumberFormatException e) {
            showError("Le prix doit être un nombre valide");
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (!Validator.isNotEmpty(txtTitre.getText())) {
            showError("Le titre est requis");
            return false;
        }
        if (!Validator.isNotEmpty(txtDescription.getText())) {
            showError("La description est requise");
            return false;
        }
        if (!Validator.isNotEmpty(txtLieu.getText())) {
            showError("Le lieu est requis");
            return false;
        }
        if (dateEvent.getValue() == null) {
            showError("La date est requise");
            return false;
        }
        if (dateEvent.getValue().isBefore(LocalDate.now())) {
            showError("La date de l'événement ne peut pas être dans le passé");
            return false;
        }
        if (!Validator.isNotEmpty(txtPrix.getText())) {
            showError("Le prix est requis");
            return false;
        }
        try {
            double prix = Double.parseDouble(txtPrix.getText().trim());
            if (prix < 0) {
                showError("Le prix ne peut pas être négatif");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Le prix doit être un nombre valide");
            return false;
        }
        return true;
    }

    @FXML
    private void handleCancel() {
        handleBackToList();
    }

    @FXML
    private void handleBackToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EventList.fxml"));
            Parent content = loader.load();
            StackPane contentArea = (StackPane) txtTitre.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("message-success");
        messageLabel.setVisible(true);
    }

    private void showError(String message) {
        messageLabel.setText("✗ " + message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("message-error");
        messageLabel.setVisible(true);
    }
}
