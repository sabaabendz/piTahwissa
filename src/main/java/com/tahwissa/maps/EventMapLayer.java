package com.tahwissa.maps;

import com.gluonhq.maps.MapLayer;
import com.tahwissa.entity.Evenement;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Map layer that displays event locations as lollipop markers (circle on a stem).
 * Clicking a marker invokes the registered handler with the associated event.
 */
public final class EventMapLayer extends MapLayer {

    private static final double STEM_LENGTH = 24;
    private static final double HEAD_RADIUS = 10;
    private static final Color HEAD_COLOR = Color.web("#9333EA");
    private static final Color STEM_COLOR = Color.web("#6D28D9");

    private final List<MarkerEntry> entries = new ArrayList<>();
    private Consumer<Evenement> onMarkerClicked;

    /**
     * Sets the handler invoked when a lollipop is clicked. Pass the event for that marker.
     */
    public void setOnMarkerClicked(Consumer<Evenement> handler) {
        this.onMarkerClicked = handler;
    }

    /**
     * Adds a marker at the given coordinates for the given event. Call from JavaFX thread after baseMap is set.
     */
    public void addMarker(double lat, double lon, Evenement event) {
        Group lollipop = createLollipopNode();
        if (event != null && event.getTitre() != null) {
            Tooltip.install(lollipop, new Tooltip(event.getTitre()));
        }
        lollipop.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && onMarkerClicked != null && event != null) {
                onMarkerClicked.accept(event);
            }
        });
        lollipop.setCursor(javafx.scene.Cursor.HAND);
        entries.add(new MarkerEntry(lat, lon, lollipop));
        getChildren().add(lollipop);
        markDirty();
    }

    public void clearMarkers() {
        getChildren().clear();
        entries.clear();
        markDirty();
    }

    @Override
    protected void layoutLayer() {
        for (MarkerEntry entry : entries) {
            Point2D point = getMapPoint(entry.lat, entry.lon);
            if (point != null) {
                entry.node.setTranslateX(point.getX());
                entry.node.setTranslateY(point.getY());
                entry.node.setVisible(true);
            } else {
                entry.node.setVisible(false);
            }
        }
    }

    private static Group createLollipopNode() {
        Line stem = new Line(0, 0, 0, -STEM_LENGTH);
        stem.setStroke(STEM_COLOR);
        stem.setStrokeWidth(3);

        Circle head = new Circle(0, -STEM_LENGTH, HEAD_RADIUS);
        head.setFill(HEAD_COLOR);
        head.setStroke(Color.WHITE);
        head.setStrokeWidth(2);

        Group group = new Group(stem, head);
        group.setMouseTransparent(false);
        return group;
    }

    private static final class MarkerEntry {
        final double lat;
        final double lon;
        final Group node;

        MarkerEntry(double lat, double lon, Group node) {
            this.lat = lat;
            this.lon = lon;
            this.node = node;
        }
    }
}
