package tn.esprit.tahwissa.controllers.event;

import tn.esprit.tahwissa.models.Evenement;
import tn.esprit.tahwissa.models.ReservationEvenement;
import tn.esprit.tahwissa.services.EvenementService;
import tn.esprit.tahwissa.services.ReservationService;
import tn.esprit.tahwissa.utils.SessionManager;
import tn.esprit.tahwissa.utils.Validator;
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
    @FXML private Label lblPlacesDisponibles;
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

        cmbEvenement.valueProperty().addListener((o, oldVal, newVal) -> updatePlacesForEvent());
        
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

    /** Pre-select an event when opening the form from the event list (e.g. "Réserver" on a card). */
    public void setEvenement(Evenement evenement) {
        if (evenement == null) return;
        loadEvenements();
        for (Evenement item : cmbEvenement.getItems()) {
            if (item.getIdEvenement() == evenement.getIdEvenement()) {
                cmbEvenement.setValue(item);
                updatePlacesForEvent();
                return;
            }
        }
        cmbEvenement.setValue(evenement);
        updatePlacesForEvent();
    }

    private void updatePlacesForEvent() {
        Evenement ev = cmbEvenement.getValue();
        if (ev == null) {
            lblPlacesDisponibles.setText("");
            SpinnerValueFactory.IntegerSpinnerValueFactory factory = (SpinnerValueFactory.IntegerSpinnerValueFactory) spnPlaces.getValueFactory();
            factory.setMin(1);
            factory.setMax(50);
            spnPlaces.getValueFactory().setValue(1);
            return;
        }
        int available = reservationService.getAvailablePlacesForEvent(ev.getIdEvenement());
        if (currentReservation != null && currentReservation.getIdEvenement() == ev.getIdEvenement()) {
            available += currentReservation.getNbPlacesReservees();
        }
        lblPlacesDisponibles.setText(available + " place(s) disponible(s) pour cet événement.");
        int max = Math.max(1, available);
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = (SpinnerValueFactory.IntegerSpinnerValueFactory) spnPlaces.getValueFactory();
        factory.setMin(1);
        factory.setMax(max);
        int current = spnPlaces.getValue();
        if (current > max) {
            spnPlaces.getValueFactory().setValue(max);
        }
    }

    private void fillForm() {
        Evenement evenement = evenementService.getEvenementById(currentReservation.getIdEvenement());
        cmbEvenement.setValue(evenement);
        cmbEvenement.setDisable(true);
        updatePlacesForEvent();
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
        Evenement ev = cmbEvenement.getValue();
        int available = reservationService.getAvailablePlacesForEvent(ev.getIdEvenement());
        if (currentReservation != null && currentReservation.getIdEvenement() == ev.getIdEvenement()) {
            available += currentReservation.getNbPlacesReservees();
        }
        if (spnPlaces.getValue() > available) {
            showError("Il ne reste que " + available + " place(s) disponible(s) pour cet événement.");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event/ReservationList.fxml"));
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
