package tn.esprit.tahwissa.controllers.agent;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import tn.esprit.tahwissa.utils.PDFGenerator;
import java.io.File;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.io.IOException;
import javafx.scene.layout.GridPane;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.tahwissa.models.ReservationVoyage;
import tn.esprit.tahwissa.models.ReservationVoyage.StatutReservation;
import tn.esprit.tahwissa.models.Paiement;
import tn.esprit.tahwissa.models.Voyage;
import tn.esprit.tahwissa.services.PaiementService;
import tn.esprit.tahwissa.services.ReservationVoyageService;
import tn.esprit.tahwissa.services.VoyageService;

import java.net.URL;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * GESTION RESERVATIONS CONTROLLER
 *
 * This is the ADMIN/AGENT view for managing all reservations
 * The agent can:
 * - See all reservations
 * - Filter by status
 * - Search by client/voyage
 * - Add new reservations (for walk-in clients)
 * - Modify reservations
 * - Delete reservations
 */
public class GestionReservationsController implements Initializable {

    // ════════════════════ SERVICES ════════════════════
    private ReservationVoyageService reservationService;
    private VoyageService voyageService;
    private PaiementService paiementService;
    private List<Voyage> voyagesList; // Cache voyages for dropdown

    // ════════════════════ TABLE COLUMNS ════════════════════
    @FXML private TableColumn<ReservationVoyage, Integer> idCol;
    @FXML private TableColumn<ReservationVoyage, String> clientCol;
    @FXML private TableColumn<ReservationVoyage, String> destinationCol;  // Shows voyage title
    @FXML private TableColumn<ReservationVoyage, Integer> personnesCol;
    @FXML private TableColumn<ReservationVoyage, String> montantCol;
    @FXML private TableColumn<ReservationVoyage, String> statutCol;
    @FXML private TableColumn<ReservationVoyage, String> paiementStatutCol; // NEW: payment status

    // ════════════════════ FILTERS ════════════════════
    @FXML private ComboBox<String> statutFilter;
    @FXML private TextField searchField;

    // ════════════════════ BUTTONS ════════════════════
    @FXML private Button refreshBtn;
    @FXML private Button filterBtn;
    @FXML private Button resetBtn;
    @FXML private Button nouveauBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;

    // ════════════════════ TABLE & STATS ════════════════════
    @FXML private TableView<ReservationVoyage> reservationsTable;
    @FXML private Label statsLabel;

    // ════════════════════ DATA ════════════════════
    private ObservableList<ReservationVoyage> reservationsList = FXCollections.observableArrayList();

    // ════════════════════ INITIALIZATION ════════════════════

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ GestionReservationsController initialized");

