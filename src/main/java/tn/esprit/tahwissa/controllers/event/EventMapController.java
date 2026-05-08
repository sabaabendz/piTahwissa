package tn.esprit.tahwissa.controllers.event;

import com.gluonhq.maps.MapView;
import tn.esprit.tahwissa.models.Evenement;
import tn.esprit.tahwissa.maps.EventMapLayer;
import tn.esprit.tahwissa.services.EvenementService;
import tn.esprit.tahwissa.services.GeocodingService;
import tn.esprit.tahwissa.services.GeocodingService.GeoPoint;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the event map view. Loads available events, geocodes their "lieu",
 * and displays them as lollipop markers on a Gluon MapView.
 */
public class EventMapController {

    private static final double DEFAULT_LAT = 34.0;
    private static final double DEFAULT_LON = 9.0;
    private static final int DEFAULT_ZOOM = 6;

    @FXML private StackPane mapStack;
    @FXML private javafx.scene.control.ProgressIndicator progressIndicator;

    private final EvenementService evenementService = new EvenementService();

    @FXML
    public void initialize() {
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<Evenement> events = evenementService.getAvailableEvenements();
                List<String> lieux = events.stream()
                        .map(Evenement::getLieu)
                        .filter(l -> l != null && !l.isBlank())
                        .distinct()
                        .toList();
                List<Optional<GeoPoint>> points = GeocodingService.geocodeAll(lieux);

                Platform.runLater(() -> {
                    buildMap(events, lieux, points);
                });
                return null;
            }
        };
        loadTask.setOnFailed(t -> Platform.runLater(() -> {
            progressIndicator.setVisible(false);
            showFallbackMessage();
        }));
        new Thread(loadTask).start();
    }

    private void buildMap(List<Evenement> events, List<String> lieux, List<Optional<GeoPoint>> points) {
        progressIndicator.setVisible(false);

        MapView mapView = new MapView();
        mapView.setCenter(DEFAULT_LAT, DEFAULT_LON);
        mapView.setZoom(DEFAULT_ZOOM);

        EventMapLayer layer = new EventMapLayer();
        layer.setOnMarkerClicked(this::openReservationForm);
        int added = 0;
        for (int i = 0; i < points.size(); i++) {
            Optional<GeoPoint> opt = points.get(i);
            if (opt.isPresent()) {
                String lieu = lieux.get(i);
                Evenement eventForLieu = events.stream()
                        .filter(ev -> lieu.equals(ev.getLieu()))
                        .findFirst()
                        .orElse(null);
                GeoPoint p = opt.get();
                layer.addMarker(p.getLat(), p.getLon(), eventForLieu);
                added++;
            }
        }
        mapView.addLayer(layer);

        if (added == 0) {
            showFallbackMessage();
            return;
        }

        mapStack.getChildren().clear();
        mapView.setMaxWidth(Double.MAX_VALUE);
        mapView.setMaxHeight(Double.MAX_VALUE);
        StackPane.setAlignment(mapView, javafx.geometry.Pos.CENTER);
        mapStack.getChildren().add(mapView);
    }

    private void openReservationForm(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event/ReservationForm.fxml"));
            Parent content = loader.load();
            ReservationFormController ctrl = loader.getController();
            ctrl.setEvenement(event);
            StackPane contentArea = (StackPane) mapStack.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFallbackMessage() {
        mapStack.getChildren().clear();
        javafx.scene.control.Label label = new javafx.scene.control.Label(
                "Aucun lieu géolocalisable pour le moment. Vérifiez votre connexion ou les noms des lieux.");
        label.setWrapText(true);
        label.getStyleClass().add("text-muted");
        label.setStyle("-fx-font-size: 14px; -fx-padding: 24px;");
        mapStack.getChildren().add(label);
    }
}
