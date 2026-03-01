package controllers;

import entites.Transport;
import entites.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import services.TransportService;
import services.UtilisateurService;

import java.sql.SQLException;
import java.util.List;

public class TransportCatalogController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterTypeCombo;

    @FXML
    private GridPane transportGrid;

    private TransportService transportService;
    private UtilisateurService utilisateurService;
    private ObservableList<Transport> transportList;
    private ObservableList<Transport> filteredList;
    private StackPane contentArea;

    public TransportCatalogController() {
        transportService = new TransportService();
        utilisateurService = new UtilisateurService();
        transportList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @FXML
    public void initialize() {
        // Setup filter
        filterTypeCombo.getItems().addAll("Tous", "Avion", "Train", "Bus", "Bateau", "Voiture");
        filterTypeCombo.setValue("Tous");
        filterTypeCombo.setOnAction(e -> filterTransports());

        // Setup search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTransports());

        // Load transports
        loadTransports();
    }

    private void loadTransports() {
        try {
            transportList.clear();
            transportList.addAll(transportService.recupererTransport());
            filterTransports();
        } catch (SQLException e) {
            showError("Erreur lors du chargement des transports", e.getMessage());
        }
    }

    private void filterTransports() {
        filteredList.clear();
        String searchText = searchField.getText().toLowerCase();
        String typeFilter = filterTypeCombo.getValue();

        for (Transport transport : transportList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    transport.getVilleDepart().toLowerCase().contains(searchText) ||
                    transport.getVilleArrivee().toLowerCase().contains(searchText) ||
                    transport.getTypeTransport().toLowerCase().contains(searchText);

            boolean matchesType = "Tous".equals(typeFilter) ||
                    transport.getTypeTransport().equals(typeFilter);

            if (matchesSearch && matchesType) {
                filteredList.add(transport);
            }
        }

        displayTransports();
    }

    private void displayTransports() {
        transportGrid.getChildren().clear();
        
        int column = 0;
        int row = 0;
        
        for (Transport transport : filteredList) {
            VBox card = createTransportCard(transport);
            transportGrid.add(card, column, row);
            
            column++;
            if (column == 3) {  // 3 cards per row
                column = 0;
                row++;
            }
        }
    }

    private VBox createTransportCard(Transport transport) {
        VBox card = new VBox(15);
        card.getStyleClass().add("transport-card");
        card.setPrefWidth(280);
        card.setPrefHeight(320);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);

        // Type icon and badge
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label(getTransportIcon(transport.getTypeTransport()));
        icon.setStyle("-fx-font-size: 32px;");
        
        Label typeBadge = new Label(transport.getTypeTransport());
        typeBadge.getStyleClass().add("type-badge");
        typeBadge.setFont(Font.font("System Bold", 12));
        
        header.getChildren().addAll(icon, typeBadge);

        // Route
        VBox routeBox = new VBox(8);
        
        Label departLabel = new Label("📍 " + transport.getVilleDepart());
        departLabel.setFont(Font.font("System Bold", 16));
        departLabel.getStyleClass().add("route-label");
        
        Label arrow = new Label("↓");
        arrow.setStyle("-fx-font-size: 20px; -fx-text-fill: #666;");
        
        Label arriveeLabel = new Label("📍 " + transport.getVilleArrivee());
        arriveeLabel.setFont(Font.font("System Bold", 16));
        arriveeLabel.getStyleClass().add("route-label");
        
        routeBox.getChildren().addAll(departLabel, arrow, arriveeLabel);

        // Details
        VBox detailsBox = new VBox(5);
        
        Label dateLabel = new Label("📅 " + transport.getDateDepart());
        dateLabel.setFont(Font.font(12));
        
        Label timeLabel = new Label("🕐 " + transport.getHeureDepart());
        timeLabel.setFont(Font.font(12));
        
        Label durationLabel = new Label("⏱️ " + transport.getDuree() + " heures");
        durationLabel.setFont(Font.font(12));
        
        Label placesLabel = new Label("💺 " + transport.getNbPlaces() + " places disponibles");
        placesLabel.setFont(Font.font(12));
        
        detailsBox.getChildren().addAll(dateLabel, timeLabel, durationLabel, placesLabel);

        // Price and button
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));
        
        Label priceLabel = new Label(String.format("%.2f €", transport.getPrix()));
        priceLabel.setFont(Font.font("System Bold", 20));
        priceLabel.setStyle("-fx-text-fill: #2196F3;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button reserveBtn = new Button("Réserver");
        reserveBtn.getStyleClass().add("reserve-button");
        reserveBtn.setFont(Font.font("System Bold", 14));
        reserveBtn.setOnAction(e -> openReservationDialog(transport));
        
        footer.getChildren().addAll(priceLabel, spacer, reserveBtn);

        card.getChildren().addAll(header, new Separator(), routeBox, detailsBox, footer);

        return card;
    }

    private String getTransportIcon(String type) {
        switch (type) {
            case "Avion": return "✈️";
            case "Train": return "🚆";
            case "Bus": return "🚌";
            case "Bateau": return "🚢";
            case "Voiture": return "🚗";
            default: return "🚗";
        }
    }

    private void openReservationDialog(Transport transport) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Réserver un Transport");
        dialog.setHeaderText("Réservation: " + transport.getVilleDepart() + " → " + transport.getVilleArrivee());

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label userLabel = new Label("Utilisateur:");
        ComboBox<String> userCombo = new ComboBox<>();
        
        Label placesLabel = new Label("Nombre de places:");
        Spinner<Integer> placesSpinner = new Spinner<>(1, transport.getNbPlaces(), 1);
        placesSpinner.setEditable(true);

        Label statutLabel = new Label("Statut:");
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("En attente", "Confirmée");
        statutCombo.setValue("En attente");

        // Load users
        try {
            List<Utilisateur> users = utilisateurService.recupererUtilisateurs();
            for (Utilisateur u : users) {
                userCombo.getItems().add(u.getIdUser() + " - " + u.getNom());
            }
            if (!userCombo.getItems().isEmpty()) {
                userCombo.setValue(userCombo.getItems().get(0));
            }
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les utilisateurs");
        }

        grid.add(userLabel, 0, 0);
        grid.add(userCombo, 1, 0);
        grid.add(placesLabel, 0, 1);
        grid.add(placesSpinner, 1, 1);
        grid.add(statutLabel, 0, 2);
        grid.add(statutCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Here you would save the reservation
                showSuccess("Réservation créée avec succès!");
            }
        });
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
