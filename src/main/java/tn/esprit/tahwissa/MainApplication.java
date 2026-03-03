package tn.esprit.tahwissa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.tahwissa.config.Database;
import tn.esprit.tahwissa.services.ReservationVoyageService;
import tn.esprit.tahwissa.models.ReservationVoyage;

import java.sql.SQLException;
import java.util.List;

/**
 * Main Application Entry Point
 * Tahwissa Travel Management System - Gestion Reservation Module
 */
public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        System.setProperty("javafx.platform", "desktop");

        try {
            System.out.println("=".repeat(60));
            System.out.println("🚀 Starting Tahwissa Travel Management System");
            System.out.println("=".repeat(60));

            // Test database connection
            testDatabaseConnection();


            // Setup SceneNavigator
            tn.esprit.tahwissa.utils.SceneNavigator.setMainStage(primaryStage);
            
            System.out.println("\n📂 Loading Login Screen...");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml")
            );
            Parent root = loader.load();

            System.out.println("✅ FXML loaded successfully");

            // Create scene
            Scene scene = new Scene(root, 1080, 720);

            // Set window properties
            primaryStage.setTitle("Tahwissa - Login");
            primaryStage.setScene(scene);
            // primaryStage.setMaximized(true); // Don't maximize login by default

            // Show window
            primaryStage.show();

            System.out.println("✅ Application started successfully!");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.err.println("\n❌ Error starting application:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            // Show error dialog to user
            showErrorDialog(e);
        }
    }

    /**
     * Test database connection and display results
     */
    private void testDatabaseConnection() {
        try {
            System.out.println("\n🔌 Testing Database Connection...");
            System.out.println("-".repeat(60));

            // Get database connection - this will establish connection
            Database.getInstance();
            System.out.println("✅ Database connection successful!");
            System.out.println("📊 Database: gestionreservation");
            System.out.println("🖥️  Host: localhost:3306");

            // Test data retrieval
            System.out.println("\n📋 Testing data retrieval...");
            ReservationVoyageService service = new ReservationVoyageService();
            List<ReservationVoyage> reservations = service.getAllReservations();

            System.out.println("✅ Found " + reservations.size() + " reservations in database");

            if (reservations.size() > 0) {
                System.out.println("\n📝 Sample reservation:");
                ReservationVoyage sample = reservations.get(0);
                System.out.println("   ID: " + sample.getId());
                System.out.println("   Destination: " + sample.getDestinationVoyage());
                System.out.println("   Status: " + sample.getStatut().getLabel());
                System.out.println("   Persons: " + sample.getNbrPersonnes());
                System.out.println("   Amount: " + sample.getMontantTotal() + " DT");
            } else {
                System.err.println("⚠️  Warning: No reservations found in database!");
                System.err.println("    Check if data was imported correctly:");
                System.err.println("    mysql -u root -p gestionreservation < gestionreservation.sql");
            }

            System.out.println("-".repeat(60));

        } catch (SQLException e) {
            System.err.println("\n❌ Database error: " + e.getMessage());
            System.err.println("\nTroubleshooting steps:");
            System.err.println("  1. Make sure MySQL/MariaDB is running");
            System.err.println("  2. Verify database 'gestionreservation' exists");
            System.err.println("  3. Import the SQL file if needed:");
            System.err.println("     mysql -u root -p < gestionreservation.sql");
            System.err.println("  4. Check credentials in Database.java");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show error dialog to user
     */
    private void showErrorDialog(Exception e) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error");
        alert.setHeaderText("Failed to start application");
        alert.setContentText("Error: " + e.getMessage() + "\n\nCheck console for details.");
        alert.showAndWait();
    }

    /**
     * Cleanup on application stop
     */
    @Override
    public void stop() {
        System.out.println("\n👋 Shutting down application...");

        // Close database connection
        try {
            Database.closeConnection();
            System.out.println("✅ Database connection closed");
        } catch (Exception e) {
            System.err.println("⚠️  Error closing database: " + e.getMessage());
        }

        System.out.println("✅ Application stopped successfully");
    }

    /**
     * Main method - Entry point
     */
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   TAHWISSA TRAVEL MANAGEMENT SYSTEM");
        System.out.println("   Gestion Reservation Module v1.0");
        System.out.println("=".repeat(60));

        // Launch JavaFX application
        launch(args);
    }
}