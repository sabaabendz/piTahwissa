package tests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.CurrencyService;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize currency detection
            CurrencyService.initialize();
            
            // Load Front Office by default
           // Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("/frontoffice_home.fxml"));
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setTitle("Voyage Loisir - Réservez Votre Voyage");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
            // For Back Office, change to: "/home.fxml"
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}