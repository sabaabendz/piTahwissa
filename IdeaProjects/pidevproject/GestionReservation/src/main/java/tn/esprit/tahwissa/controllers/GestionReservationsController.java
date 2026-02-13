package tn.esprit.tahwissa.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.tahwissa.entities.ReservationVoyage;
import tn.esprit.tahwissa.entities.Voyage;
import tn.esprit.tahwissa.services.ReservationVoyageService;
import tn.esprit.tahwissa.services.VoyageService;




import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * GESTION RESERVATIONS CONTROLLER
 * ...rest of code

import java.net.URL;
import java.sql.SQLException;
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
    private List<Voyage> voyagesList; // Cache voyages for dropdown

    // ════════════════════ TABLE COLUMNS ════════════════════
    @FXML private TableColumn<ReservationVoyage, Integer> idCol;
    @FXML private TableColumn<ReservationVoyage, String> clientCol;
    @FXML private TableColumn<ReservationVoyage, String> destCol;  // ← Shows voyage title (NOT dates!)
    @FXML private TableColumn<ReservationVoyage, Integer> personnesCol;
    @FXML private TableColumn<ReservationVoyage, Double> montantCol;
    @FXML private TableColumn<ReservationVoyage, String> statutCol;

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
        System.out.println("✅ GestionReservationsController.initialize() appele");

        try {
            // Create services
            reservationService = new ReservationVoyageService();
            voyageService = new VoyageService();

            // Load voyages for later use
            voyagesList = voyageService.recupererTous();
            System.out.println("✅ Loaded " + voyagesList.size() + " voyages");

            // Setup UI
            setupTable();
            setupFilters();
            setupButtons();
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Initialization Error", e.getMessage());

        }
    }

    // ════════════════════ SETUP METHODS ════════════════════

    /**
     * Configure table columns to link with entity fields
     *
     * What happens:
     * - idCol shows the reservation ID
     * - clientCol shows the client/user ID
     * - destCol shows the VOYAGE TITLE (not destination!)
     * - personnesCol shows number of people
     * - montantCol shows the total amount
     * - statutCol shows the status
     */
    private void setupTable() {
        // Simple columns - direct mapping to entity fields
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                "Client #" + cellData.getValue().getIdUtilisateur()
            )
        );
        personnesCol.setCellValueFactory(new PropertyValueFactory<>("nbrPersonnes"));
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Complex column - show voyage title (from JOIN in service)
        // This is the KEY CHANGE from before!
        destCol.setCellValueFactory(cellData -> {
            String titre = cellData.getValue().getVoyageTitre();

            // If we have the title, show it. Otherwise show "Voyage #X"
            String displayValue = (titre != null) ? titre : ("Voyage #" + cellData.getValue().getIdVoyage());

            return new javafx.beans.property.SimpleStringProperty(displayValue);
        });
    }

    /**
     * Setup filter options
     */
    private void setupFilters() {
        // Add status options to dropdown
        statutFilter.getItems().addAll(
            "Tous",           // All statuses
            "EN_ATTENTE",     // Pending confirmation
            "CONFIRMEE",      // Approved by agent
            "ANNULEA",        // Rejected by agent
            "TERMINEE"        // Voyage completed
        );
        statutFilter.setValue("Tous");
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
                showMessage("Table actualisée");
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
     * READ - Load all reservations from database and display in table
     */
    private void loadData() throws SQLException {
        System.out.println(" loadData() - debut");
        // Clear current data
        reservationsList.clear();

        // Load from service
        reservationsList.addAll(reservationService.recupererTous());
        System.out.println("✅ " + reservationsList.size() + " réservations récupérées");
        // Set in table
        reservationsTable.setItems(reservationsList);

        // Update stats
        updateStats();


    }

    /**
     * CREATE - Add a new reservation
     *
     * What happens:
     * 1. Show dialog asking for: User ID, Voyage ID, Number of People
     * 2. Look up the voyage to get its price
     * 3. Calculate: total = price × people
     * 4. Create ReservationVoyage object
     * 5. Save to database
     * 6. Refresh table
     */
    private void addNewReservation() {
        // Dialog 1: Ask for user ID
        TextInputDialog dialogUserId = new TextInputDialog();
        dialogUserId.setTitle("Nouvelle Réservation");
        dialogUserId.setHeaderText("Step 1 of 3: Client ID");
        dialogUserId.setContentText("Enter client/user ID:");

        Optional<String> userIdResult = dialogUserId.showAndWait();

        userIdResult.ifPresent(userId -> {
            try {
                int parsedUserId = Integer.parseInt(userId);

                // Dialog 2: Ask for voyage ID
                TextInputDialog dialogVoyageId = new TextInputDialog();
                dialogVoyageId.setTitle("Nouvelle Réservation");
                dialogVoyageId.setHeaderText("Step 2 of 3: Voyage Selection");
                dialogVoyageId.setContentText("Enter voyage ID:");

                Optional<String> voyageIdResult = dialogVoyageId.showAndWait();

                voyageIdResult.ifPresent(voyageId -> {
                    try {
                        int parsedVoyageId = Integer.parseInt(voyageId);

                        // Dialog 3: Ask for number of people
                        TextInputDialog dialogNbPersonnes = new TextInputDialog("1");
                        dialogNbPersonnes.setTitle("Nouvelle Réservation");
                        dialogNbPersonnes.setHeaderText("Step 3 of 3: Number of People");
                        dialogNbPersonnes.setContentText("How many people?");

                        Optional<String> nbResult = dialogNbPersonnes.showAndWait();

                        nbResult.ifPresent(nb -> {
                            try {
                                int parsedNb = Integer.parseInt(nb);

                                // ════════════════════ FIND VOYAGE ════════════════════
                                // Search through voyages to find the one with matching ID
                                Voyage selectedVoyage = voyagesList.stream()
                                    .filter(v -> v.getId() == parsedVoyageId)
                                    .findFirst()
                                    .orElse(null);

                                // If voyage not found, show error
                                if (selectedVoyage == null) {
                                    showError("Error", "Voyage #" + parsedVoyageId + " not found!");
                                    return;
                                }

                                // ════════════════════ CALCULATE AMOUNT ════════════════════
                                double prix = selectedVoyage.getPrixUnitaire();
                                double montant = prix * parsedNb;

                                System.out.println("Creating reservation:");
                                System.out.println("  User: " + parsedUserId);
                                System.out.println("  Voyage: " + selectedVoyage.getTitre() + " (ID: " + parsedVoyageId + ")");
                                System.out.println("  People: " + parsedNb);
                                System.out.println("  Price/person: " + prix + " DT");
                                System.out.println("  Total: " + montant + " DT");

                                // ════════════════════ CREATE OBJECT ════════════════════
                                ReservationVoyage newReservation = new ReservationVoyage(
                                    parsedUserId,
                                    parsedVoyageId,
                                    parsedNb,
                                    montant
                                );

                                // ════════════════════ SAVE TO DATABASE ════════════════════
                                reservationService.ajouter(newReservation);

                                // ════════════════════ REFRESH UI ════════════════════
                                loadData();
                                showMessage("✅ Réservation ajoutée avec succès!");

                            } catch (NumberFormatException e) {
                                showError("Error", "Number of people must be a number");
                            } catch (SQLException e) {
                                showError("Database Error", e.getMessage());
                            }
                        });

                    } catch (NumberFormatException e) {
                        showError("Error", "Voyage ID must be a number");
                    }
                });

            } catch (NumberFormatException e) {
                showError("Error", "User ID must be a number");
            }
        });
    }

    /**
     * UPDATE - Modify an existing reservation
     */
    private void editReservation() {
        // Get selected reservation from table
        ReservationVoyage selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Please select a reservation to modify");
            return;
        }

        // Create dialog
        Dialog<ReservationVoyage> dialog = new Dialog<>();
        dialog.setTitle("Modifier Réservation");
        dialog.setHeaderText("Edit Reservation #" + selected.getId());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // User ID field
        TextField userIdField = new TextField(String.valueOf(selected.getIdUtilisateur()));

        // Voyage ID field (or dropdown)
        TextField voyageIdField = new TextField(String.valueOf(selected.getIdVoyage()));

        // Number of people
        Spinner<Integer> personnesSpinner = new Spinner<>(1, 100, selected.getNbrPersonnes());
        personnesSpinner.setPrefWidth(100);

        // Status dropdown
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("EN_ATTENTE", "CONFIRMEE", "ANNULEA", "TERMINEE");
        statutCombo.setValue(selected.getStatut());

        // Add to grid
        grid.add(new Label("User ID:"), 0, 0);
        grid.add(userIdField, 1, 0);
        grid.add(new Label("Voyage ID:"), 0, 1);
        grid.add(voyageIdField, 1, 1);
        grid.add(new Label("Number of People:"), 0, 2);
        grid.add(personnesSpinner, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statutCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    // Get values from fields
                    selected.setIdUtilisateur(Integer.parseInt(userIdField.getText()));
                    selected.setIdVoyage(Integer.parseInt(voyageIdField.getText()));
                    selected.setNbrPersonnes(personnesSpinner.getValue());
                    selected.setStatut(statutCombo.getValue());

                    // Recalculate amount based on new voyage and people count
                    Voyage updatedVoyage = voyagesList.stream()
                        .filter(v -> v.getId() == selected.getIdVoyage())
                        .findFirst()
                        .orElse(null);

                    if (updatedVoyage != null) {
                        selected.setMontantTotal(updatedVoyage.getPrixUnitaire() * selected.getNbrPersonnes());
                    }

                    return selected;
                } catch (NumberFormatException e) {
                    showError("Error", "Please enter valid numbers");
                    return null;
                }
            }
            return null;
        });

        Optional<ReservationVoyage> result = dialog.showAndWait();
        result.ifPresent(reservation -> {
            try {
                reservationService.modifier(reservation);
                loadData();
                showMessage("✅ Réservation modifiée!");
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        });
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
                "Voyage: " + selected.getVoyageTitre() + "\n" +
                "Amount: " + selected.getMontantTotal() + " DT"
        );

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.supprimer(selected.getId());
                loadData();
                showMessage("✅ Réservation supprimée!");
            } catch (SQLException e) {
                showError("Database Error", e.getMessage());
            }
        }
    }

    // ════════════════════ FILTER METHODS ════════════════════

    /**
     * Apply filters and search
     *
     * Logic:
     * 1. Get selected status filter
     * 2. Get search text
     * 3. Check each reservation:
     *    - Does it match the status? (or is status "Tous"?)
     *    - Does it match the search text?
     * 4. Show only matching reservations
     */
    private void applyFilter() {
        String selectedStatus = statutFilter.getValue();
        String searchText = searchField.getText().toLowerCase().trim();

        // If no filters selected, show all
        if ("Tous".equals(selectedStatus) && searchText.isEmpty()) {
            reservationsTable.setItems(reservationsList);
            return;
        }

        // Create filtered list
        ObservableList<ReservationVoyage> filteredList = FXCollections.observableArrayList();

        for (ReservationVoyage reservation : reservationsList) {
            boolean matches = true;

            // ════════════════════ CHECK STATUS ════════════════════
            // If status filter is NOT "Tous" AND doesn't match, skip this reservation
            if (!"Tous".equals(selectedStatus) && !selectedStatus.equals(reservation.getStatut())) {
                matches = false;
            }

            // ════════════════════ CHECK SEARCH TEXT ════════════════════
            // If search text is entered, check if it appears in:
            // - Client ID (as text)
            // - Voyage title
            if (!searchText.isEmpty() && matches) {
                String clientId = String.valueOf(reservation.getIdUtilisateur());
                String voyage = reservation.getVoyageTitre() != null ? reservation.getVoyageTitre() : "";

                // If search text doesn't match client or voyage, skip
                if (!clientId.toLowerCase().contains(searchText) &&
                    !voyage.toLowerCase().contains(searchText)) {
                    matches = false;
                }
            }

            // If passes all checks, add to filtered list
            if (matches) {
                filteredList.add(reservation);
            }
        }

        // Show filtered results
        reservationsTable.setItems(filteredList);
    }

    /**
     * Reset all filters to default
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
        statsLabel.setText("Total: " + reservationsList.size() + " reservations");
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
