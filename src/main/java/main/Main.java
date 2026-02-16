package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🚀 Démarrage de l'application...");

            URL resource = getClass().getResource("/view/login.fxml");
            if (resource == null) {
                System.err.println("❌ Fichier non trouvé: /view/login.fxml");
                System.err.println("📁 Chemins à vérifier:");

                checkPath("/view/login.fxml");
                checkPath("view/login.fxml");
                checkPath("/view/user/user-list.fxml");
                checkPath("view/user/user-list.fxml");
                return;
            }

            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root, 700, 650);
            primaryStage.setTitle("Tahwissa - Connexion");
            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("✅ Application démarrée avec succès!");

        } catch (Exception e) {
            System.err.println("❌ Erreur de démarrage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkPath(String path) {
        URL resource = getClass().getResource(path);
        if (resource != null) {
            System.out.println("   ✅ Chemin trouvé: " + path + " → " + resource);
        } else {
            System.out.println("   ❌ Chemin introuvable: " + path);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}