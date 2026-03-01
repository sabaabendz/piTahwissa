package controllers;

import entites.Transport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.TransportService;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class TransportController {

    // Table view elements
    @FXML
    private TableView<Transport> transportTable;

    @FXML
    private TableColumn<Transport, Integer> idColumn;

    @FXML
    private TableColumn<Transport, String> typeColumn;

    @FXML
    private TableColumn<Transport, String> villeDepartColumn;

    @FXML
    private TableColumn<Transport, String> villeArriveeColumn;

    @FXML
    private TableColumn<Transport, String> dateDepartColumn;

    @FXML
    private TableColumn<Transport, String> heureDepartColumn;

    @FXML
    private TableColumn<Transport, Integer> dureeColumn;

    @FXML
    private TableColumn<Transport, Double> prixColumn;

    @FXML
    private TableColumn<Transport, Integer> nbPlacesColumn;

    @FXML
    private TableColumn<Transport, Void> actionsColumn;

    @FXML
    private TextField searchField;

    // Form elements
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField villeDepartField;

    @FXML
    private TextField villeArriveeField;

    @FXML
    private DatePicker dateDepartPicker;

    @FXML
    private Spinner<Integer> dureeSpinner;

    @FXML
    private TextField prixField;

    @FXML
    private Spinner<Integer> nbPlacesSpinner;

    // View elements
    @FXML
    private Label viewIdLabel;

    @FXML
    private Label viewTypeLabel;

    @FXML
    private Label viewVilleDepartLabel;

    @FXML
    private Label viewVilleArriveeLabel;

    @FXML
    private Label viewDateDepartLabel;

    @FXML
    private Label viewHeureDepartLabel;

    @FXML
    private Label viewDureeLabel;

    @FXML
    private Label viewPrixLabel;

    @FXML
    private Label viewNbPlacesLabel;

    private TransportService transportService;
    private ObservableList<Transport> transportList;
    private Transport currentTransport;
    private Stage currentStage;
    private TransportController parentController;
    private StackPane contentArea;

    public TransportController() {
        transportService = new TransportService();
        transportList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Initialize table if present (transport_list.fxml)
        if (transportTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idTransport"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeTransport"));
            villeDepartColumn.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
            villeArriveeColumn.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
            dateDepartColumn.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
            heureDepartColumn.setCellValueFactory(new PropertyValueFactory<>("heureDepart"));
            dureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
            prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
            nbPlacesColumn.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));

            configureActionsColumn();
            loadTransports();
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTransports(newValue));
        }

        // Initialize form if present (transport_form.fxml)
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("Avion", "Train", "Bus", "Bateau", "Voiture");
            typeComboBox.setValue("Avion");
            
            dureeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 72, 2));
            nbPlacesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 50));
            dateDepartPicker.setValue(LocalDate.now());
        }
    }

    private void configureActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏");
            private final Button deleteBtn = new Button("🗑");
            private final Button reservationsBtn = new Button("📋");
            private final HBox pane;

            {
                viewBtn.getStyleClass().add("action-button");
                editBtn.getStyleClass().add("action-button");
                deleteBtn.getStyleClass().add("action-button");
                reservationsBtn.getStyleClass().add("action-button");
                
                viewBtn.setTooltip(new Tooltip("Voir détails"));
                editBtn.setTooltip(new Tooltip("Modifier"));
                deleteBtn.setTooltip(new Tooltip("Supprimer"));
                reservationsBtn.setTooltip(new Tooltip("Voir réservations"));

                viewBtn.setOnAction(event -> {
                    Transport transport = getTableView().getItems().get(getIndex());
                    viewTransport(transport);
                });

                editBtn.setOnAction(event -> {
                    Transport transport = getTableView().getItems().get(getIndex());
                    editTransport(transport);
                });

                deleteBtn.setOnAction(event -> {
                    Transport transport = getTableView().getItems().get(getIndex());
                    deleteTransport(transport);
                });
                
                reservationsBtn.setOnAction(event -> {
                    Transport transport = getTableView().getItems().get(getIndex());
                    viewReservations(transport);
                });

                pane = new HBox(5, viewBtn, editBtn, reservationsBtn, deleteBtn);
                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadTransports() {
        try {
            transportList.clear();
            transportList.addAll(transportService.recupererTransport());
            transportTable.setItems(transportList);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des transports", e.getMessage());
        }
    }

    private void filterTransports(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            transportTable.setItems(transportList);
            return;
        }

        ObservableList<Transport> filteredList = FXCollections.observableArrayList();
        String lowerCaseFilter = searchText.toLowerCase();

        for (Transport transport : transportList) {
            if (transport.getTypeTransport().toLowerCase().contains(lowerCaseFilter) ||
                transport.getVilleDepart().toLowerCase().contains(lowerCaseFilter) ||
                transport.getVilleArrivee().toLowerCase().contains(lowerCaseFilter)) {
                filteredList.add(transport);
            }
        }

        transportTable.setItems(filteredList);
    }

    @FXML
    private void handleAddTransport() {
        openTransportForm(null);
    }

    @FXML
    private void handleRefresh() {
        loadTransports();
        searchField.clear();
    }

    private void viewTransport(Transport transport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport_view.fxml"));
            Parent root = loader.load();

            TransportController controller = loader.getController();
            controller.setTransportForView(transport);
            controller.setContentArea(contentArea);
            controller.setParentController(this);

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } else {
                Stage stage = new Stage();
                stage.setTitle("Détails du Transport");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
            }
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la vue du transport");
            e.printStackTrace();
        }
    }

    public void setTransportForView(Transport transport) {
        viewIdLabel.setText(String.valueOf(transport.getIdTransport()));
        viewTypeLabel.setText(transport.getTypeTransport());
        viewVilleDepartLabel.setText(transport.getVilleDepart());
        viewVilleArriveeLabel.setText(transport.getVilleArrivee());
        viewDateDepartLabel.setText(transport.getDateDepart() != null ? transport.getDateDepart().toString() : "N/A");
        viewHeureDepartLabel.setText(transport.getHeureDepart() != null ? transport.getHeureDepart().toString() : "N/A");
        viewDureeLabel.setText(transport.getDuree() + " heures");
        viewPrixLabel.setText(String.format("%.2f TND", transport.getPrix()));
        viewNbPlacesLabel.setText(String.valueOf(transport.getNbPlaces()));
    }

    private void editTransport(Transport transport) {
        openTransportForm(transport);
    }

    private void deleteTransport(Transport transport) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le transport");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce transport de " +
                transport.getVilleDepart() + " à " + transport.getVilleArrivee() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                transportService.supprimerTransport(transport.getIdTransport());
                loadTransports();
                showSuccess("Transport supprimé avec succès !");
            } catch (SQLException e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    private void viewReservations(Transport transport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_transport_list.fxml"));
            Parent root = loader.load();

            ReservationTransportController controller = loader.getController();
            controller.setTransportFilter(transport);
            controller.setContentArea(contentArea);

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } else {
                Stage stage = new Stage();
                stage.setTitle("Réservations - " + transport.getTypeTransport() + " (" + 
                              transport.getVilleDepart() + " → " + transport.getVilleArrivee() + ")");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root, 900, 600));
                stage.showAndWait();
            }
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir les réservations");
            e.printStackTrace();
        }
    }

    private void openTransportForm(Transport transport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport_form.fxml"));
            Parent root = loader.load();

            TransportController controller = loader.getController();
            controller.setTransportForEdit(transport);
            controller.setParentController(this);
            controller.setContentArea(contentArea);

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } else {
                Stage stage = new Stage();
                stage.setTitle(transport == null ? "Ajouter un Transport" : "Modifier le Transport");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                controller.currentStage = stage;
                stage.showAndWait();
            }
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire");
            e.printStackTrace();
        }
    }

    public void setParentController(TransportController parent) {
        this.parentController = parent;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public void setTransportForEdit(Transport transport) {
        currentTransport = transport;
        if (transport != null) {
            typeComboBox.setValue(transport.getTypeTransport());
            villeDepartField.setText(transport.getVilleDepart());
            villeArriveeField.setText(transport.getVilleArrivee());
            if (transport.getDateDepart() != null) {
                dateDepartPicker.setValue(transport.getDateDepart().toLocalDate());
            }
            dureeSpinner.getValueFactory().setValue(transport.getDuree());
            prixField.setText(String.valueOf(transport.getPrix()));
            nbPlacesSpinner.getValueFactory().setValue(transport.getNbPlaces());
        }
    }

    @FXML
    private void handleSaveForm() {
        // Validation
        if (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            showError("Validation", "Veuillez sélectionner un type de transport");
            return;
        }
        if (villeDepartField.getText().trim().isEmpty()) {
            showError("Validation", "Veuillez saisir la ville de départ");
            return;
        }
        if (villeArriveeField.getText().trim().isEmpty()) {
            showError("Validation", "Veuillez saisir la ville d'arrivée");
            return;
        }
        if (dateDepartPicker.getValue() == null) {
            showError("Validation", "Veuillez sélectionner une date de départ");
            return;
        }
        if (prixField.getText().trim().isEmpty()) {
            showError("Validation", "Veuillez saisir un prix");
            return;
        }

        try {
            Transport t = currentTransport != null ? currentTransport : new Transport();
            
            t.setTypeTransport(typeComboBox.getValue());
            t.setVilleDepart(villeDepartField.getText());
            t.setVilleArrivee(villeArriveeField.getText());
            t.setDateDepart(Date.valueOf(dateDepartPicker.getValue()));
            
            // Use current local time
            LocalTime currentTime = LocalTime.now();
            t.setHeureDepart(Time.valueOf(currentTime));
            
            t.setDuree(dureeSpinner.getValue());
            t.setPrix(Double.parseDouble(prixField.getText()));
            t.setNbPlaces(nbPlacesSpinner.getValue());

            if (currentTransport == null) {
                transportService.ajouterTransport(t);
                showSuccess("Transport ajouté avec succès !");
            } else {
                transportService.modifierTransport(t);
                showSuccess("Transport modifié avec succès !");
            }

            if (currentStage != null) {
                if (parentController != null) {
                    parentController.loadTransports();
                }
                currentStage.close();
            } else if (contentArea != null) {
                loadTransportList();
            }
        } catch (SQLException e) {
            showError("Erreur lors de l'enregistrement", e.getMessage());
        } catch (NumberFormatException e) {
            showError("Erreur", "Le prix doit être un nombre valide");
        }
    }

    @FXML
    private void handleCancelForm() {
        if (currentStage != null) {
            currentStage.close();
        } else if (contentArea != null) {
            loadTransportList();
        }
    }

    @FXML
    private void handleBackToList() {
        loadTransportList();
    }

    private void loadTransportList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transport_list.fxml"));
            Parent root = loader.load();
            TransportController controller = loader.getController();
            controller.setContentArea(contentArea);
            
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
