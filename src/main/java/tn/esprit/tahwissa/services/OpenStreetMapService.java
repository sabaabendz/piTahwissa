package tn.esprit.tahwissa.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Service OpenStreetMap - Gratuit, pas de clé API nécessaire
 * Utilise Nominatim pour le géocodage et Leaflet pour les cartes
 */
public class OpenStreetMapService {

    private final Gson gson = new Gson();
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    /**
     * Obtient les coordonnées d'une adresse (gratuit, sans clé)
     */
    public BigDecimal[] getCoordinates(String destinationName, String country) {
        try {
            String query = destinationName + ", " + country;
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = NOMINATIM_URL + "?q=" + encodedQuery + "&format=json&limit=1";

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                request.setHeader("User-Agent", "TahwissaTravelApp/1.0"); // Requis par Nominatim

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String json = EntityUtils.toString(response.getEntity());
                    JsonArray results = gson.fromJson(json, JsonArray.class);

                    if (results != null && results.size() > 0) {
                        JsonObject first = results.get(0).getAsJsonObject();
                        double lat = first.get("lat").getAsDouble();
                        double lon = first.get("lon").getAsDouble();

                        System.out.println("✅ Coordonnées trouvées: " + lat + ", " + lon);
                        return new BigDecimal[]{
                                BigDecimal.valueOf(lat),
                                BigDecimal.valueOf(lon)
                        };
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur géocodage: " + e.getMessage());
        }
        return null;
    }

    /**
     * Génère une carte HTML avec Leaflet (OpenStreetMap)
     */
    public String generateMapHtml(String destinationName, BigDecimal latitude, BigDecimal longitude) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <style>
                    body { margin: 0; padding: 0; }
                    #map { height: 100vh; width: 100%%; }
                    .info-window {
                        font-family: Arial, sans-serif;
                        padding: 10px;
                    }
                    .info-window h3 {
                        margin: 0 0 5px 0;
                        color: #4F46E5;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map').setView([%s, %s], 12);
                    
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
                        maxZoom: 19
                    }).addTo(map);
                    
                    var marker = L.marker([%s, %s]).addTo(map);
                    
                    var popupContent = '<div class="info-window">' +
                        '<h3>📍 %s</h3>' +
                        '<p>Latitude: %s</p>' +
                        '<p>Longitude: %s</p>' +
                        '</div>';
                    
                    marker.bindPopup(popupContent).openPopup();
                </script>
            </body>
            </html>
            """,
                latitude.toString(), longitude.toString(),
                latitude.toString(), longitude.toString(),
                destinationName, latitude.toString(), longitude.toString()
        );
    }

    /**
     * Génère une carte avec plusieurs marqueurs
     */
    public String generateMultipleMarkersMapHtml(List<MapMarker> markers) {
        StringBuilder markersJs = new StringBuilder();

        for (MapMarker m : markers) {
            markersJs.append(String.format(
                    "L.marker([%s, %s]).addTo(map).bindPopup('<b>%s</b><br>%s');\n",
                    m.latitude.toString(), m.longitude.toString(),
                    m.name, m.description != null ? m.description : ""
            ));
        }

        // Calculer le centre (moyenne des coordonnées)
        double centerLat = markers.stream()
                .mapToDouble(m -> m.latitude.doubleValue())
                .average().orElse(33.8869);
        double centerLng = markers.stream()
                .mapToDouble(m -> m.longitude.doubleValue())
                .average().orElse(9.5375);

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <style>body { margin: 0; } #map { height: 100vh; }</style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map').setView([%s, %s], 6);
                    
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; OpenStreetMap'
                    }).addTo(map);
                    
                    %s
                </script>
            </body>
            </html>
            """,
                centerLat, centerLng,
                markersJs.toString()
        );
    }

    // Classe interne pour les marqueurs
    public static class MapMarker {
        public String name;
        public String description;
        public BigDecimal latitude;
        public BigDecimal longitude;

        public MapMarker(String name, String description, BigDecimal latitude, BigDecimal longitude) {
            this.name = name;
            this.description = description;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}