        try {
            // Create services
            reservationService = new ReservationVoyageService();
            voyageService = new VoyageService();
            paiementService = new PaiementService();
            paiementService.ensureTableExists();

            // Load voyages for later use
            voyagesList = voyageService.getAllVoyages();
            System.out.println("✅ Loaded " + voyagesList.size() + " voyages");

            // Setup UI
            setupTable();
            setupFilters();
            setupButtons();
            loadData();

        } catch (Exception e) {
            showError("Initialization Error", e.getMessage());
            e.printStackTrace();
        }
    }

    // ════════════════════ SETUP METHODS ════════════════════

    /**
     * Configure table columns to link with entity fields
     */
    /**
     * Configure table columns to link with entity fields
     */
    private void setupTable() {
        // ID Column
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Client Column
        clientCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        "Client #" + cellData.getValue().getIdUtilisateur()
                )
        );

        // Number of People Column
        personnesCol.setCellValueFactory(new PropertyValueFactory<>("nbrPersonnes"));

        // Amount Column
        montantCol.setCellValueFactory(cellData -> {
            BigDecimal montant = cellData.getValue().getMontantTotal();
            SimpleStringProperty simpleStringProperty = new SimpleStringProperty(
                    montant != null ? montant.toPlainString() + " DT" : "N/A"
            );
            return simpleStringProperty;
        });

        // Status Column with colored badge
        statutCol.setCellValueFactory(cellData -> {
            StatutReservation statut = cellData.getValue().getStatut();
            return new javafx.beans.property.SimpleStringProperty(
                    statut != null ? statut.getLabel() : "N/A"
            );
        });

        // ✅ DESTINATION COLUMN
        destinationCol.setCellValueFactory(cellData -> {
            String titre = cellData.getValue().getTitreVoyage();
            String displayValue = (titre != null && !titre.isEmpty())
                    ? titre
                    : ("Voyage #" + cellData.getValue().getIdVoyage());
            return new javafx.beans.property.SimpleStringProperty(displayValue);
        });

        // 💳 PAYMENT STATUS COLUMN
        if (paiementStatutCol != null) {
            paiementStatutCol.setCellValueFactory(cellData -> {
                // Look up payment for this reservation
                java.util.List<Paiement> pays = paiementService.getAllPaiements();
                java.util.Optional<Paiement> paiement = pays.stream()
                        .filter(p -> p.getIdReservation() == cellData.getValue().getId())
                        .findFirst();
                String label = paiement.map(p -> p.getStatut().getLabel()).orElse("—");
                return new javafx.beans.property.SimpleStringProperty(label);
            });

            paiementStatutCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        String style = switch (item) {
                            case "Payé"       -> "-fx-text-fill: #065F46; -fx-font-weight: bold; -fx-background-color: #D1FAE5; -fx-background-radius: 6; -fx-padding: 3 10;";
                            case "En attente" -> "-fx-text-fill: #92400E; -fx-font-weight: bold; -fx-background-color: #FEF3C7; -fx-background-radius: 6; -fx-padding: 3 10;";
                            case "Échoué"     -> "-fx-text-fill: #991B1B; -fx-font-weight: bold; -fx-background-color: #FEE2E2; -fx-background-radius: 6; -fx-padding: 3 10;";
                            case "Remboursé"  -> "-fx-text-fill: #1E40AF; -fx-font-weight: bold; -fx-background-color: #DBEAFE; -fx-background-radius: 6; -fx-padding: 3 10;";
                            default           -> "-fx-text-fill: #6B7280;";
                        };
                        setStyle(style);
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            });
        }
    } // end setupTable()

    /**
     * Setup filter options
     */
    private void setupFilters() {
        // Add status options to dropdown
        statutFilter.getItems().addAll(
                "Tous",                                      // All statuses
                StatutReservation.EN_ATTENTE.getLabel(),    // Pending
                StatutReservation.CONFIRMEE.getLabel(),     // Confirmed
                StatutReservation.ANNULEE.getLabel(),       // Cancelled
                StatutReservation.TERMINEE.getLabel()       // Completed
        );
        statutFilter.setValue("Tous");

        // Search field auto-filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter();
        });
    }

    /**
     * Setup button click handlers
     */
    private void setupButtons() {
        // New reservation button
        nouveauBtn.setOnAction(event -> addNewReservation());

        // Refresh button
        refreshBtn.setOnAction(event -> {
            try {
                loadData();
                showMessage("✅ Table actualisée");
            } catch (SQLException e) {
                showError("Refresh Error", e.getMessage());
            }
        });

        // Apply filter button
        filterBtn.setOnAction(event -> applyFilter());

        // Reset filter button
        resetBtn.setOnAction(event -> resetFilters());

        // Modify button
        modifierBtn.setOnAction(event -> editReservation());

        // Delete button
        supprimerBtn.setOnAction(event -> deleteReservation());
    }

    // ════════════════════ CRUD METHODS ════════════════════

    /**
     * READ - Load all reservations from database
     */
    private void loadData() throws SQLException {
        reservationsList.clear();
        reservationsList.addAll(reservationService.getAllReservations());
        reservationsTable.setItems(reservationsList);
        updateStats();
        System.out.println("✅ Loaded " + reservationsList.size() + " reservations");
    }

    /**
     * CREATE - Add a new reservation
     */
    /**
     * CREATE - Open a new tab with the creation form
     */
    private void addNewReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateReservationForm.fxml"));
            Parent form = loader.load();
            ReservationFormController formController = loader.getController();
            formController.setOnSaveCallback(() -> {
                try {
                    loadData();
                } catch (SQLException e) {
                    showError("Refresh Error", e.getMessage());
                }
            });

            // Find the main TabPane (fx:id="tabPane" in MainLayoutWithTabs.fxml)
            TabPane mainTabPane = (TabPane) nouveauBtn.getScene().lookup("#tabPane");
            if (mainTabPane != null) {
                Tab tab = new Tab("Nouvelle réservation", form);
                tab.setClosable(true);
                mainTabPane.getTabs().add(tab);
                mainTabPane.getSelectionModel().select(tab);
            } else {
                showError("Erreur", "Impossible de trouver le conteneur d'onglets.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire.");
        }
    }
    @FXML
    private void handleExportPDF(ActionEvent event) {
        // Récupérer les réservations actuellement affichées (filtrées ou non)
        ObservableList<ReservationVoyage> reservations = reservationsTable.getItems();

        if (reservations.isEmpty()) {
            showMessage("Aucune réservation à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("reservations_agent.pdf");

        // Obtenir la fenêtre à partir de l'événement
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                PDFGenerator.generateReservationsReport(file.getAbsolutePath(), reservations);
                showMessage("✅ PDF généré avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Impossible de générer le PDF : " + e.getMessage());
            }
        }
    }

    /**
     * UPDATE - Open a new tab with the edit form pre-filled
     */
    private void editReservation() {
        ReservationVoyage selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Veuillez sélectionner une réservation.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditReservationForm.fxml"));
            Parent form = loader.load();
            ReservationFormController formController = loader.getController();
            formController.setExistingReservation(selected);
            formController.setOnSaveCallback(() -> {
                try {
                    loadData();
                } catch (SQLException e) {
                    showError("Refresh Error", e.getMessage());
                }
            });

            TabPane mainTabPane = (TabPane) modifierBtn.getScene().lookup("#tabPane");
            if (mainTabPane != null) {
                Tab tab = new Tab("Modifier réservation #" + selected.getId(), form);
                tab.setClosable(true);
                mainTabPane.getTabs().add(tab);
                mainTabPane.getSelectionModel().select(tab);
            } else {
                showError("Erreur", "Impossible de trouver le conteneur d'onglets.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire.");
        }
    }

    /**
     * DELETE - Remove a reservation
     */
    private void deleteReservation() {
        ReservationVoyage selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Please select a reservation to delete");
            return;
        }

        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Reservation?");
        confirmation.setContentText(
                "Are you sure you want to delete reservation #" + selected.getId() + "?\n" +
                        "Voyage: " + selected.getTitreVoyage() + "\n" +
                        "Amount: " + selected.getMontantTotal() + " DT"
        );

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.deleteReservation(selected.getId());
                loadData();
                showMessage("✅ Réservation supprimée!");
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        }
    }

    // ═══════════���════════ FILTER METHODS ════════════════════

    /**
     * Apply filters and search
     */
    private void applyFilter() {
        String selectedStatus = statutFilter.getValue();
        String searchText = searchField.getText().toLowerCase().trim();

        // If no filters, show all
        if ("Tous".equals(selectedStatus) && searchText.isEmpty()) {
            reservationsTable.setItems(reservationsList);
            return;
        }

        // Create filtered list
        ObservableList<ReservationVoyage> filteredList = FXCollections.observableArrayList();

        for (ReservationVoyage reservation : reservationsList) {
            boolean matches = true;

            // Check status
            if (!"Tous".equals(selectedStatus)) {
                StatutReservation statut = reservation.getStatut();
                if (statut == null || !selectedStatus.equals(statut.getLabel())) {
                    matches = false;
                }
            }

            // Check search text
            if (!searchText.isEmpty() && matches) {
                String clientId = String.valueOf(reservation.getIdUtilisateur());
                String voyage = reservation.getTitreVoyage() != null ? reservation.getTitreVoyage() : "";

                if (!clientId.toLowerCase().contains(searchText) &&
                        !voyage.toLowerCase().contains(searchText)) {
                    matches = false;
                }
            }

            if (matches) {
                filteredList.add(reservation);
            }
        }

        reservationsTable.setItems(filteredList);
    }

    /**
     * Reset all filters
     */
    private void resetFilters() {
        statutFilter.setValue("Tous");
        searchField.clear();
        try {
            loadData();
        } catch (SQLException e) {
            showError("Error", e.getMessage());
        }
    }

    // ════════════════════ HELPER METHODS ════════════════════

    /**
     * Update statistics label
     */
    private void updateStats() {
        statsLabel.setText("Total: " + reservationsList.size() + " réservations");
    }

    /**
     * Show info message
     */
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show error message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
