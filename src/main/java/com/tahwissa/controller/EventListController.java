package com.tahwissa.controller;

import com.tahwissa.entity.Evenement;
import com.tahwissa.service.EvenementService;
import com.tahwissa.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventListController {
    @FXML private TableView<Evenement> tableEvenements;
    @FXML private TableColumn<Evenement, Integer> colId;
    @FXML private TableColumn<Evenement, String> colTitre;
    @FXML private TableColumn<Evenement, String> colLieu;
    @FXML private TableColumn<Evenement, LocalDate> colDate;
    @FXML private TableColumn<Evenement, LocalTime> colHeure;
    @FXML private TableColumn<Evenement, Double> colPrix;
    @FXML private TableColumn<Evenement, Integer> colPlaces;
    @FXML private TableColumn<Evenement, String> colCategorie;
    @FXML private TableColumn<Evenement, String> colStatut;
    @FXML private TextField txtSearch;
    @FXML private Button btnCreate;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final EvenementService evenementService = new EvenementService();
    private final ObservableList<Evenement> evenements = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadEvenements();
        
        // Cacher les boutons pour les utilisateurs non-admin
        if (!SessionManager.getInstance().isAdmin()) {
            btnCreate.setVisible(false);
            btnCreate.setManaged(false);
            btnEdit.setVisible(false);
            btnEdit.setManaged(false);
            btnDelete.setVisible(false);
            btnDelete.setManaged(false);
        }
        
        // Gérer la sélection dans le tableau
        tableEvenements.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            if (SessionManager.getInstance().isAdmin()) {
                btnEdit.setDisable(!isSelected);
                btnDelete.setDisable(!isSelected);
            }
        });
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEvenement"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEvent"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heureEvent"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        // Formatter les colonnes
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colDate.setCellFactory(column -> new TableCell<Evenement, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(dateFormatter));
            }
        });
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        colHeure.setCellFactory(column -> new TableCell<Evenement, LocalTime>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(timeFormatter));
            }
        });
        
        colPrix.setCellFactory(column -> new TableCell<Evenement, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f DT", item));
            }
        });
        
        // Style pour le statut
        colStatut.setCellFactory(column -> new TableCell<Evenement, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("DISPONIBLE")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else if (item.equals("COMPLET")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        tableEvenements.setItems(evenements);
    }

    private void loadEvenements() {
        evenements.clear();
        if (SessionManager.getInstance().isAdmin()) {
            evenements.addAll(evenementService.getAllEvenements());
        } else {
            evenements.addAll(evenementService.getAvailableEvenements());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        evenements.clear();
        
        if (keyword.isEmpty()) {
            loadEvenements();
        } else {
            evenements.addAll(evenementService.searchEvenements(keyword));
        }
    }

    @FXML
    private void handleCreate() {
        loadForm(null);
    }

    @FXML
    private void handleEdit() {
        Evenement selectedEvenement = tableEvenements.getSelectionModel().getSelectedItem();
        if (selectedEvenement != null) {
            loadForm(selectedEvenement);
        }
    }

    @FXML
    private void handleDelete() {
        Evenement selectedEvenement = tableEvenements.getSelectionModel().getSelectedItem();
        if (selectedEvenement == null) {
            showError("Veuillez sélectionner un événement à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'événement : " + selectedEvenement.getTitre());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet événement ? Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (evenementService.deleteEvenement(selectedEvenement.getIdEvenement())) {
                showSuccess("Événement supprimé avec succès");
                loadEvenements();
            } else {
                showError("Erreur lors de la suppression");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadEvenements();
    }

    private void loadForm(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EventForm.fxml"));
            Parent content = loader.load();
            
            if (evenement != null) {
                EventFormController controller = loader.getController();
                controller.setEvenement(evenement);
            }
            
            StackPane contentArea = (StackPane) tableEvenements.getScene().lookup("#contentArea");
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
