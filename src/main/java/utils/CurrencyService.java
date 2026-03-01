package utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurrencyService {
    
    // Mock mode: set to true if internet is blocked/restricted
    private static final boolean USE_MOCK_DATA = false;
    
    private static String userCurrency = null;
    private static String userCountryCode = null;
    private static Map<String, Double> exchangeRates = new HashMap<>();
    private static final double BASE_TND_RATE = 1.0; // TND is our base currency
    
    /**
     * Detect user's currency based on their location (IP geolocation)
     */
    public static void detectUserCurrency() {
        
        // MOCK MODE: Simulate Tunisia location
        if (USE_MOCK_DATA) {
            System.out.println("🌍 Using mock location data");
            userCurrency = "TND";
            userCountryCode = "TN";
            setDefaultRates();
            System.out.println("✓ Simulated detection: TND (Tunisia)");
            return;
        }
        
        // REAL API MODE
        try {
            String apiUrl = "https://ipinfo.io/json";
            
            System.out.println("Detecting user location and currency...");
            
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            System.out.println("IP API response code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                userCountryCode = jsonResponse.getString("country");
                
                // Map country code to currency
                userCurrency = getCurrencyFromCountry(userCountryCode);
                
                System.out.println("✓ Detected currency: " + userCurrency + " (" + userCountryCode + ")");
                
                // Fetch exchange rates
                fetchExchangeRates();
            } else {
                System.err.println("IP API failed, using default currency");
                setDefaultCurrency();
            }
        } catch (Exception e) {
            System.err.println("Currency detection failed: " + e.getMessage());
            setDefaultCurrency();
        }
    }
    
    /**
     * Map country code to currency
     */
    private static String getCurrencyFromCountry(String countryCode) {
        Map<String, String> countryToCurrency = new HashMap<>();
        // Americas
        countryToCurrency.put("US", "USD");
        countryToCurrency.put("CA", "CAD");
        countryToCurrency.put("MX", "MXN");
        countryToCurrency.put("BR", "BRL");
        
        // Europe
        countryToCurrency.put("GB", "GBP");
        countryToCurrency.put("FR", "EUR");
        countryToCurrency.put("DE", "EUR");
        countryToCurrency.put("ES", "EUR");
        countryToCurrency.put("IT", "EUR");
        countryToCurrency.put("NL", "EUR");
        countryToCurrency.put("BE", "EUR");
        countryToCurrency.put("PT", "EUR");
        countryToCurrency.put("GR", "EUR");
        countryToCurrency.put("AT", "EUR");
        countryToCurrency.put("IE", "EUR");
        countryToCurrency.put("CH", "CHF");
        countryToCurrency.put("SE", "SEK");
        countryToCurrency.put("NO", "NOK");
        countryToCurrency.put("DK", "DKK");
        countryToCurrency.put("PL", "PLN");
        countryToCurrency.put("CZ", "CZK");
        countryToCurrency.put("HU", "HUF");
        countryToCurrency.put("RO", "RON");
        
        // Africa & Middle East
        countryToCurrency.put("TN", "TND");
        countryToCurrency.put("MA", "MAD");
        countryToCurrency.put("DZ", "DZD");
        countryToCurrency.put("EG", "EGP");
        countryToCurrency.put("SA", "SAR");
        countryToCurrency.put("AE", "AED");
        countryToCurrency.put("QA", "QAR");
        countryToCurrency.put("KW", "KWD");
        
        // Asia
        countryToCurrency.put("JP", "JPY");
        countryToCurrency.put("CN", "CNY");
        countryToCurrency.put("IN", "INR");
        countryToCurrency.put("KR", "KRW");
        countryToCurrency.put("SG", "SGD");
        countryToCurrency.put("TH", "THB");
        
        // Oceania
        countryToCurrency.put("AU", "AUD");
        countryToCurrency.put("NZ", "NZD");
        
        return countryToCurrency.getOrDefault(countryCode, "USD");
    }
    
    /**
     * Fetch exchange rates from TND to other currencies
     */
    private static void fetchExchangeRates() {
        try {
            // Use exchangerate-api.com (free tier: 1500 requests/month, no key needed for basic)
            String apiUrl = "https://api.exchangerate-api.com/v4/latest/TND";
            
            System.out.println("Fetching exchange rates from TND...");
            
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            System.out.println("Exchange rate API response code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject rates = jsonResponse.getJSONObject("rates");
                
                // Store all exchange rates
                exchangeRates.put("TND", 1.0);
                exchangeRates.put("EUR", rates.getDouble("EUR"));
                exchangeRates.put("USD", rates.getDouble("USD"));
                exchangeRates.put("GBP", rates.getDouble("GBP"));
                exchangeRates.put("MAD", rates.getDouble("MAD"));
                exchangeRates.put("DZD", rates.getDouble("DZD"));
                exchangeRates.put("SAR", rates.getDouble("SAR"));
                exchangeRates.put("AED", rates.getDouble("AED"));
                exchangeRates.put("CAD", rates.getDouble("CAD"));
                exchangeRates.put("MXN", rates.getDouble("MXN"));
                exchangeRates.put("BRL", rates.getDouble("BRL"));
                exchangeRates.put("CHF", rates.getDouble("CHF"));
                exchangeRates.put("SEK", rates.getDouble("SEK"));
                exchangeRates.put("NOK", rates.getDouble("NOK"));
                exchangeRates.put("DKK", rates.getDouble("DKK"));
                exchangeRates.put("PLN", rates.getDouble("PLN"));
                exchangeRates.put("CZK", rates.getDouble("CZK"));
                exchangeRates.put("HUF", rates.getDouble("HUF"));
                exchangeRates.put("RON", rates.getDouble("RON"));
                exchangeRates.put("EGP", rates.getDouble("EGP"));
                exchangeRates.put("QAR", rates.getDouble("QAR"));
                exchangeRates.put("KWD", rates.getDouble("KWD"));
                exchangeRates.put("JPY", rates.getDouble("JPY"));
                exchangeRates.put("CNY", rates.getDouble("CNY"));
                exchangeRates.put("INR", rates.getDouble("INR"));
                exchangeRates.put("KRW", rates.getDouble("KRW"));
                exchangeRates.put("SGD", rates.getDouble("SGD"));
                exchangeRates.put("THB", rates.getDouble("THB"));
                exchangeRates.put("AUD", rates.getDouble("AUD"));
                exchangeRates.put("NZD", rates.getDouble("NZD"));
                
                System.out.println("✓ Exchange rates loaded: 1 TND = " + exchangeRates.get(userCurrency) + " " + userCurrency);
            } else {
                System.err.println("Exchange rate API failed, using default rates");
                setDefaultRates();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch exchange rates: " + e.getMessage());
            setDefaultRates();
        }
    }
    
    /**
     * Set default currency (TND) if detection fails
     */
    private static void setDefaultCurrency() {
        userCurrency = "TND";
        userCountryCode = "TN";
        setDefaultRates();
        System.out.println("ℹ️ Using default currency: TND (Tunisia)");
    }
    
    /**
     * Set default exchange rates if API fails
     */
    private static void setDefaultRates() {
        exchangeRates.put("TND", 1.0);
        exchangeRates.put("EUR", 0.31);
        exchangeRates.put("USD", 0.32);
        exchangeRates.put("GBP", 0.26);
        exchangeRates.put("MAD", 3.18);
        exchangeRates.put("DZD", 42.5);
        exchangeRates.put("SAR", 1.2);
        exchangeRates.put("AED", 1.17);
        exchangeRates.put("RON", 1.52);
        exchangeRates.put("CAD", 0.45);
        exchangeRates.put("CHF", 0.29);
        exchangeRates.put("JPY", 48.5);
        exchangeRates.put("CNY", 2.3);
        exchangeRates.put("AUD", 0.49);
    }
    
    /**
     * Convert price from TND to user's currency
     */
    public static double convertPrice(double priceInTND) {
        if (userCurrency == null) {
            detectUserCurrency();
        }
        
        Double rate = exchangeRates.get(userCurrency);
        if (rate == null) {
            return priceInTND; // Return original if rate not found
        }
        
        return priceInTND * rate;
    }
    
    /**
     * Format price with user's currency symbol
     */
    public static String formatPrice(double priceInTND) {
        double convertedPrice = convertPrice(priceInTND);
        String currencySymbol = getCurrencySymbol();
        
        return String.format("%.2f %s", convertedPrice, currencySymbol);
    }
    
    /**
     * Get currency symbol for user's currency
     */
    public static String getCurrencySymbol() {
        if (userCurrency == null) {
            detectUserCurrency();
        }
        
        switch (userCurrency) {
            case "EUR": return "€";
            case "USD": return "$";
            case "GBP": return "£";
            case "JPY": return "¥";
            case "CNY": return "¥";
            case "INR": return "₹";
            case "KRW": return "₩";
            case "RON": return "RON";
            case "CHF": return "CHF";
            case "CAD": return "C$";
            case "AUD": return "A$";
            case "TND": return "TND";
            case "MAD": return "MAD";
            case "DZD": return "DZD";
            case "SAR": return "SAR";
            case "AED": return "AED";
            default: return userCurrency;
        }
    }
    
    /**
     * Get user's currency code
     */
    public static String getUserCurrency() {
        if (userCurrency == null) {
            detectUserCurrency();
        }
        return userCurrency;
    }
    
    /**
     * Initialize currency service (call at app startup)
     */
    public static void initialize() {
        new Thread(() -> {
            detectUserCurrency();
        }).start();
    }
}
