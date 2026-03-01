package controllers;

import entites.Reservation_transport;
import entites.Transport;
import entites.Utilisateur;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import services.Reservation_transportService;
import services.TransportService;
import services.UtilisateurService;

import java.sql.SQLException;
import java.util.List;

public class FrontOfficeReservationsController {

    @FXML
    private VBox reservationsContainer;

    @FXML
    private ComboBox<String> statusFilter;

    private StackPane contentArea;
    private Reservation_transportService reservationService;
    private TransportService transportService;
    private UtilisateurService utilisateurService;
    private static final int USER_ID = 1; // Static user ID
    private List<Reservation_transport> allUserReservations;

    public FrontOfficeReservationsController() {
        reservationService = new Reservation_transportService();
        transportService = new TransportService();
        utilisateurService = new UtilisateurService();
    }

    @FXML
    public void initialize() {
        // Populate ComboBox
        statusFilter.getItems().addAll("Tous", "Confirmée", "En attente", "Annulée");
        statusFilter.setValue("Tous");
        
        loadReservations();
        
        // Setup filter listener
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterReservations(newVal);
        });
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    private void loadReservations() {
        try {
            List<Reservation_transport> allReservations = reservationService.recupererReservations();
            
            // Filter reservations for user ID 1
            allUserReservations = allReservations.stream()
                .filter(r -> r.getIdUser() == USER_ID)
                .toList();
            
            displayReservations(allUserReservations);

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Erreur lors du chargement des réservations.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            reservationsContainer.getChildren().add(errorLabel);
        }
    }

    private void filterReservations(String status) {
        if (allUserReservations == null) return;
        
        List<Reservation_transport> filtered;
        
        if (status == null || status.equals("Tous")) {
            filtered = allUserReservations;
        } else {
            filtered = allUserReservations.stream()
                .filter(r -> r.getStatut().equals(status))
                .toList();
        }
        
        displayReservations(filtered);
    }

    private void displayReservations(List<Reservation_transport> reservations) {
        reservationsContainer.getChildren().clear();
        
        if (reservations.isEmpty()) {
            Label emptyLabel = new Label("Aucune réservation trouvée.");
            emptyLabel.setFont(Font.font("System", 18));
            emptyLabel.setStyle("-fx-text-fill: #7F8C8D;");
            VBox emptyBox = new VBox(emptyLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            reservationsContainer.getChildren().add(emptyBox);
            return;
        }

        // Create card for each reservation
        for (Reservation_transport reservation : reservations) {
            Transport transport = getTransportById(reservation.getIdTransport());
            if (transport != null) {
                VBox card = createReservationCard(reservation, transport);
                reservationsContainer.getChildren().add(card);
            }
        }
    }

    private Transport getTransportById(int idTransport) {
        try {
            List<Transport> transports = transportService.recupererTransport();
            return transports.stream()
                .filter(t -> t.getIdTransport() == idTransport)
                .findFirst()
                .orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private VBox createReservationCard(Reservation_transport reservation, Transport transport) {
        VBox card = new VBox(20);
        card.getStyleClass().add("frontoffice-reservation-card");
        card.setPadding(new Insets(25));
        card.setMaxWidth(900);
        card.setAlignment(Pos.TOP_LEFT);

        // Header with transport info
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label(getTransportIcon(transport.getTypeTransport()));
        icon.setStyle("-fx-font-size: 40px;");
        
        VBox routeInfo = new VBox(5);
        Label routeLabel = new Label(transport.getVilleDepart() + " → " + transport.getVilleArrivee());
        routeLabel.setFont(Font.font("System Bold", 22));
        routeLabel.setTextFill(Color.web("#2C3E50"));
        
        Label typeLabel = new Label(transport.getTypeTransport());
        typeLabel.setFont(Font.font("System", 14));
        typeLabel.setTextFill(Color.web("#7F8C8D"));
        
        routeInfo.getChildren().addAll(routeLabel, typeLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(reservation.getStatut());
        statusLabel.setFont(Font.font("System Bold", 14));
        statusLabel.setPadding(new Insets(8, 20, 8, 20));
        statusLabel.setStyle(getStatusStyle(reservation.getStatut()));
        
        header.getChildren().addAll(icon, routeInfo, spacer, statusLabel);

        // Details section
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(40);
        detailsGrid.setVgap(15);
        detailsGrid.setPadding(new Insets(15));
        detailsGrid.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 10;");

        addDetailRow(detailsGrid, 0, "📅 Date du voyage", transport.getDateDepart().toString());
        addDetailRow(detailsGrid, 1, "🕐 Heure de départ", transport.getHeureDepart().toString());
        addDetailRow(detailsGrid, 2, "⏱️ Durée", transport.getDuree() + " heures");
        addDetailRow(detailsGrid, 3, "💺 Places réservées", String.valueOf(reservation.getNbPlacesReservees()));
        addDetailRow(detailsGrid, 4, "📋 Réservation effectuée le", reservation.getDateReservation().toString());
        addDetailRow(detailsGrid, 5, "💰 Prix total", String.format("%.2f TND", transport.getPrix() * reservation.getNbPlacesReservees()));

        card.getChildren().addAll(header, new Separator(), detailsGrid);

        return card;
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("System", 14));
        labelNode.setTextFill(Color.web("#7F8C8D"));
        
        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("System Bold", 14));
        valueNode.setTextFill(Color.web("#2C3E50"));
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
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

    private String getStatusStyle(String status) {
        switch (status.toLowerCase()) {
            case "confirmée":
                return "-fx-background-color: #D5F4E6; -fx-text-fill: #27AE60; -fx-background-radius: 20;";
            case "en attente":
                return "-fx-background-color: #FFF4E6; -fx-text-fill: #F39C12; -fx-background-radius: 20;";
            case "annulée":
                return "-fx-background-color: #FADBD8; -fx-text-fill: #E74C3C; -fx-background-radius: 20;";
            default:
                return "-fx-background-color: #E8E8E8; -fx-text-fill: #7F8C8D; -fx-background-radius: 20;";
        }
    }
}
