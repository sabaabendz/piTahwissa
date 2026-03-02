package com.tahwissa.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Geocodes place names (e.g. event "lieu") to latitude/longitude using Nominatim (OpenStreetMap).
 * Respects Nominatim usage policy: custom User-Agent, minimal request rate.
 */
public final class GeocodingService {

    private static final String NOMINATIM_SEARCH = "https://nominatim.openstreetmap.org/search";
    private static final String USER_AGENT = "TahwissaApp/1.0 (JavaFX event map)";
    private static final Pattern LAT_PATTERN = Pattern.compile("\"lat\":\"([^\"]+)\"");
    private static final Pattern LON_PATTERN = Pattern.compile("\"lon\":\"([^\"]+)\"");

    private GeocodingService() {}

    /**
     * Geocode a place name to (lat, lon). Returns empty if place is blank or geocoding fails.
     */
    public static Optional<GeoPoint> geocode(String place) {
        if (place == null || place.isBlank()) {
            return Optional.empty();
        }
        try {
            String query = URLEncoder.encode(place.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create(NOMINATIM_SEARCH + "?q=" + query + "&format=json&limit=1");
            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(5))
                    .build();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();
            java.net.http.HttpResponse<String> response = client.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                return Optional.empty();
            }
            String body = response.body();
            Matcher latMatcher = LAT_PATTERN.matcher(body);
            Matcher lonMatcher = LON_PATTERN.matcher(body);
            if (latMatcher.find() && lonMatcher.find()) {
                double lat = Double.parseDouble(latMatcher.group(1));
                double lon = Double.parseDouble(lonMatcher.group(1));
                return Optional.of(new GeoPoint(lat, lon));
            }
        } catch (Exception ignored) {
            // Return empty on any error (network, parse, etc.)
        }
        return Optional.empty();
    }

    /**
     * Async geocode for use off the JavaFX thread. Completes with empty optional on failure.
     */
    public static CompletableFuture<Optional<GeoPoint>> geocodeAsync(String place) {
        return CompletableFuture.supplyAsync(() -> geocode(place));
    }

    /**
     * Geocode multiple places with 1 second delay between requests (Nominatim policy).
     * Returns list of optional points in same order as places; empty optional where geocoding failed.
     */
    public static List<Optional<GeoPoint>> geocodeAll(List<String> places) {
        List<Optional<GeoPoint>> results = new ArrayList<>(places.size());
        for (String place : places) {
            results.add(geocode(place));
            if (places.size() > 1) {
                try {
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return results;
    }

    public static final class GeoPoint {
        private final double lat;
        private final double lon;

        public GeoPoint(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() { return lat; }
        public double getLon() { return lon; }
    }
}
