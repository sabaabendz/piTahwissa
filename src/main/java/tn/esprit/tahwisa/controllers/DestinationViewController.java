package tn.esprit.tahwisa.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import tn.esprit.tahwisa.models.Destination;
import tn.esprit.tahwisa.services.DestinationService;
import tn.esprit.tahwisa.services.PointInteretService;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DestinationViewController implements Initializable {

    @FXML private TableView<Destination> tableDestinations;
    @FXML private TableColumn<Destination, Integer> colId;
    @FXML private TableColumn<Destination, String> colNom;
    @FXML private TableColumn<Destination, String> colPays;
    @FXML private TableColumn<Destination, String> colVille;
    @FXML private TableColumn<Destination, String> colDescription;
    @FXML private TableColumn<Destination, Void> colActions;

    @FXML private TextField txtSearch;
    @FXML private Label lblTotalDestinations;
    @FXML private Label lblTotalPoints;
    @FXML private Label lblTotalPays;
    @FXML private Label lblTableCount;

    private DestinationService destinationService;
    private PointInteretService pointInteretService;
    private ObservableList<Destination> destinationsList;
    private ObservableList<Destination> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        destinationService = new DestinationService();
        pointInteretService = new PointInteretService();
        destinationsList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        setupTable();
        loadData();
        updateStatistics();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDestination"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPays.setCellValueFactory(new PropertyValueFactory<>("pays"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox box = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("icon-button");
                btnDelete.getStyleClass().add("icon-button");

                btnEdit.setOnAction(e -> editDestination(getTableRow().getItem()));
                btnDelete.setOnAction(e -> deleteDestination(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadData() {
        try {
            List<Destination> destinations = destinationService.afficherDestinations();
            destinationsList.setAll(destinations);
            filteredList.setAll(destinations);
            tableDestinations.setItems(filteredList);
            lblTableCount.setText(destinations.size() + " destination(s)");
        } catch (Exception e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            lblTotalDestinations.setText(String.valueOf(destinationsList.size()));

            int totalPoints = pointInteretService.afficherPointsInteret().size();
            lblTotalPoints.setText(String.valueOf(totalPoints));

            Set<String> pays = destinationsList.stream()
                    .map(Destination::getPays)
                    .collect(Collectors.toSet());
            lblTotalPays.setText(String.valueOf(pays.size()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            filteredList.setAll(destinationsList);
        } else {
            List<Destination> results = destinationsList.stream()
                    .filter(d -> d.getNom().toLowerCase().contains(keyword) ||
                            d.getPays().toLowerCase().contains(keyword) ||
                            (d.getVille() != null && d.getVille().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
            filteredList.setAll(results);
        }

        lblTableCount.setText(filteredList.size() + " destination(s)");
    }

    @FXML
    private void showCreateForm() {
        loadFormView(null);
    }

    @FXML
    private void refreshTable() {
        loadData();
        updateStatistics();
        txtSearch.clear();
    }

    private void editDestination(Destination destination) {
        if (destination != null) {
            loadFormView(destination);
        }
    }

    private void deleteDestination(Destination destination) {
        if (destination == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer : " + destination.getNom());
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                destinationService.supprimerDestination(destination.getIdDestination());
                loadData();
                updateStatistics();
                showSuccess("Destination supprimée avec succès");
            } catch (Exception e) {
                showError("Erreur de suppression", e.getMessage());
            }
        }
    }

    private void loadFormView(Destination destination) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateDestinationForm.fxml"));
            Parent formView = loader.load();

            CreateDestinationController controller = loader.getController();
            controller.setParentController(this);

            if (destination != null) {
                controller.setDestination(destination);
            }

            StackPane contentArea = (StackPane) tableDestinations.getScene().getRoot().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(formView);

        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le formulaire");
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}