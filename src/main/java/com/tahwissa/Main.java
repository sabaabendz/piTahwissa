package com.tahwissa;

import com.tahwissa.utils.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Tester la connexion à la base de données
            DBConnection.getConnection();
            
            // Charger l'écran de connexion
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene scene = new Scene(root, 1200, 700);
            
            primaryStage.setTitle("Tahwissa - Gestion d'Événements");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            System.out.println("✓ Application Tahwissa lancée avec succès");
            
        } catch (Exception e) {
            System.err.println("✗ Erreur lors du démarrage de l'application");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DBConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
