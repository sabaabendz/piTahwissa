package tn.esprit.tahwissa.controllers.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.tahwissa.models.Destination;
import tn.esprit.tahwissa.models.PointInteret;
import tn.esprit.tahwissa.services.DestinationService;
import tn.esprit.tahwissa.services.PointInteretService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientPointsInteretController implements Initializable {

    @FXML private FlowPane pointsGrid;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbTypeFilter;
    @FXML private ComboBox<String> cmbDestinationFilter;

    private PointInteretService pointInteretService;
    private DestinationService destinationService;
    private ObservableList<PointInteret> pointsList;
    private ObservableList<PointInteret> filteredList;
    private List<Destination> destinationsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pointInteretService = new PointInteretService();
        try {
            destinationService = new DestinationService();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        pointsList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        setupFilters();
        loadData();
    }

    private void setupFilters() {
        try {
            destinationsList = destinationService.afficherDestinations();

            cmbTypeFilter.setItems(FXCollections.observableArrayList(
                    "Tous les types", "monument", "plage", "musée", "restaurant", "parc", "hôtel", "autre"));
            cmbTypeFilter.setValue("Tous les types");

            List<String> destNames = destinationsList.stream()
                    .map(Destination::getNom)
                    .collect(Collectors.toList());
            destNames.add(0, "Toutes les destinations");
            cmbDestinationFilter.setItems(FXCollections.observableArrayList(destNames));
            cmbDestinationFilter.setValue("Toutes les destinations");

            txtSearch.textProperty().addListener((obs, old, newVal) -> filterPoints());
            cmbTypeFilter.setOnAction(e -> filterPoints());
            cmbDestinationFilter.setOnAction(e -> filterPoints());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            List<PointInteret> points = pointInteretService.afficherPointsInteret();
            pointsList.setAll(points);
            filteredList.setAll(points);
            displayPoints();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterPoints() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String selectedType = cmbTypeFilter.getValue();
        String selectedDest = cmbDestinationFilter.getValue();

        List<PointInteret> results = pointsList.stream()
                .filter(p -> {
                    boolean matchSearch = searchText.isEmpty() ||
                            p.getNom().toLowerCase().contains(searchText) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchText));

                    boolean matchType = selectedType == null ||
                            selectedType.equals("Tous les types") ||
                            p.getType().equalsIgnoreCase(selectedType);

                    boolean matchDest = selectedDest == null ||
                            selectedDest.equals("Toutes les destinations") ||
                            getDestinationName(p.getDestinationId()).equals(selectedDest);

                    return matchSearch && matchType && matchDest;
                })
                .collect(Collectors.toList());

        filteredList.setAll(results);
        displayPoints();
    }

    private String getDestinationName(int destinationId) {
        return destinationsList.stream()
                .filter(d -> d.getIdDestination() == destinationId)
                .map(Destination::getNom)
                .findFirst()
                .orElse("Inconnue");
    }

    private void displayPoints() {
        pointsGrid.getChildren().clear();

        for (PointInteret point : filteredList) {
            VBox card = createPointCard(point);
            pointsGrid.getChildren().add(card);
        }

        if (filteredList.isEmpty()) {
            Label emptyLabel = new Label("Aucun point d'intérêt trouvé");
            emptyLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14px; -fx-padding: 50px;");
            pointsGrid.getChildren().add(emptyLabel);
        }
    }

    private VBox createPointCard(PointInteret point) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; " +
                "-fx-padding: 15px; -fx-min-width: 250px; -fx-max-width: 250px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");

        String icon = switch (point.getType().toLowerCase()) {
            case "monument" -> "🏛️";
            case "plage" -> "🏖️";
            case "musée" -> "🏛️";
            case "restaurant" -> "🍽️";
            case "parc" -> "🌳";
            case "hôtel" -> "🏨";
            default -> "📍";
        };

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");

        Label nameLabel = new Label(point.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-wrap-text: true;");

        String typeColor = switch (point.getType().toLowerCase()) {
            case "monument" -> "#7C3AED";
            case "plage" -> "#2563EB";
            case "musée" -> "#9333EA";
            case "restaurant" -> "#D97706";
            case "parc" -> "#059669";
            default -> "#6B7280";
        };

        Label typeLabel = new Label(point.getType());
        typeLabel.setStyle("-fx-text-fill: " + typeColor + "; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label destLabel = new Label("📍 " + getDestinationName(point.getDestinationId()));
        destLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        Label descLabel = new Label(point.getDescription() != null ?
                (point.getDescription().length() > 100 ? point.getDescription().substring(0, 97) + "..." : point.getDescription()) :
                "Aucune description");
        descLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px; -fx-wrap-text: true;");

        card.getChildren().addAll(iconLabel, nameLabel, typeLabel, destLabel, descLabel);
        return card;
    }
}