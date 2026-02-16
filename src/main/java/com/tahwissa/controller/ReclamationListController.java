package com.tahwissa.controller;

import com.tahwissa.entity.Reclamation;
import com.tahwissa.service.ReclamationService;
import com.tahwissa.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ReclamationListController {
    @FXML private TableView<Reclamation> tableReclamations;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String> colTitre;
    @FXML private TableColumn<Reclamation, String> colType;
    @FXML private TableColumn<Reclamation, String> colStatut;
    @FXML private TableColumn<Reclamation, LocalDateTime> colDate;
    @FXML private TableColumn<Reclamation, String> colUtilisateur;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterStatut;
    @FXML private Button btnCreate;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final ReclamationService reclamationService = new ReclamationService();
    private final ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadReclamations();
        
        // Configurer le filtre de statut
        cmbFilterStatut.getItems().addAll("Tous", "EN_ATTENTE", "EN_COURS", "TRAITEE", "CLOTUREE");
        cmbFilterStatut.setValue("Tous");
        
        // Cacher la colonne utilisateur pour les non-admin
        if (!SessionManager.getInstance().isAdmin()) {
            colUtilisateur.setVisible(false);
        }
        
        // Gérer la sélection dans le tableau
        tableReclamations.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            btnEdit.setDisable(!isSelected);
            btnDelete.setDisable(!isSelected);
        });
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReclamation"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colUtilisateur.setCellValueFactory(new PropertyValueFactory<>("nomUser"));
        
        // Formatter la date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colDate.setCellFactory(column -> new TableCell<Reclamation, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(dateFormatter));
            }
        });
        
        // Style pour le statut
        colStatut.setCellFactory(column -> new TableCell<Reclamation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "EN_ATTENTE":
                            setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                            break;
                        case "EN_COURS":
                            setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                            break;
                        case "TRAITEE":
                            setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                            break;
                        case "CLOTUREE":
                            setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        tableReclamations.setItems(reclamations);
    }

    private void loadReclamations() {
        reclamations.clear();
        reclamations.addAll(reclamationService.getAllReclamations());
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        reclamations.clear();
        
        if (keyword.isEmpty()) {
            loadReclamations();
        } else {
            reclamations.addAll(reclamationService.searchReclamations(keyword));
        }
    }

    @FXML
    private void handleFilterStatut() {
        String statut = cmbFilterStatut.getValue();
        reclamations.clear();
        
        if (statut.equals("Tous")) {
            loadReclamations();
        } else {
            reclamations.addAll(reclamationService.getReclamationsByStatut(statut));
        }
    }

    @FXML
    private void handleCreate() {
        loadForm(null);
    }

    @FXML
    private void handleEdit() {
        Reclamation selectedReclamation = tableReclamations.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            // Les utilisateurs ne peuvent modifier que leurs propres réclamations
            if (!SessionManager.getInstance().isAdmin() && 
                selectedReclamation.getIdUser() != SessionManager.getInstance().getCurrentUserId()) {
                showError("Vous ne pouvez modifier que vos propres réclamations");
                return;
            }
            loadForm(selectedReclamation);
        }
    }

    @FXML
    private void handleDelete() {
        Reclamation selectedReclamation = tableReclamations.getSelectionModel().getSelectedItem();
        if (selectedReclamation == null) {
            showError("Veuillez sélectionner une réclamation à supprimer");
            return;
        }
        
        // Les utilisateurs ne peuvent supprimer que leurs propres réclamations
        if (!SessionManager.getInstance().isAdmin() && 
            selectedReclamation.getIdUser() != SessionManager.getInstance().getCurrentUserId()) {
            showError("Vous ne pouvez supprimer que vos propres réclamations");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la réclamation : " + selectedReclamation.getTitre());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reclamationService.deleteReclamation(selectedReclamation.getIdReclamation())) {
                showSuccess("Réclamation supprimée avec succès");
                loadReclamations();
            } else {
                showError("Erreur lors de la suppression");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cmbFilterStatut.setValue("Tous");
        loadReclamations();
    }

    private void loadForm(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReclamationForm.fxml"));
            Parent content = loader.load();
            
            if (reclamation != null) {
                ReclamationFormController controller = loader.getController();
                controller.setReclamation(reclamation);
            }
            
            StackPane contentArea = (StackPane) tableReclamations.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du formulaire");
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
