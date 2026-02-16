package com.tahwissa.controller;

import com.tahwissa.entity.ReservationEvenement;
import com.tahwissa.service.ReservationService;
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

public class ReservationListController {
    @FXML private TableView<ReservationEvenement> tableReservations;
    @FXML private TableColumn<ReservationEvenement, Integer> colId;
    @FXML private TableColumn<ReservationEvenement, String> colEvenement;
    @FXML private TableColumn<ReservationEvenement, Integer> colPlaces;
    @FXML private TableColumn<ReservationEvenement, String> colStatut;
    @FXML private TableColumn<ReservationEvenement, LocalDateTime> colDate;
    @FXML private TableColumn<ReservationEvenement, String> colUtilisateur;
    @FXML private Button btnCreate;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final ReservationService reservationService = new ReservationService();
    private final ObservableList<ReservationEvenement> reservations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadReservations();
        
        if (!SessionManager.getInstance().isAdmin()) {
            colUtilisateur.setVisible(false);
        }
        
        tableReservations.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            btnEdit.setDisable(!isSelected);
            btnDelete.setDisable(!isSelected);
        });
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        colEvenement.setCellValueFactory(new PropertyValueFactory<>("titreEvenement"));
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("nbPlacesReservees"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        colUtilisateur.setCellValueFactory(new PropertyValueFactory<>("nomUser"));
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(dateFormatter));
            }
        });
        
        colStatut.setCellFactory(column -> new TableCell<>() {
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
                        case "CONFIRMEE":
                            setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                            break;
                        case "ANNULEE":
                            setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        tableReservations.setItems(reservations);
    }

    private void loadReservations() {
        reservations.clear();
        reservations.addAll(reservationService.getAllReservations());
    }

    @FXML
    private void handleCreate() {
        loadForm(null);
    }

    @FXML
    private void handleEdit() {
        ReservationEvenement selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!SessionManager.getInstance().isAdmin() && 
                selected.getIdUser() != SessionManager.getInstance().getCurrentUserId()) {
                showError("Vous ne pouvez modifier que vos propres réservations");
                return;
            }
            loadForm(selected);
        }
    }

    @FXML
    private void handleDelete() {
        ReservationEvenement selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une réservation");
            return;
        }
        
        if (!SessionManager.getInstance().isAdmin() && 
            selected.getIdUser() != SessionManager.getInstance().getCurrentUserId()) {
            showError("Vous ne pouvez supprimer que vos propres réservations");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la réservation ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reservationService.deleteReservation(selected.getIdReservation())) {
                showSuccess("Réservation supprimée");
                loadReservations();
            } else {
                showError("Erreur lors de la suppression");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadReservations();
    }

    private void loadForm(ReservationEvenement reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReservationForm.fxml"));
            Parent content = loader.load();
            
            if (reservation != null) {
                ReservationFormController controller = loader.getController();
                controller.setReservation(reservation);
            }
            
            StackPane contentArea = (StackPane) tableReservations.getScene().lookup("#contentArea");
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
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
