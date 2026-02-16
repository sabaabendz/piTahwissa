package tn.esprit.tahwisa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.tahwisa.config.MyConnection;

import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            // ✅ Test connexion base de données
            MyConnection.getInstance();
            System.out.println("✅ Connexion à la base de données réussie !");

            // ✅ Vérifier que le FXML existe
            URL fxmlLocation = getClass().getResource("/fxml/MainLayout.fxml");

            if (fxmlLocation == null) {
                throw new RuntimeException("❌ Fichier FXML introuvable ! Vérifie le dossier resources.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1400, 800);

            primaryStage.setTitle("Tahwissa - Système de Gestion de Voyage");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

            System.out.println("✅ Application lancée avec succès !");

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
