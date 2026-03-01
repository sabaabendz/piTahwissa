package utils;

import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ImageService {
    
    // Unsplash API Access Key (free tier: 50 requests/hour)
    // Get your key at: https://unsplash.com/developers
    private static final String UNSPLASH_ACCESS_KEY = "ZV154MIjdGoRL1F0zMC7pmKhh-ZA-E4jrpzIvg5JUOY";
    
    // Cache to avoid reloading same images
    private static final Map<String, Image> imageCache = new HashMap<>();
    
    // Fallback images if API fails
    private static final Map<String, String> FALLBACK_IMAGES = new HashMap<>();
    
    static {
        FALLBACK_IMAGES.put("Paris", "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=400");
        FALLBACK_IMAGES.put("London", "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?w=400");
        FALLBACK_IMAGES.put("Rome", "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=400");
        FALLBACK_IMAGES.put("DEFAULT", "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400");
    }
    
    /**
     * Get image for destination city - automatically searches online
     * @param cityName Name of arrival city
     * @return JavaFX Image object
     */
    public static Image getCityImage(String cityName) {
        // Check cache first
        if (imageCache.containsKey(cityName)) {
            return imageCache.get(cityName);
        }
        
        try {
            // Try to get image from Unsplash API
            String imageUrl = searchCityImageUrl(cityName);
            
            if (imageUrl != null) {
                Image image = loadImageFromUrl(imageUrl);
                imageCache.put(cityName, image);
                return image;
            }
        } catch (Exception e) {
            System.err.println("API search failed for " + cityName + ": " + e.getMessage());
        }
        
        // Fallback to predefined images
        String fallbackUrl = FALLBACK_IMAGES.getOrDefault(cityName, FALLBACK_IMAGES.get("DEFAULT"));
        try {
            Image image = loadImageFromUrl(fallbackUrl);
            imageCache.put(cityName, image);
            return image;
        } catch (Exception e) {
            System.err.println("Error loading fallback image: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Search for city image using Unsplash API
     */
    private static String searchCityImageUrl(String cityName) throws Exception {
        // Build search query: "cityName landmark" for better results
        String searchQuery = URLEncoder.encode(cityName + " landmark travel", StandardCharsets.UTF_8);
        String apiUrl = "https://api.unsplash.com/search/photos?query=" + searchQuery + 
                       "&per_page=1&orientation=landscape";
        
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Client-ID " + UNSPLASH_ACCESS_KEY);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");
            
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                JSONObject urls = firstResult.getJSONObject("urls");
                return urls.getString("regular") + "&w=400&h=200&fit=crop";
            }
        }
        
        return null;
    }
    
    /**
     * Load image from URL
     */
    private static Image loadImageFromUrl(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        InputStream inputStream = connection.getInputStream();
        Image image = new Image(inputStream, 400, 200, true, true);
        inputStream.close();
        
        return image;
    }
    
    /**
     * Load image asynchronously to avoid blocking UI
     */
    public static void loadImageAsync(String cityName, ImageCallback callback) {
        new Thread(() -> {
            try {
                Image image = getCityImage(cityName);
                javafx.application.Platform.runLater(() -> callback.onImageLoaded(image));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> callback.onImageLoaded(null));
            }
        }).start();
    }
    
    /**
     * Callback interface for async image loading
     */
    public interface ImageCallback {
        void onImageLoaded(Image image);
    }
    
    /**
     * Clear image cache (useful for memory management)
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
