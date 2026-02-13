package tn.esprit.tahwissa.controllers.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.tahwissa.entities.Voyage;
import tn.esprit.tahwissa.services.VoyageService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientVoyagesController implements Initializable {

    private VoyageService voyageService;
    private List<Voyage> allVoyages;
    private int currentUserId = 1; // TODO: Get from logged-in user session

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> priceFilter;
    @FXML private VBox voyagesContainer;
    @FXML private Label statsLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ Client Voyages Interface initialized");

        try {
            voyageService = new VoyageService();
            loadVoyages();
            setupFilters();
        } catch (Exception e) {
            showError("Initialization Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadVoyages() throws SQLException {
        allVoyages = voyageService.recupererTous();
        displayVoyages(allVoyages);
        updateStats();
    }

    private void setupFilters() {
        categoryFilter.getItems().addAll("All", "Beach", "Mountain", "City", "Adventure");
        categoryFilter.setValue("All");

        priceFilter.getItems().addAll("All", "Under 500 DT", "500-1000 DT", "1000-2000 DT", "2000+ DT");
        priceFilter.setValue("All");
    }

    private void displayVoyages(List<Voyage> voyages) {
        voyagesContainer.getChildren().clear();

        for (Voyage voyage : voyages) {
            VBox voyageCard = createVoyageCard(voyage);
            voyagesContainer.getChildren().add(voyageCard);
        }
    }

    private VBox createVoyageCard(Voyage voyage) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #d1fae5; -fx-border-radius: 8; -fx-padding: 15; " +
            "-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Header
        HBox header = new HBox(10);
        Label title = new Label(voyage.getTitre());
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #0f766e;");

        Label badge = new Label(voyage.getCategorie() != null ? voyage.getCategorie() : "Travel");
        badge.setStyle("-fx-background-color: #14b8a6; -fx-text-fill: white; -fx-padding: 4 10; " +
            "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 11;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label price = new Label(voyage.getPrixUnitaire() + " DT/person");
        price.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #059669;");

        header.getChildren().addAll(title, badge, spacer, price);

        // Description
        Label description = new Label(voyage.getDestination());
        description.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");
        description.setWrapText(true);

        // Dates
        HBox dates = new HBox(20);
        Label departure = new Label("📅 Depart: " + voyage.getDateDepart());
        departure.setStyle("-fx-font-size: 10; -fx-text-fill: #4b5563;");
        Label returnDate = new Label("📅 Return: " + voyage.getDateRetour());
        returnDate.setStyle("-fx-font-size: 10; -fx-text-fill: #4b5563;");
        Label availability = new Label("🪑 " + voyage.getPlacesDisponibles() + " seats");
        availability.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: #0f766e;");
        dates.getChildren().addAll(departure, returnDate, availability);

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button detailsBtn = new Button("👁️ View Details");
        detailsBtn.setStyle("-fx-background-color: #e0f2f1; -fx-text-fill: #0f766e; -fx-padding: 8 16; " +
            "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-weight: bold;");
        detailsBtn.setOnAction(e -> showVoyageDetails(voyage));

        Button bookBtn = new Button("✅ Book Now");
        bookBtn.setStyle("-fx-background-color: #14b8a6; -fx-text-fill: white; -fx-padding: 8 16; " +
            "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-weight: bold;");
        bookBtn.setOnAction(e -> bookVoyage(voyage));

        if (voyage.getPlacesDisponibles() <= 0) {
            bookBtn.setDisable(true);
            bookBtn.setStyle("-fx-background-color: #d1d5db; -fx-text-fill: white; -fx-padding: 8 16; " +
                "-fx-border-radius: 4; -fx-background-radius: 4;");
        }

        actions.getChildren().addAll(detailsBtn, bookBtn);

        card.getChildren().addAll(header, description, dates, actions);
        return card;
    }

    private void showVoyageDetails(Voyage voyage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Voyage Details - " + voyage.getTitre());
        alert.setHeaderText(null);
        alert.setContentText(
            "Destination: " + voyage.getDestination() + "\n" +
                "Category: " + voyage.getCategorie() + "\n" +
                "Price: " + voyage.getPrixUnitaire() + " DT/person\n" +
                "Departure: " + voyage.getDateDepart() + "\n" +
                "Return: " + voyage.getDateRetour() + "\n" +
                "Available Seats: " + voyage.getPlacesDisponibles() + "\n\n" +
                "Description: " + (voyage.getDescription() != null ? voyage.getDescription() : "No description")
        );
        alert.showAndWait();
    }

    private void bookVoyage(Voyage voyage) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Book Voyage - " + voyage.getTitre());
        dialog.setHeaderText("How many people do you want to book?");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label pricePerPersonLabel = new Label("Price per person: " + voyage.getPrixUnitaire() + " DT");
        pricePerPersonLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f766e;");

        Spinner<Integer> peopleSpinner = new Spinner<>(1, voyage.getPlacesDisponibles(), 1);
        peopleSpinner.setPrefWidth(100);
        peopleSpinner.setStyle("-fx-padding: 8; -fx-font-size: 14;");

        Label totalLabel = new Label("Total: " + voyage.getPrixUnitaire() + " DT");
        totalLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #059669;");

        // Update total when spinner changes
        peopleSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            double total = voyage.getPrixUnitaire() * newVal;
            totalLabel.setText(String.format("Total: %.2f DT", total));
        });

        grid.add(new Label("Number of People:"), 0, 0);
        grid.add(peopleSpinner, 1, 0);
        grid.add(pricePerPersonLabel, 0, 1);
        grid.add(totalLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return peopleSpinner.getValue();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(numPeople -> {
            try {
                // Here you would save the reservation
                // For now just show confirmation
                double totalPrice = voyage.getPrixUnitaire() * numPeople;

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Booking Confirmation");
                confirmation.setHeaderText("Confirm Your Reservation");
                confirmation.setContentText(
                    "Voyage: " + voyage.getTitre() + "\n" +
                        "Number of People: " + numPeople + "\n" +
                        "Total Price: " + String.format("%.2f DT", totalPrice) + "\n\n" +
                        "Proceed with booking?"
                );

                Optional<ButtonType> confirm = confirmation.showAndWait();
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    // TODO: Save reservation using ReservationVoyageService
                    showMessage("✅ Reservation submitted! An agent will confirm it soon.");
                    loadVoyages(); // Refresh
                }
            } catch (SQLException e) {
                showError("Booking Error", e.getMessage());
            }
        });
    }

    private void updateStats() {
        statsLabel.setText("Total Voyages: " + allVoyages.size());
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
