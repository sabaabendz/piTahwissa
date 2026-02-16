package com.tahwissa.controller;

import com.tahwissa.entity.Evenement;
import com.tahwissa.entity.ReservationEvenement;
import com.tahwissa.service.EvenementService;
import com.tahwissa.service.ReservationService;
import com.tahwissa.utils.SessionManager;
import com.tahwissa.utils.Validator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class ReservationFormController {
    @FXML private Label formTitle;
    @FXML private ComboBox<Evenement> cmbEvenement;
    @FXML private Spinner<Integer> spnPlaces;
    @FXML private ComboBox<String> cmbStatut;
    @FXML private Label messageLabel;
    @FXML private Button btnSubmit;
    @FXML private VBox statutBox;

    private final ReservationService reservationService = new ReservationService();
    private final EvenementService evenementService = new EvenementService();
    private ReservationEvenement currentReservation;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> placesFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        spnPlaces.setValueFactory(placesFactory);
        spnPlaces.setEditable(true);
        
        loadEvenements();

        if (SessionManager.getInstance().isAdmin()) {
            statutBox.setVisible(true);
            statutBox.setManaged(true);
            cmbStatut.getItems().addAll("EN_ATTENTE", "CONFIRMEE", "ANNULEE");
            cmbStatut.setValue("EN_ATTENTE");
        } else {
            statutBox.setVisible(false);
            statutBox.setManaged(false);
        }
    }

    private void loadEvenements() {
        cmbEvenement.getItems().clear();
        cmbEvenement.getItems().addAll(evenementService.getAvailableEvenements());
        cmbEvenement.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Evenement evenement) {
                return evenement == null ? "" : evenement.getTitre();
            }
            @Override
            public Evenement fromString(String string) {
                return null;
            }
        });
    }

    public void setReservation(ReservationEvenement reservation) {
        this.currentReservation = reservation;
        formTitle.setText("Modifier la réservation");
        btnSubmit.setText("Mettre à jour");
        fillForm();
    }

    private void fillForm() {
        Evenement evenement = evenementService.getEvenementById(currentReservation.getIdEvenement());
        cmbEvenement.setValue(evenement);
        cmbEvenement.setDisable(true);
        spnPlaces.getValueFactory().setValue(currentReservation.getNbPlacesReservees());
        if (SessionManager.getInstance().isAdmin()) {
            cmbStatut.setValue(currentReservation.getStatut());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;

        try {
            if (currentReservation == null) {
                ReservationEvenement reservation = new ReservationEvenement(
                        LocalDateTime.now(),
                        spnPlaces.getValue(),
                        "EN_ATTENTE",
                        cmbEvenement.getValue().getIdEvenement(),
                        SessionManager.getInstance().getCurrentUserId()
                );

                if (reservationService.createReservation(reservation)) {
                    showSuccess("Réservation créée avec succès");
                    handleBackToList();
                } else {
                    showError("Erreur lors de la création");
                }
            } else {
                currentReservation.setNbPlacesReservees(spnPlaces.getValue());
                if (SessionManager.getInstance().isAdmin()) {
                    currentReservation.setStatut(cmbStatut.getValue());
                }

                if (reservationService.updateReservation(currentReservation)) {
                    showSuccess("Réservation modifiée avec succès");
                    handleBackToList();
                } else {
                    showError("Erreur lors de la modification");
                }
            }
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (cmbEvenement.getValue() == null) {
            showError("Veuillez sélectionner un événement");
            return false;
        }
        if (!Validator.isPositiveInteger(spnPlaces.getValue())) {
            showError("Le nombre de places doit être positif");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReservationList.fxml"));
            Parent content = loader.load();
            StackPane contentArea = (StackPane) cmbEvenement.getScene().lookup("#contentArea");
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
