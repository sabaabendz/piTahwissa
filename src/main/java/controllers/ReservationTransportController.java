package controllers;

import entites.Reservation_transport;
import entites.Transport;
import entites.Utilisateur;
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
import services.Reservation_transportService;
import services.TransportService;
import services.UtilisateurService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationTransportController {

    // Table view elements
    @FXML
    private TableView<Reservation_transport> reservationTable;

    @FXML
    private TableColumn<Reservation_transport, Integer> idColumn;

    @FXML
    private TableColumn<Reservation_transport, Date> dateColumn;

    @FXML
    private TableColumn<Reservation_transport, Integer> nbPlacesColumn;

    @FXML
    private TableColumn<Reservation_transport, String> statutColumn;

    @FXML
    private TableColumn<Reservation_transport, String> transportColumn;

    @FXML
    private TableColumn<Reservation_transport, String> userColumn;

    @FXML
    private TableColumn<Reservation_transport, Void> actionsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterStatutCombo;

    @FXML
    private Button backToTransportBtn;

    // Form elements
    @FXML
    private Spinner<Integer> nbPlacesSpinner;

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private ComboBox<String> transportComboBox;

    @FXML
    private ComboBox<String> userComboBox;

    // View elements
    @FXML
    private Label viewIdLabel;

    @FXML
    private Label viewDateLabel;

    @FXML
    private Label viewNbPlacesLabel;

    @FXML
    private Label viewStatutLabel;

    @FXML
    private Label viewTransportLabel;

    @FXML
    private Label viewUserLabel;

    private Reservation_transportService reservationService;
    private TransportService transportService;
    private UtilisateurService utilisateurService;
    private ObservableList<Reservation_transport> reservationList;
    private ObservableList<Transport> transportList;
    private ObservableList<Utilisateur> userList;
    private Reservation_transport currentReservation;
    private Stage currentStage;
    private ReservationTransportController parentController;
    private Integer filterByTransportId = null;
    private StackPane contentArea;

    public ReservationTransportController() {
        reservationService = new Reservation_transportService();
        transportService = new TransportService();
        utilisateurService = new UtilisateurService();
        reservationList = FXCollections.observableArrayList();
        transportList = FXCollections.observableArrayList();
        userList = FXCollections.observableArrayList();
    }

    public void setTransportFilter(Transport transport) {
        this.filterByTransportId = transport.getIdTransport();
        if (reservationTable != null) {
            loadReservations();
        }
        // Show back button when filtering by transport
        if (backToTransportBtn != null) {
            backToTransportBtn.setVisible(true);
            backToTransportBtn.setManaged(true);
        }
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @FXML
    public void initialize() {
        // Initialize table if present
        if (reservationTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
            nbPlacesColumn.setCellValueFactory(new PropertyValueFactory<>("nbPlacesReservees"));
            statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
            transportColumn.setCellValueFactory(new PropertyValueFactory<>("transportDisplay"));
            userColumn.setCellValueFactory(new PropertyValueFactory<>("userDisplay"));

            configureActionsColumn();
            loadReservations();
            loadTransports();
            
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterReservations());
            
            // Setup filter
            filterStatutCombo.getItems().addAll("Tous", "En attente", "Confirmée", "Annulée");
            filterStatutCombo.setValue("Tous");
            filterStatutCombo.setOnAction(e -> filterReservations());
        }

        // Initialize form if present
        if (statutComboBox != null) {
            statutComboBox.getItems().addAll("En attente", "Confirmée", "Annulée");
            statutComboBox.setValue("En attente");
            
            nbPlacesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
            
            // Load users from database
            loadUsers();
        }
    }

    private void loadUsers() {
        try {
            userList.clear();
            userList.addAll(utilisateurService.recupererUtilisateurs());
            
            if (userComboBox != null) {
                userComboBox.getItems().clear();
                for (Utilisateur u : userList) {
                    userComboBox.getItems().add(u.getIdUser() + " - " + u.getNom());
                }
                if (!userComboBox.getItems().isEmpty()) {
                    userComboBox.setValue(userComboBox.getItems().get(0));
                }
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des utilisateurs", e.getMessage());
        }
    }

    private void configureActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏");
            private final Button deleteBtn = new Button("🗑");
            private final HBox pane;

            {
                viewBtn.getStyleClass().add("action-button");
                editBtn.getStyleClass().add("action-button");
                deleteBtn.getStyleClass().add("action-button");

                viewBtn.setOnAction(event -> {
                    Reservation_transport reservation = getTableView().getItems().get(getIndex());
                    viewReservation(reservation);
                });

                editBtn.setOnAction(event -> {
                    Reservation_transport reservation = getTableView().getItems().get(getIndex());
                    editReservation(reservation);
                });

                deleteBtn.setOnAction(event -> {
                    Reservation_transport reservation = getTableView().getItems().get(getIndex());
                    deleteReservation(reservation);
                });

                pane = new HBox(5, viewBtn, editBtn, deleteBtn);
                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadReservations() {
        try {
            reservationList.clear();
            List<Reservation_transport> allReservations = reservationService.recupererReservations();
            
            // Load transports and users for display
            List<Transport> transports = transportService.recupererTransport();
            List<Utilisateur> users = utilisateurService.recupererUtilisateurs();
            
            // Filter by transport if set
            if (filterByTransportId != null) {
                for (Reservation_transport r : allReservations) {
                    if (r.getIdTransport() == filterByTransportId) {
                        populateDisplayFields(r, transports, users);
                        reservationList.add(r);
                    }
                }
            } else {
                for (Reservation_transport r : allReservations) {
                    populateDisplayFields(r, transports, users);
                    reservationList.add(r);
                }
            }
            
            reservationTable.setItems(reservationList);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des réservations", e.getMessage());
        }
    }

    private void populateDisplayFields(Reservation_transport reservation, List<Transport> transports, List<Utilisateur> users) {
        // Set transport display
        for (Transport t : transports) {
            if (t.getIdTransport() == reservation.getIdTransport()) {
                reservation.setTransportDisplay(t.getVilleDepart() + " → " + t.getVilleArrivee());
                break;
            }
        }
        
        // Set user display
        for (Utilisateur u : users) {
            if (u.getIdUser() == reservation.getIdUser()) {
                reservation.setUserDisplay(u.getNom());
                break;
            }
        }
    }

    private void loadTransports() {
        try {
            transportList.clear();
            transportList.addAll(transportService.recupererTransport());
            
            if (transportComboBox != null) {
                transportComboBox.getItems().clear();
                for (Transport t : transportList) {
                    transportComboBox.getItems().add(
                        t.getIdTransport() + " - " + t.getTypeTransport() + 
                        " (" + t.getVilleDepart() + " → " + t.getVilleArrivee() + ")"
                    );
                }
                if (!transportComboBox.getItems().isEmpty()) {
                    transportComboBox.setValue(transportComboBox.getItems().get(0));
                }
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des transports", e.getMessage());
        }
    }

    private void filterReservations() {
        String searchText = searchField.getText();
        String statutFilter = filterStatutCombo.getValue();
        
        ObservableList<Reservation_transport> filteredList = FXCollections.observableArrayList();

        for (Reservation_transport reservation : reservationList) {
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                String.valueOf(reservation.getIdReservation()).contains(searchText) ||
                String.valueOf(reservation.getIdTransport()).contains(searchText);
                
            boolean matchesStatut = "Tous".equals(statutFilter) || 
                reservation.getStatut().equals(statutFilter);

            if (matchesSearch && matchesStatut) {
                filteredList.add(reservation);
            }
        }

        reservationTable.setItems(filteredList);
    }

    @FXML
    private void handleAddReservation() {
        openReservationForm(null);
    }

    @FXML
    private void handleRefresh() {
        loadReservations();
        loadTransports();
        searchField.clear();
        filterStatutCombo.setValue("Tous");
    }

    private void viewReservation(Reservation_transport reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_transport_view.fxml"));
            Parent root = loader.load();

            ReservationTransportController controller = loader.getController();
            controller.setReservationForView(reservation);
            controller.setContentArea(contentArea);
            controller.setParentController(this);

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } else {
                Stage stage = new Stage();
                stage.setTitle("Détails de la Réservation");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
            }
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la vue de la réservation");
            e.printStackTrace();
        }
    }

    public void setReservationForView(Reservation_transport reservation) {
        viewIdLabel.setText(String.valueOf(reservation.getIdReservation()));
        viewDateLabel.setText(reservation.getDateReservation() != null ? reservation.getDateReservation().toString() : "N/A");
        viewNbPlacesLabel.setText(String.valueOf(reservation.getNbPlacesReservees()));
        viewStatutLabel.setText(reservation.getStatut());
        
        // Get transport details
        try {
            List<Transport> transports = transportService.recupererTransport();
            for (Transport t : transports) {
                if (t.getIdTransport() == reservation.getIdTransport()) {
                    viewTransportLabel.setText(t.getTypeTransport() + " - " + t.getVilleDepart() + " → " + t.getVilleArrivee());
                    break;
                }
            }
        } catch (SQLException e) {
            viewTransportLabel.setText("ID: " + reservation.getIdTransport());
        }
        
        viewUserLabel.setText("Utilisateur ID: " + reservation.getIdUser());
    }

    @FXML
    private void handleCloseView() {
        if (contentArea != null) {
            loadReservationList();
        } else {
            Stage stage = (Stage) viewIdLabel.getScene().getWindow();
            stage.close();
        }
    }

    private void editReservation(Reservation_transport reservation) {
        openReservationForm(reservation);
    }

    private void deleteReservation(Reservation_transport reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la réservation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.supprimerReservation(reservation);
                loadReservations();
                showSuccess("Réservation supprimée avec succès !");
            } catch (SQLException e) {
                showError("Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    private void openReservationForm(Reservation_transport reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_transport_form.fxml"));
            Parent root = loader.load();

            ReservationTransportController controller = loader.getController();
            controller.loadTransports();
            controller.loadUsers();
            controller.setReservationForEdit(reservation);
            controller.setParentController(this);
            controller.setContentArea(contentArea);

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } else {
                Stage stage = new Stage();
                stage.setTitle(reservation == null ? "Ajouter une Réservation" : "Modifier la Réservation");
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

    public void setParentController(ReservationTransportController parent) {
        this.parentController = parent;
    }

    public void setReservationForEdit(Reservation_transport reservation) {
        currentReservation = reservation;
        if (reservation != null) {
            nbPlacesSpinner.getValueFactory().setValue(reservation.getNbPlacesReservees());
            statutComboBox.setValue(reservation.getStatut());
            
            // Set transport
            for (String item : transportComboBox.getItems()) {
                if (item.startsWith(reservation.getIdTransport() + " - ")) {
                    transportComboBox.setValue(item);
                    break;
                }
            }
            
            // Set user
            for (String item : userComboBox.getItems()) {
                if (item.startsWith(reservation.getIdUser() + " - ")) {
                    userComboBox.setValue(item);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSaveForm() {
        // Validation
        if (transportComboBox.getValue() == null) {
            showError("Validation", "Veuillez sélectionner un transport");
            return;
        }
        if (userComboBox.getValue() == null) {
            showError("Validation", "Veuillez sélectionner un utilisateur");
            return;
        }

        try {
            Reservation_transport r = currentReservation != null ? currentReservation : new Reservation_transport();
            
            // Use current date
            r.setDateReservation(Date.valueOf(LocalDate.now()));
            r.setNbPlacesReservees(nbPlacesSpinner.getValue());
            r.setStatut(statutComboBox.getValue());
            
            // Extract transport ID
            String transportStr = transportComboBox.getValue();
            int idTransport = Integer.parseInt(transportStr.substring(0, transportStr.indexOf(" - ")));
            r.setIdTransport(idTransport);
            
            // Extract user ID
            String userStr = userComboBox.getValue();
            int idUser = Integer.parseInt(userStr.substring(0, userStr.indexOf(" - ")));
            r.setIdUser(idUser);

            if (currentReservation == null) {
                reservationService.ajouterReservation(r);
                showSuccess("Réservation ajoutée avec succès !");
            } else {
                reservationService.modifierReservation(r);
                showSuccess("Réservation modifiée avec succès !");
            }

            if (currentStage != null) {
                if (parentController != null) {
                    parentController.loadReservations();
                }
                currentStage.close();
            } else if (contentArea != null) {
                loadReservationList();
            }
        } catch (SQLException e) {
            showError("Erreur lors de l'enregistrement", e.getMessage());
        } catch (Exception e) {
            showError("Erreur", "Données invalides");
        }
    }

    @FXML
    private void handleCancelForm() {
        if (currentStage != null) {
            currentStage.close();
        } else if (contentArea != null) {
            loadReservationList();
        }
    }

    @FXML
    private void handleBackToList() {
        loadReservationList();
    }

    @FXML
    private void handleBackToTransports() {
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

    private void loadReservationList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_transport_list.fxml"));
            Parent root = loader.load();
            ReservationTransportController controller = loader.getController();
            controller.setContentArea(contentArea);
            if (filterByTransportId != null) {
                Transport temp = new Transport();
                temp.setIdTransport(filterByTransportId);
                controller.setTransportFilter(temp);
            }
            
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
