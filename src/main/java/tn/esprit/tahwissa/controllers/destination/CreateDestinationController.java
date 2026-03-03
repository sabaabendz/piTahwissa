package tn.esprit.tahwissa.controllers.destination;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import tn.esprit.tahwissa.models.Destination;
import tn.esprit.tahwissa.services.DestinationService;
import tn.esprit.tahwissa.services.OpenStreetMapService;
import tn.esprit.tahwissa.services.OpenRouterService;
import tn.esprit.tahwissa.services.HuggingFaceImageService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateDestinationController implements Initializable {

    private DestinationService destinationService;
    private OpenStreetMapService mapsService = new OpenStreetMapService();
    private OpenRouterService aiService = new OpenRouterService();
    private HuggingFaceImageService imageService = new HuggingFaceImageService();

    private Destination currentDestination;
    private DestinationViewController parentController;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtNom;
    @FXML private TextField txtPays;
    @FXML private TextField txtVille;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtImageUrl;
    @FXML private TextField txtLatitude;
    @FXML private TextField txtLongitude;
    @FXML private Button btnSave;
    @FXML private Button btnGenerateDescription;
    @FXML private Button btnGenerateImage;
    @FXML private Button btnGetCoordinates;
    @FXML private Button btnShowMap;
    @FXML private Label errNom;
    @FXML private Label errPays;
    @FXML private Label errLatitude;
    @FXML private Label errLongitude;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            destinationService = new DestinationService();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setupValidation();

        // Test de connexion au démarrage
        new Thread(() -> {
            boolean connected = imageService.testConnection();
            if (!connected) {
                Platform.runLater(() -> {
                    System.err.println("⚠️ Attention: Problème de connexion à Hugging Face");
                });
            }
        }).start();
    }

    public void setParentController(DestinationViewController controller) {
        this.parentController = controller;
    }

    public void setDestination(Destination destination) {
        this.currentDestination = destination;
        lblFormTitle.setText("Modifier la Destination");
        btnSave.setText("💾 Mettre à jour");
        txtNom.setText(destination.getNom());
        txtPays.setText(destination.getPays());
        txtVille.setText(destination.getVille());
        txtDescription.setText(destination.getDescription());
        txtImageUrl.setText(destination.getImageUrl());
        if (destination.getLatitude() != null)
            txtLatitude.setText(destination.getLatitude().toString());
        if (destination.getLongitude() != null)
            txtLongitude.setText(destination.getLongitude().toString());
    }

    @FXML
    private void generateAIDescription() {
        String nom = txtNom.getText().trim();
        String pays = txtPays.getText().trim();
        String ville = txtVille.getText().trim();

        if (nom.isEmpty() || pays.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Champs manquants",
                    "Veuillez remplir au moins le nom et le pays.");
            return;
        }

        btnGenerateDescription.setDisable(true);
        btnGenerateDescription.setText("🤖 Génération...");

        new Thread(() -> {
            try {
                String description = aiService.generateDestinationDescription(nom, pays, ville);
                Platform.runLater(() -> {
                    txtDescription.setText(description);
                    btnGenerateDescription.setDisable(false);
                    btnGenerateDescription.setText("🤖 Générer Description IA");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnGenerateDescription.setDisable(false);
                    btnGenerateDescription.setText("🤖 Générer Description IA");
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void generateAIImage() {
        String nom = txtNom.getText().trim();
        String pays = txtPays.getText().trim();

        if (nom.isEmpty() || pays.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Champs manquants",
                    "Veuillez remplir le nom et le pays d'abord.");
            return;
        }

        btnGenerateImage.setDisable(true);
        btnGenerateImage.setText("⏳ Génération...");

        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Génération en cours",
                            "L'image est en cours de génération...\nCela peut prendre 10-30 secondes.");
                });

                // Générer et sauvegarder l'image
                String imageUrl = imageService.generateAndSaveImage(nom, pays);

                Platform.runLater(() -> {
                    // Mettre à jour le champ URL
                    txtImageUrl.setText(imageUrl);

                    btnGenerateImage.setDisable(false);
                    btnGenerateImage.setText("🎨 Générer Image IA");

                    showAlert(Alert.AlertType.INFORMATION,
                            "Succès",
                            "Image générée avec succès !");
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    btnGenerateImage.setDisable(false);
                    btnGenerateImage.setText("🎨 Générer Image IA");

                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("chargement")) {
                        showAlert(Alert.AlertType.WARNING,
                                "Modèle en chargement",
                                "Le modèle est en cours de chargement.\nVeuillez réessayer dans 20-30 secondes.");
                    } else {
                        showAlert(Alert.AlertType.ERROR,
                                "Erreur",
                                "Impossible de générer l'image: " + errorMessage);
                    }
                });
            }
        }).start();
    }

    @FXML
    private void getCoordinatesFromAddress() {
        String nom = txtNom.getText().trim();
        String pays = txtPays.getText().trim();

        if (nom.isEmpty() || pays.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Champs manquants",
                    "Veuillez remplir le nom et le pays.");
            return;
        }

        btnGetCoordinates.setDisable(true);
        btnGetCoordinates.setText("🗺️ Recherche...");

        new Thread(() -> {
            try {
                BigDecimal[] coords = mapsService.getCoordinates(nom, pays);
                Platform.runLater(() -> {
                    if (coords != null) {
                        txtLatitude.setText(coords[0].toString());
                        txtLongitude.setText(coords[1].toString());
                        showAlert(Alert.AlertType.INFORMATION,
                                "Coordonnées trouvées",
                                "Latitude : " + coords[0] + "\nLongitude : " + coords[1]);
                    } else {
                        showAlert(Alert.AlertType.ERROR,
                                "Erreur",
                                "Impossible de trouver les coordonnées.");
                    }
                    btnGetCoordinates.setDisable(false);
                    btnGetCoordinates.setText("🗺️ Obtenir Coordonnées");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnGetCoordinates.setDisable(false);
                    btnGetCoordinates.setText("🗺️ Obtenir Coordonnées");
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void showOnMap() {
        String nom = txtNom.getText().trim();
        String latStr = txtLatitude.getText().trim();
        String lngStr = txtLongitude.getText().trim();

        if (nom.isEmpty() || latStr.isEmpty() || lngStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Coordonnées manquantes",
                    "Veuillez remplir le nom et les coordonnées.");
            return;
        }

        try {
            BigDecimal lat = new BigDecimal(latStr);
            BigDecimal lng = new BigDecimal(lngStr);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/destination/MapView.fxml"));

            Parent mapView = loader.load();

            MapViewController controller = loader.getController();
            controller.showDestinationOnMap(nom, lat, lng);

            StackPane contentArea = (StackPane) txtNom.getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(mapView);
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "Les coordonnées doivent être valides.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la carte: " + e.getMessage());
        }
    }

    @FXML
    private void saveDestination() {
        if (!validateForm()) return;

        try {
            Destination destination = new Destination(
                    txtNom.getText().trim(),
                    txtPays.getText().trim(),
                    txtVille.getText().trim(),
                    txtDescription.getText().trim(),
                    txtImageUrl.getText().trim(),
                    txtLatitude.getText().isEmpty() ? null : new BigDecimal(txtLatitude.getText().trim()),
                    txtLongitude.getText().isEmpty() ? null : new BigDecimal(txtLongitude.getText().trim())
            );

            if (currentDestination != null) {
                destination.setIdDestination(currentDestination.getIdDestination());
                destinationService.modifierDestination(destination);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Destination modifiée !");
            } else {
                destinationService.ajouterDestination(destination);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Destination créée !");
            }

            returnToList();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void returnToList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/destination/DestinationsView.fxml"));
            Parent listView = loader.load();

            StackPane contentArea = (StackPane) txtNom.getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(listView);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupValidation() {
        txtNom.textProperty().addListener((obs, o, n) -> validateNom());
        txtPays.textProperty().addListener((obs, o, n) -> validatePays());
        txtLatitude.textProperty().addListener((obs, o, n) -> validateLatitude());
        txtLongitude.textProperty().addListener((obs, o, n) -> validateLongitude());
    }

    private boolean validateNom() {
        String nom = txtNom.getText().trim();
        if (nom.isEmpty() || nom.length() < 3) {
            showError(errNom, "Nom invalide (min 3 caractères)");
            return false;
        }
        hideError(errNom);
        return true;
    }

    private boolean validatePays() {
        if (txtPays.getText().trim().isEmpty()) {
            showError(errPays, "Pays obligatoire");
            return false;
        }
        hideError(errPays);
        return true;
    }

    private boolean validateLatitude() {
        return validateCoordinate(txtLatitude, errLatitude, new BigDecimal("-90"), new BigDecimal("90"));
    }

    private boolean validateLongitude() {
        return validateCoordinate(txtLongitude, errLongitude, new BigDecimal("-180"), new BigDecimal("180"));
    }

    private boolean validateCoordinate(TextField field, Label error, BigDecimal min, BigDecimal max) {
        String value = field.getText().trim();
        if (!value.isEmpty()) {
            try {
                BigDecimal val = new BigDecimal(value);
                if (val.compareTo(min) < 0 || val.compareTo(max) > 0) {
                    showError(error, "Valeur hors limite");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError(error, "Format invalide");
                return false;
            }
        }
        hideError(error);
        return true;
    }

    private boolean validateForm() {
        return validateNom() && validatePays() && validateLatitude() && validateLongitude();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setManaged(true);
        label.setVisible(true);
    }

    private void hideError(Label label) {
        label.setManaged(false);
        label.setVisible(false);
    }
}