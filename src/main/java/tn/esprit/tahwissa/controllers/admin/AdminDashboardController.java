package tn.esprit.tahwissa.controllers.admin;

import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;

import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.beans.property.SimpleStringProperty;
import tn.esprit.tahwissa.models.Paiement;
import tn.esprit.tahwissa.models.ReservationVoyage;
import tn.esprit.tahwissa.models.Voyage;
import tn.esprit.tahwissa.services.PaiementService;
import tn.esprit.tahwissa.services.ReservationVoyageService;
import tn.esprit.tahwissa.services.VoyageService;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import tn.esprit.tahwissa.utils.PDFGenerator;


import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.io.IOException;

public class AdminDashboardController implements Initializable {

    // Voyage table
    @FXML private TableView<Voyage> voyagesTable;
    @FXML private TableColumn<Voyage, Integer> voyageIdCol;
    @FXML private TableColumn<Voyage, String> titreCol, destinationCol, categorieCol, statutCol;
    @FXML private TableColumn<Voyage, BigDecimal> prixCol;
    @FXML private TableColumn<Voyage, LocalDate> departCol, retourCol;
    @FXML private TableColumn<Voyage, Integer> placesCol;
    @FXML private Button addBtn, editBtn, deleteBtn, refreshBtn;

    // Recent reservations table
    @FXML private TableView<ReservationVoyage> recentReservationsTable;
    @FXML private TableColumn<ReservationVoyage, Integer> reservationIdCol;
    @FXML private TableColumn<ReservationVoyage, String> clientCol, destCol, dateCol, statusCol;
    @FXML
    private AnchorPane eventContentContainer;
    // Filters and stats
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private Label totalReservationsLabel, totalRevenueLabel, avgRevenueLabel, activeVoyagesLabel;
    @FXML private PieChart statusPieChart;
    @FXML private LineChart<String, Number> timeLineChart;
    @FXML private BarChart<String, Number> destinationsBarChart;
    @FXML private Button filterBtn, resetBtn;

    // Payment labels (optional — only wired if present in FXML)
    @FXML private Label totalPaidLabel;
    @FXML private Label pendingPaymentsLabel;
    @FXML private Label totalPaymentsCountLabel;

    private ReservationVoyageService reservationService;
    private VoyageService voyageService;
    private PaiementService paiementService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            reservationService = new ReservationVoyageService();
            voyageService = new VoyageService();
            paiementService = new PaiementService();
            paiementService.ensureTableExists();

            startDatePicker.setValue(LocalDate.now().minusDays(30));
            endDatePicker.setValue(LocalDate.now());

