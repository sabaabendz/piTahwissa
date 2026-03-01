package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WeatherService {
    
    private static final String API_KEY = ""; // Free API, no key needed
    private static final int TIMEOUT = 5000;
    
    /**
     * Get weather forecast for a city (async)
     */
    public static void getWeatherForCity(String cityName, WeatherCallback callback) {
        new Thread(() -> {
            try {
                String weatherData = fetchWeather(cityName);
                callback.onSuccess(weatherData);
            } catch (Exception e) {
                callback.onError("Weather unavailable");
            }
        }).start();
    }
    
    /**
     * Fetch weather from Open-Meteo API (free, no key required)
     */
    private static String fetchWeather(String cityName) throws Exception {
        // First, get coordinates for the city using geocoding
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
        String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=en&format=json";
        
        URL url = new URL(geoUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            return getDefaultWeather();
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        JSONObject geoResponse = new JSONObject(response.toString());
        
        if (!geoResponse.has("results") || geoResponse.getJSONArray("results").length() == 0) {
            return getDefaultWeather();
        }
        
        JSONObject location = geoResponse.getJSONArray("results").getJSONObject(0);
        double lat = location.getDouble("latitude");
        double lon = location.getDouble("longitude");
        
        // Get weather forecast
        String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + 
                           "&longitude=" + lon + 
                           "&current=temperature_2m,weather_code&timezone=auto";
        
        url = new URL(weatherUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        
        responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            return getDefaultWeather();
        }
        
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        JSONObject weatherResponse = new JSONObject(response.toString());
        JSONObject current = weatherResponse.getJSONObject("current");
        
        double temp = current.getDouble("temperature_2m");
        int weatherCode = current.getInt("weather_code");
        
        String condition = getWeatherCondition(weatherCode);
        String icon = getWeatherIcon(weatherCode);
        
        return String.format("%s %s %.0f°C", icon, condition, temp);
        
    }
    
    /**
     * Convert WMO weather code to readable condition
     */
    private static String getWeatherCondition(int code) {
        if (code == 0) return "Clear";
        if (code <= 3) return "Cloudy";
        if (code <= 49) return "Foggy";
        if (code <= 59) return "Drizzle";
        if (code <= 69) return "Rain";
        if (code <= 79) return "Snow";
        if (code <= 84) return "Showers";
        if (code <= 99) return "Thunderstorm";
        return "Unknown";
    }
    
    /**
     * Get weather emoji icon
     */
    private static String getWeatherIcon(int code) {
        if (code == 0) return "☀️";
        if (code <= 3) return "⛅";
        if (code <= 49) return "🌫️";
        if (code <= 59) return "🌦️";
        if (code <= 69) return "🌧️";
        if (code <= 79) return "❄️";
        if (code <= 84) return "🌧️";
        if (code <= 99) return "⛈️";
        return "🌡️";
    }
    
    /**
     * Default weather if API fails
     */
    private static String getDefaultWeather() {
        return "🌡️ Weather N/A";
    }
    
    /**
     * Callback interface for async weather fetching
     */
    public interface WeatherCallback {
        void onSuccess(String weatherData);
        void onError(String error);
    }
}
