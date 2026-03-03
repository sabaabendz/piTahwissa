package tn.esprit.tahwissa.controllers.event;

import tn.esprit.tahwissa.models.Reclamation;
import tn.esprit.tahwissa.services.ReclamationService;
import tn.esprit.tahwissa.utils.SessionManager;
import tn.esprit.tahwissa.utils.Validator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class ReclamationFormController {
    @FXML private Label formTitle;
    @FXML private TextField txtTitre;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> cmbType;
    @FXML private ComboBox<String> cmbStatut;
    @FXML private Label messageLabel;
    @FXML private Button btnSubmit;
    @FXML private VBox statutBox;

    private final ReclamationService reclamationService = new ReclamationService();
    private Reclamation currentReclamation;

    @FXML
    public void initialize() {
        // Populate type ComboBox
        cmbType.getItems().addAll("Technique", "Service Client", "Information", "Facturation", "Autre");
        cmbType.setValue("Technique");

        // Gérer la visibilité du champ statut pour l'admin
        if (SessionManager.getInstance().isAdmin()) {
            statutBox.setVisible(true);
            statutBox.setManaged(true);
            // Populate status ComboBox for admin
            cmbStatut.getItems().addAll("EN_ATTENTE", "EN_COURS", "TRAITEE", "CLOTUREE");
            cmbStatut.setValue("EN_ATTENTE");
        } else {
            statutBox.setVisible(false);
            statutBox.setManaged(false);
        }
    }

    public void setReclamation(Reclamation reclamation) {
        this.currentReclamation = reclamation;
        formTitle.setText("Modifier la réclamation");
        btnSubmit.setText("Mettre à jour");
        fillForm();
    }

    private void fillForm() {
        txtTitre.setText(currentReclamation.getTitre());
        txtDescription.setText(currentReclamation.getDescription());
        cmbType.setValue(currentReclamation.getType());
        if (SessionManager.getInstance().isAdmin()) {
            cmbStatut.setValue(currentReclamation.getStatut());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;

        try {
            if (currentReclamation == null) {
                // Nouvelle réclamation
                Reclamation reclamation = new Reclamation(
                        txtTitre.getText().trim(),
                        txtDescription.getText().trim(),
                        cmbType.getValue(),
                        "EN_ATTENTE",
                        LocalDateTime.now(),
                        SessionManager.getInstance().getCurrentUserId()
                );

                if (reclamationService.createReclamation(reclamation)) {
                    showSuccess("Réclamation créée avec succès");
                    handleBackToList();
                } else {
                    showError("Erreur lors de la création");
                }
            } else {
                // Modification
                currentReclamation.setTitre(txtTitre.getText().trim());
                currentReclamation.setDescription(txtDescription.getText().trim());
                currentReclamation.setType(cmbType.getValue());

                // Seul l'admin peut modifier le statut
                if (SessionManager.getInstance().isAdmin()) {
                    currentReclamation.setStatut(cmbStatut.getValue());
                }

                if (reclamationService.updateReclamation(currentReclamation)) {
                    showSuccess("Réclamation modifiée avec succès");
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
        if (!Validator.isNotEmpty(txtTitre.getText())) {
            showError("Le titre est requis");
            return false;
        }
        if (!Validator.isNotEmpty(txtDescription.getText())) {
            showError("La description est requise");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event/ReclamationList.fxml"));
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
