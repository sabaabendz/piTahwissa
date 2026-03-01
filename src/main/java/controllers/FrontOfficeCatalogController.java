package controllers;

import entites.Reservation_transport;
import entites.Transport;
import entites.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import services.Reservation_transportService;
import services.TransportService;
import services.UtilisateurService;
import utils.ImageService;
import utils.CurrencyService;
import utils.WeatherService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FrontOfficeCatalogController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterTypeCombo;

    @FXML
    private GridPane transportGrid;

    @FXML
    private Spinner<Integer> nbPlacesSpinner;

    @FXML
    private Label transportInfoLabel;

    private TransportService transportService;
    private Reservation_transportService reservationService;
    private UtilisateurService utilisateurService;
    private ObservableList<Transport> transportList;
    private ObservableList<Transport> filteredList;
    private StackPane contentArea;
    private Transport currentTransport;

    public FrontOfficeCatalogController() {
        transportService = new TransportService();
        reservationService = new Reservation_transportService();
        utilisateurService = new UtilisateurService();
        transportList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @FXML
    public void initialize() {
        // Initialize catalog view
        if (filterTypeCombo != null) {
            filterTypeCombo.getItems().addAll("Tous", "Avion", "Train", "Bus", "Bateau", "Voiture");
            filterTypeCombo.setValue("Tous");
            filterTypeCombo.setOnAction(e -> filterTransports());

            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTransports());
            loadTransports();
        }

        // Initialize reservation form
        if (nbPlacesSpinner != null) {
            nbPlacesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        }
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
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createTransportCard(Transport transport) {
        VBox card = new VBox(0);
        card.setPrefWidth(340);
        card.setMaxWidth(340);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 3);");

        // === IMAGE HEADER ===
        StackPane imageHeader = new StackPane();
        imageHeader.setPrefHeight(200);
        imageHeader.setStyle("-fx-background-color: #667EEA;");
        
        // Try to load real image based on arrival city
        ImageView imageView = new ImageView();
        imageView.setFitWidth(340);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        
        // Load image asynchronously for destination city
        ImageService.loadImageAsync(transport.getVilleArrivee(), image -> {
            if (image != null) {
                imageView.setImage(image);
            } else {
                // Fallback to icon if image fails
                Label iconLabel = new Label(getTransportIcon(transport.getTypeTransport()));
                iconLabel.setStyle("-fx-font-size: 80px; -fx-text-fill: white;");
                imageHeader.getChildren().add(0, iconLabel);
            }
        });
        
        // Type badge
        Label badge = new Label(transport.getTypeTransport());
        badge.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; " +
                      "-fx-padding: 8 16; -fx-background-radius: 20; -fx-font-size: 12px; -fx-font-weight: bold;");
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(15));
        
        imageHeader.getChildren().addAll(imageView, badge);

        // === CONTENT SECTION ===
        VBox contentBox = new VBox(18);
        contentBox.setPadding(new Insets(25));
        contentBox.setStyle("-fx-background-color: white;");
        
        // Route
        HBox route = new HBox(15);
        route.setAlignment(Pos.CENTER_LEFT);
        
        Label from = new Label(transport.getVilleDepart());
        from.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        Label arrowIcon = new Label("→");
        arrowIcon.setStyle("-fx-font-size: 22px; -fx-text-fill: #95A5A6;");
        
        Label to = new Label(transport.getVilleArrivee());
        to.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        route.getChildren().addAll(from, arrowIcon, to);
        
        // Details Grid
        GridPane details = new GridPane();
        details.setHgap(25);
        details.setVgap(12);
        
        // Date
        Label dateText = new Label("📅  " + transport.getDateDepart());
        dateText.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        details.add(dateText, 0, 0);
        
        // Time
        Label timeText = new Label("🕐  " + transport.getHeureDepart());
        timeText.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        details.add(timeText, 1, 0);
        
        // Duration
        Label durationText = new Label("⏱  " + transport.getDuree() + " heures");
        durationText.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        details.add(durationText, 0, 1);
        
        // Calculate places
        int availablePlaces = transport.getNbPlaces();
        try {
            int confirmed = reservationService.getTotalPlacesConfirmees(transport.getIdTransport());
            availablePlaces = transport.getNbPlaces() - confirmed;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Places
        String placesColor = availablePlaces > 0 ? "#27AE60" : "#E74C3C";
        Label placesText = new Label("👥  " + availablePlaces + " places");
        placesText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + placesColor + ";");
        details.add(placesText, 0, 2);
        
        // Weather
        Label weatherText = new Label("🌡️  Loading...");
        weatherText.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        details.add(weatherText, 1, 1);
        
        // Load weather async
        WeatherService.getWeatherForCity(transport.getVilleArrivee(), new WeatherService.WeatherCallback() {
            @Override
            public void onSuccess(String weatherData) {
                javafx.application.Platform.runLater(() -> {
                    weatherText.setText(weatherData);
                });
            }
            
            @Override
            public void onError(String error) {
                javafx.application.Platform.runLater(() -> {
                    weatherText.setText("🌡️  N/A");
                });
            }
        });
        
        // Separator
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #EEEEEE;");
        
        // Footer - Price and Button
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        
        Label price = new Label(CurrencyService.formatPrice(transport.getPrix()));
        price.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        price.setMinWidth(180);
        price.setMaxWidth(250);
        price.setWrapText(false);
        
        Region grow = new Region();
        HBox.setHgrow(grow, Priority.ALWAYS);
        
        Button btn = new Button(availablePlaces > 0 ? "Réserver" : "Complet");
        btn.setPrefWidth(130);
        btn.setPrefHeight(45);
        
        if (availablePlaces > 0) {
            btn.setStyle("-fx-background-color: #667EEA; -fx-text-fill: white; " +
                        "-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: #CCCCCC; -fx-text-fill: #888888; " +
                        "-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10;");
        }
        
        final int places = availablePlaces;
        btn.setOnAction(e -> {
            if (places <= 0) {
                showError("Complet", "Désolé, il n'y a plus de places disponibles.");
            } else {
                openReservationForm(transport);
            }
        });
        
        footer.getChildren().addAll(price, grow, btn);
        
        contentBox.getChildren().addAll(route, details, sep, footer);
        card.getChildren().addAll(imageHeader, contentBox);

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

    private void openReservationForm(Transport transport) {
        currentTransport = transport;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontoffice_reservation_form.fxml"));
            Parent root = loader.load();

            FrontOfficeCatalogController controller = loader.getController();
            controller.setContentArea(contentArea);
            controller.setTransportForReservation(transport);

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            }
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire de réservation");
            e.printStackTrace();
        }
    }

    public void setTransportForReservation(Transport transport) {
        currentTransport = transport;
        if (transportInfoLabel != null) {
            transportInfoLabel.setText(
                transport.getTypeTransport() + " • " +
                transport.getVilleDepart() + " → " + transport.getVilleArrivee() + " • " +
                transport.getDateDepart() + " à " + transport.getHeureDepart()
            );
        }
        if (nbPlacesSpinner != null) {
            nbPlacesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, transport.getNbPlaces(), 1)
            );
        }
    }

    @FXML
    private void handleConfirmReservation() {
        if (currentTransport == null) {
            showError("Erreur", "Aucun transport sélectionné");
            return;
        }

        int nbPlacesReservees = nbPlacesSpinner.getValue();
        
        try {
            // Calculate available places based on confirmed reservations
            int placesConfirmees = reservationService.getTotalPlacesConfirmees(currentTransport.getIdTransport());
            int placesDisponibles = currentTransport.getNbPlaces() - placesConfirmees;
            
            if (placesDisponibles <= 0) {
                showError("Complet", "Désolé, il n'y a plus de places disponibles pour ce transport.");
                return;
            }
            
            if (placesDisponibles < nbPlacesReservees) {
                showError("Places insuffisantes", 
                    "Seulement " + placesDisponibles + " place(s) disponible(s).\n" +
                    "Veuillez réduire le nombre de places.");
                return;
            }

            // Get first user (in real app, this would be the logged-in user)
            List<Utilisateur> users = utilisateurService.recupererUtilisateurs();
            if (users.isEmpty()) {
                showError("Erreur", "Aucun utilisateur disponible");
                return;
            }
            
            Reservation_transport reservation = new Reservation_transport();
            reservation.setDateReservation(Date.valueOf(LocalDate.now()));
            reservation.setNbPlacesReservees(nbPlacesReservees);
            reservation.setStatut("En attente");
            reservation.setIdTransport(currentTransport.getIdTransport());
            reservation.setIdUser(users.get(0).getIdUser()); // First user for demo

            reservationService.ajouterReservation(reservation);
            
            int nouvellesPlacesConfirmees = reservationService.getTotalPlacesConfirmees(currentTransport.getIdTransport());
            int placesRestantes = currentTransport.getNbPlaces() - nouvellesPlacesConfirmees;
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Réservation Confirmée");
            alert.setHeaderText("Félicitations !");
            alert.setContentText(
                "Votre réservation a été confirmée avec succès.\n\n" +
                "Référence: #" + reservation.getIdReservation() + "\n" +
                "Nombre de places: " + reservation.getNbPlacesReservees() + "\n" +
                "Statut: Confirmée\n" +
                "Places restantes: " + placesRestantes + "\n\n" +
                "Merci pour votre réservation !"
            );
            alert.showAndWait();

            handleBackToCatalog();
        } catch (SQLException e) {
            showError("Erreur lors de la réservation", e.getMessage());
        }
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontoffice_catalog.fxml"));
            Parent root = loader.load();

            FrontOfficeCatalogController controller = loader.getController();
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
}
