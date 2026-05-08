package tn.esprit.tahwissa.controllers.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tn.esprit.tahwissa.models.Destination;
import tn.esprit.tahwissa.models.PointInteret;
import tn.esprit.tahwissa.services.DestinationService;
import tn.esprit.tahwissa.services.PointInteretService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientDestinationsController implements Initializable {

    @FXML private ListView<Destination> destinationsListView;
    @FXML private VBox detailsContainer;
    @FXML private Label lblDestinationNom;
    @FXML private Label lblDestinationPays;
    @FXML private Label lblDestinationVille;
    @FXML private TextArea txtDestinationDescription;
    @FXML private Label lblDestinationCoords;
    @FXML private ListView<PointInteret> pointsInteretListView;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbPaysFilter;
    @FXML private Label lblTotalDestinations;
    @FXML private Label lblTotalPoints;
    @FXML private ImageView destinationImageView;

    private DestinationService destinationService;
    private PointInteretService pointInteretService;
    private ObservableList<Destination> destinationsList;
    private ObservableList<Destination> filteredList;
    private ObservableList<PointInteret> pointsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            destinationService = new DestinationService();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        pointInteretService = new PointInteretService();
        destinationsList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
        pointsList = FXCollections.observableArrayList();

        setupFilters();
        setupDestinationsList();
        loadData();
        updateStatistics();
    }

    private void setupFilters() {
        cmbPaysFilter.setItems(FXCollections.observableArrayList("Tous les pays"));
        cmbPaysFilter.setValue("Tous les pays");

        txtSearch.textProperty().addListener((obs, old, newVal) -> filterDestinations());
        cmbPaysFilter.setOnAction(e -> filterDestinations());
    }

    private void setupDestinationsList() {
        destinationsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Destination item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cell = new HBox(10);
                    cell.setStyle("-fx-padding: 10px; -fx-alignment: center-left;");

                    Label icon = new Label("📍");
                    icon.setStyle("-fx-font-size: 16px;");

                    VBox info = new VBox(3);
                    Label nom = new Label(item.getNom());
                    nom.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Label pays = new Label(item.getPays() + (item.getVille() != null ? " - " + item.getVille() : ""));
                    pays.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

                    info.getChildren().addAll(nom, pays);
                    cell.getChildren().addAll(icon, info);

                    setGraphic(cell);
                }
            }
        });

        destinationsListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        showDestinationDetails(selected);
                    }
                }
        );
    }

    private void loadData() {
        try {
            List<Destination> destinations = destinationService.afficherDestinations();
            destinationsList.setAll(destinations);
            filteredList.setAll(destinations);
            destinationsListView.setItems(filteredList);

            List<String> pays = destinations.stream()
                    .map(Destination::getPays)
                    .distinct()
                    .collect(Collectors.toList());
            cmbPaysFilter.getItems().clear();
            cmbPaysFilter.getItems().add("Tous les pays");
            cmbPaysFilter.getItems().addAll(pays);

        } catch (Exception e) {
            showError("Erreur", "Impossible de charger les destinations: " + e.getMessage());
        }
    }

    private void filterDestinations() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String selectedPays = cmbPaysFilter.getValue();

        List<Destination> results = destinationsList.stream()
                .filter(d -> {
                    boolean matchSearch = searchText.isEmpty() ||
                            d.getNom().toLowerCase().contains(searchText) ||
                            d.getPays().toLowerCase().contains(searchText) ||
                            (d.getVille() != null && d.getVille().toLowerCase().contains(searchText));

                    boolean matchPays = selectedPays == null ||
                            selectedPays.equals("Tous les pays") ||
                            d.getPays().equals(selectedPays);

                    return matchSearch && matchPays;
                })
                .collect(Collectors.toList());

        filteredList.setAll(results);
        destinationsListView.setItems(filteredList);
    }

    private void showDestinationDetails(Destination destination) {
        System.out.println("=== AFFICHAGE DÉTAILS DESTINATION ===");
        System.out.println("Nom: " + destination.getNom());
        System.out.println("Image URL from DB: " + destination.getImageUrl());

        lblDestinationNom.setText(destination.getNom());
        lblDestinationPays.setText("Pays: " + destination.getPays());
        lblDestinationVille.setText("Ville: " + (destination.getVille() != null ? destination.getVille() : "Non spécifiée"));
        txtDestinationDescription.setText(destination.getDescription() != null ?
                destination.getDescription() : "Aucune description disponible.");

        if (destination.getLatitude() != null && destination.getLongitude() != null) {
            lblDestinationCoords.setText(String.format("Coordonnées: %.6f, %.6f",
                    destination.getLatitude(), destination.getLongitude()));
        } else {
            lblDestinationCoords.setText("Coordonnées: Non disponibles");
        }

        // Charger l'image
        loadDestinationImage(destination);

        loadPointsInteret(destination.getIdDestination());
        detailsContainer.setVisible(true);
    }

    private void loadDestinationImage(Destination destination) {
        String imageUrl = destination.getImageUrl();

        // Réinitialiser l'image
        destinationImageView.setImage(null);

        if (imageUrl == null || imageUrl.isEmpty()) {
            System.out.println("⚠️ Pas d'URL d'image pour " + destination.getNom());
            setDefaultImage();
            return;
        }

        try {
            // MÉTHODE 1: Essayer avec getResource (chemin relatif aux resources)
            String resourcePath = imageUrl;
            if (!resourcePath.startsWith("/")) {
                resourcePath = "/" + resourcePath;
            }

            System.out.println("🔍 Essai chemin 1 (resource): " + resourcePath);
            URL resourceUrl = getClass().getResource(resourcePath);

            if (resourceUrl != null) {
                System.out.println("✅ Resource trouvée: " + resourceUrl);
                Image image = new Image(resourceUrl.toExternalForm(), 300, 200, true, true);
                destinationImageView.setImage(image);
                return;
            }

            // MÉTHODE 2: Chemin absolu vers le fichier
            String projectPath = System.getProperty("user.dir");
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            String fullPath = projectPath + "/src/main/resources/images/destinations/" + fileName;

            System.out.println("🔍 Essai chemin 2 (fichier): " + fullPath);
            File imageFile = new File(fullPath);

            if (imageFile.exists()) {
                System.out.println("✅ Fichier trouvé: " + imageFile.getAbsolutePath());
                Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true);
                destinationImageView.setImage(image);
                return;
            }

            // MÉTHODE 3: Chercher dans le dossier target/classes
            String targetPath = projectPath + "/target/classes/images/destinations/" + fileName;
            System.out.println("🔍 Essai chemin 3 (target): " + targetPath);
            File targetFile = new File(targetPath);

            if (targetFile.exists()) {
                System.out.println("✅ Fichier trouvé dans target: " + targetPath);
                Image image = new Image(targetFile.toURI().toString(), 300, 200, true, true);
                destinationImageView.setImage(image);
                return;
            }

            System.out.println("❌ Aucune image trouvée pour: " + destination.getNom());
            setDefaultImage();

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement image: " + e.getMessage());
            e.printStackTrace();
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        try {
            // Essayer de charger une image par défaut
            URL defaultUrl = getClass().getResource("/images/default-destination.jpg");
            if (defaultUrl != null) {
                Image defaultImage = new Image(defaultUrl.toExternalForm(), 300, 200, true, true);
                destinationImageView.setImage(defaultImage);
                System.out.println("🖼️ Image par défaut chargée");
                return;
            }

            // Si pas d'image par défaut, créer une image placeholder
            createPlaceholderImage();

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement image par défaut: " + e.getMessage());
            destinationImageView.setImage(null);
        }
    }

    private void createPlaceholderImage() {
        // Image placeholder simple (juste un carré gris)
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(300, 200);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
        gc.fillRect(0, 0, 300, 200);
        gc.setFill(javafx.scene.paint.Color.DARKGRAY);
        gc.setFont(javafx.scene.text.Font.font(20));
        gc.fillText("Pas d'image", 90, 100);

        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(300, 200);
        canvas.snapshot(null, writableImage);
        destinationImageView.setImage(writableImage);
    }

    private void loadPointsInteret(int destinationId) {
        try {
            List<PointInteret> points = pointInteretService.getPointsByDestination(destinationId);
            pointsList.setAll(points);

            pointsInteretListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(PointInteret item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox cell = new HBox(10);
                        cell.setStyle("-fx-padding: 8px; -fx-alignment: center-left;");

                        String icon = switch (item.getType().toLowerCase()) {
                            case "monument" -> "🏛️";
                            case "plage" -> "🏖️";
                            case "musée" -> "🏛️";
                            case "restaurant" -> "🍽️";
                            case "parc" -> "🌳";
                            case "hôtel" -> "🏨";
                            default -> "📍";
                        };

                        Label iconLabel = new Label(icon);
                        iconLabel.setStyle("-fx-font-size: 14px;");

                        VBox info = new VBox(2);
                        Label nom = new Label(item.getNom());
                        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

                        Label type = new Label(item.getType());
                        type.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");

                        info.getChildren().addAll(nom, type);
                        cell.getChildren().addAll(iconLabel, info);

                        setGraphic(cell);
                    }
                }
            });

            pointsInteretListView.setItems(pointsList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        lblTotalDestinations.setText(String.valueOf(destinationsList.size()));
        try {
            int totalPoints = pointInteretService.afficherPointsInteret().size();
            lblTotalPoints.setText(String.valueOf(totalPoints));
        } catch (Exception e) {
            lblTotalPoints.setText("0");
        }
    }

    @FXML
    private void showOnMap() {
        Destination selected = destinationsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection requise", "Veuillez sélectionner une destination d'abord.");
            return;
        }

        if (selected.getLatitude() == null || selected.getLongitude() == null) {
            showError("Coordonnées manquantes", "Cette destination n'a pas de coordonnées GPS.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/destination/MapView.fxml"));
            Parent mapView = loader.load();

            tn.esprit.tahwissa.controllers.destination.MapViewController controller =
                    loader.getController();
            controller.showDestinationOnMap(selected.getNom(),
                    selected.getLatitude(), selected.getLongitude());

            StackPane contentArea = (StackPane) txtSearch.getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(mapView);
            }

        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la carte: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}