            setupVoyageTable();
            setupReservationTable();
            refreshStatistics();
            loadVoyages();

        } catch (SQLException e) {
            showError("Database error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupVoyageTable() {
        voyageIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        departCol.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        retourCol.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        placesCol.setCellValueFactory(new PropertyValueFactory<>("placesDisponibles"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void setupReservationTable() {
        reservationIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientCol.setCellValueFactory(cellData ->
                new SimpleStringProperty("Client #" + cellData.getValue().getIdUtilisateur()));
        destCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitreVoyage()));
        dateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateReservation().toLocalDate().toString()));
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatut().getLabel()));
    }

    private void loadVoyages() throws SQLException {
        voyagesTable.setItems(FXCollections.observableArrayList(voyageService.getAllVoyages()));
    }

    @FXML
    private void handleAddVoyage() {
        openVoyageForm(null);
    }

    @FXML
    private void handleEditVoyage() {
        Voyage selected = voyagesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openVoyageForm(selected);
        } else {
            showAlert("Sélection", "Veuillez sélectionner un voyage.");
        }
    }

    @FXML
    private void handleDeleteVoyage() {
        Voyage selected = voyagesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner un voyage.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer voyage");
        confirm.setContentText("Voulez-vous vraiment supprimer " + selected.getTitre() + " ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                voyageService.deleteVoyage(selected.getId());
                loadVoyages();
            } catch (SQLException e) {
                showError("Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefreshVoyages() {
        try {
            loadVoyages();
        } catch (SQLException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            tn.esprit.tahwissa.utils.SceneNavigator.navigate(((javafx.scene.Node) event.getSource()), "/fxml/MainLayoutWithTabs.fxml", "Tahwissa - Tableau de Bord");
        } catch (Exception e) {
            showError("Erreur de Navigation", "Impossible de retourner au tableau de bord: " + e.getMessage());
        }
    }
    private void openVoyageForm(Voyage voyage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/VoyageForm.fxml"));
            Parent form = loader.load();
            VoyageFormController controller = loader.getController();
            controller.setVoyage(voyage);
            controller.setOnSaveCallback(() -> {
                try {
                    loadVoyages();
                } catch (SQLException e) {
                    showError("Erreur", e.getMessage());
                }
            });

            TabPane mainTabPane = (TabPane) addBtn.getScene().lookup("#tabPane");
            if (mainTabPane != null) {
                String title = (voyage == null) ? "Nouveau voyage" : "Modifier voyage #" + voyage.getId();
                Tab tab = new Tab(title, form);
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
    private void refreshRecentReservations() {
        try {
            loadRecentReservations();
        } catch (SQLException e) {
            showError("Erreur", e.getMessage());
        }
    }
    @FXML
    private void handleExportPDF() {
        // Récupérer toutes les réservations (admin)
        List<ReservationVoyage> reservations = reservationService.getAllReservations();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("rapport_reservations.pdf");
        File file = fileChooser.showSaveDialog(addBtn.getScene().getWindow()); // Utilise addBtn pour la fenêtre

        if (file != null) {
            try {
                PDFGenerator.generateReservationsReport(file.getAbsolutePath(), reservations);
                showSuccess("PDF généré", "Le rapport a été enregistré avec succès.");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Impossible de générer le PDF : " + e.getMessage());
            }
        }
    }

    // Ajout de la méthode showSuccess
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleFilter() {
        refreshStatistics();
    }

    private void refreshStatistics() {
        try {
            int total = reservationService.getTotalReservations();
            BigDecimal revenue = reservationService.getTotalRevenue();
            int avg = total == 0 ? 0 : revenue.divide(BigDecimal.valueOf(total), BigDecimal.ROUND_HALF_UP).intValue();
            int activeVoyages = voyageService.getAllVoyages().size();

            totalReservationsLabel.setText(String.valueOf(total));
            totalRevenueLabel.setText(revenue + " DT");
            avgRevenueLabel.setText(avg + " DT");
            activeVoyagesLabel.setText(String.valueOf(activeVoyages));

            // Payment statistics (admin overview)
            java.util.List<Paiement> allPaiements = paiementService.getAllPaiements();
            BigDecimal totalPaid = allPaiements.stream()
                    .filter(p -> p.getStatut() == Paiement.StatutPaiement.PAYE)
                    .map(Paiement::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalPending = allPaiements.stream()
                    .filter(p -> p.getStatut() == Paiement.StatutPaiement.EN_ATTENTE)
                    .map(Paiement::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long paidCount = allPaiements.stream()
                    .filter(p -> p.getStatut() == Paiement.StatutPaiement.PAYE).count();

            if (totalPaidLabel != null)          totalPaidLabel.setText(totalPaid + " DT");
            if (pendingPaymentsLabel != null)     pendingPaymentsLabel.setText(totalPending + " DT");
            if (totalPaymentsCountLabel != null)  totalPaymentsCountLabel.setText(String.valueOf(paidCount));

            loadStatusPieChart();
            loadDestinationsBarChart();
            loadRecentReservations();

        } catch (SQLException e) {
            showError("Failed to load statistics", e.getMessage());
        }
    }

    private void loadStatusPieChart() throws SQLException {
        Map<String, Integer> statusCounts = reservationService.getReservationsCountByStatus();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        statusCounts.forEach((status, count) ->
                pieData.add(new PieChart.Data(ReservationVoyage.StatutReservation.fromCode(status).getLabel(), count)));
        statusPieChart.setData(pieData);
    }

    private void loadDestinationsBarChart() throws SQLException {
        List<Object[]> topDestinations = reservationService.getTopDestinations(5);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations");
        for (Object[] row : topDestinations) {
            series.getData().add(new XYChart.Data<>(row[0].toString(), (Number) row[1]));
        }
        destinationsBarChart.getData().clear();
        destinationsBarChart.getData().add(series);
    }

    private void loadRecentReservations() throws SQLException {
        List<ReservationVoyage> recent = reservationService.getAllReservations();
        recentReservationsTable.setItems(FXCollections.observableArrayList(
                recent.stream().limit(10).toList()
        ));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void loadEventView(String path) {
        try {
            URL url = getClass().getResource(path);

            if (url == null) {
                System.err.println("❌ FXML introuvable : " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            eventContentContainer.getChildren().setAll(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showEventList() {
        loadEventView("/fxml/event/EventList.fxml");
    }

    @FXML
    private void showReservationList() {
        loadEventView("/fxml/event/ReservationList.fxml");
    }

    @FXML
    private void showReclamationList() {
        loadEventView("/fxml/event/ReclamationList.fxml");
    }

    @FXML
    private void showCreateEvent() {
        loadEventView("/fxml/event/EventForm.fxml");
    }

    @FXML
    private void showCarte() {
        loadEventView("/fxml/event/EventMap.fxml");
    }

}
