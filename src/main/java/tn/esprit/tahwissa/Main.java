package tn.esprit.tahwissa;

/**
 * Legacy Main class - Redirects to MainApplication
 * This file is kept for compatibility but delegates to MainApplication
 */
public class Main {
    
    /**
     * Main entry point
     * Redirects to MainApplication which launches JavaFX
     */
    public static void main(String[] args) {
        System.out.println("🔄 Redirecting to MainApplication...");
        
        // Launch the JavaFX application
        MainApplication.main(args);
    }
}