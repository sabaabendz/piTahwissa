package tn.esprit.tahwissa.controllers.agent;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.tahwissa.models.ReservationVoyage;
import tn.esprit.tahwissa.models.ReservationVoyage.StatutReservation;
import tn.esprit.tahwissa.services.ReservationVoyageService;
import tn.esprit.tahwissa.services.VoyageService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationFormController {

    @FXML private TextField userIdField;
    @FXML private TextField voyageIdField;
    @FXML private Spinner<Integer> personnesSpinner;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<StatutReservation> statutCombo; // only used in edit
    @FXML private Label userIdError;
    @FXML private Label voyageIdError;
    @FXML private Label personnesError;
    @FXML private Label dateError;

    private ReservationVoyage existingReservation; // null for create, set for edit
    private Runnable onSaveCallback;

    private ReservationVoyageService reservationService;
    private VoyageService voyageService;

    public void setExistingReservation(ReservationVoyage reservation) {
        this.existingReservation = reservation;
        if (reservation != null) {
            userIdField.setText(String.valueOf(reservation.getIdUtilisateur()));
            voyageIdField.setText(String.valueOf(reservation.getIdVoyage()));
            personnesSpinner.getValueFactory().setValue(reservation.getNbrPersonnes());
            datePicker.setValue(reservation.getDateReservation().toLocalDate());
            statutCombo.setValue(reservation.getStatut());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        try {
            reservationService = new ReservationVoyageService();
            voyageService = new VoyageService();
            personnesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
            if (statutCombo != null) {
                statutCombo.getItems().setAll(StatutReservation.values());
            }
        } catch (SQLException e) {
            showError("Service error", e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) return;

        int userId = Integer.parseInt(userIdField.getText());
        int voyageId = Integer.parseInt(voyageIdField.getText());
        int personnes = personnesSpinner.getValue();
        LocalDate date = datePicker.getValue();

        var voyage = voyageService.getVoyageById(voyageId);
        if (voyage == null) {
            voyageIdError.setText("Voyage introuvable");
            return;
        }

        BigDecimal montant = voyage.getPrixUnitaire().multiply(BigDecimal.valueOf(personnes));

        ReservationVoyage newReservation = new ReservationVoyage(
                0, userId, voyageId,
                date.atStartOfDay(),
                StatutReservation.EN_ATTENTE,
                personnes,
                montant
        );

        boolean success = reservationService.addReservation(newReservation);
        if (success) {
            closeTab();
            if (onSaveCallback != null) onSaveCallback.run();
        } else {
            showError("Erreur", "Échec de l'enregistrement. Vérifiez les données.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (existingReservation == null) return;

        if (!validateInputs()) return;
        
        StatutReservation oldStatus = existingReservation.getStatut();
        StatutReservation newStatus = statutCombo.getValue();

        existingReservation.setIdUtilisateur(Integer.parseInt(userIdField.getText()));
        existingReservation.setIdVoyage(Integer.parseInt(voyageIdField.getText()));
        existingReservation.setNbrPersonnes(personnesSpinner.getValue());
        existingReservation.setDateReservation(datePicker.getValue().atStartOfDay());
        existingReservation.setStatut(statutCombo.getValue());

        var voyage = voyageService.getVoyageById(existingReservation.getIdVoyage());
        if (voyage != null) {
            existingReservation.setMontantTotal(
                    voyage.getPrixUnitaire().multiply(BigDecimal.valueOf(existingReservation.getNbrPersonnes()))
            );
        }

        boolean success = reservationService.updateReservation(existingReservation);
        if (success) {
            // Send Application Email if status changed to CONFIRMEE or ANNULEE
            if (oldStatus != newStatus && (newStatus == StatutReservation.CONFIRMEE || newStatus == StatutReservation.ANNULEE)) {
                try {
                    tn.esprit.tahwissa.services.UserService userService = new tn.esprit.tahwissa.services.UserService();
                    tn.esprit.tahwissa.models.User client = userService.read().stream()
                        .filter(u -> u.getId() == existingReservation.getIdUtilisateur())
                        .findFirst()
                        .orElse(null);
                        
                    if (client != null && client.getEmail() != null) {
                        tn.esprit.tahwissa.services.MailService mailService = new tn.esprit.tahwissa.services.MailService();
                        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        String formattedDate = existingReservation.getDateReservation() != null ? existingReservation.getDateReservation().format(dtf) : "-";
                        
                        String voyageName = "Réservation #" + existingReservation.getId();
                        if (voyage != null) {
                             voyageName = voyage.getTitre();
                        }
                        
                        System.out.println("✅ Preparation de l'email pour le client: " + client.getEmail());
                        System.out.println("   - Voyage: " + voyageName);
                        System.out.println("   - Status: " + newStatus.getLabel().toUpperCase());
                        
                        mailService.sendReservationStatusEmail(
                            client.getEmail(),
                            client.getFirstName() + " " + client.getLastName(),
                            voyageName,
                            newStatus.getLabel().toUpperCase(),
                            existingReservation.getMontantTotal().toString(),
                            formattedDate,
                            "N/A"
                        );
                        System.out.println("✅ Email API appele avec succes dans ReservationFormController.");
                    } else {
                        System.out.println("❌ Impossible d'envoyer l'email: Client introuvable ou email manquant (Reservation IDUtilisateur = " + existingReservation.getIdUtilisateur() + ")");
                    }
                } catch (Exception ex) {
                    System.err.println("❌ Could not send email on agent update: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                 System.out.println("⚠️ Status inchange ou pas CONFIRMEE/ANNULEE. Old: " + oldStatus + ", New: " + newStatus);
            }

            closeTab();
            if (onSaveCallback != null) onSaveCallback.run();
        } else {
            showError("Erreur", "Échec de la mise à jour.");
        }
    }
    @FXML
    private void handleCancel() {
        closeTab();
    }

    private void closeTab() {
        TabPane tabPane = (TabPane) userIdField.getScene().lookup("#tabPane");
        if (tabPane != null) {
            tabPane.getTabs().removeIf(tab -> tab.getContent() == userIdField.getScene().getRoot());
        }
    }
    private void clearErrors() {
        userIdError.setText("");
        voyageIdError.setText("");
        personnesError.setText("");
        dateError.setText("");
    }

    // Validate fields, return true if all valid
    private boolean validateInputs() {
        clearErrors();
        boolean valid = true;

        // Client ID
        try {
            Integer.parseInt(userIdField.getText());
        } catch (NumberFormatException e) {
            userIdError.setText("ID client invalide");
            valid = false;
        }

        // Voyage ID
        try {
            Integer.parseInt(voyageIdField.getText());
        } catch (NumberFormatException e) {
            voyageIdError.setText("ID voyage invalide");
            valid = false;
        }

        // Date
        if (datePicker.getValue() == null) {
            dateError.setText("Date requise");
            valid = false;
        }

        return valid;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}