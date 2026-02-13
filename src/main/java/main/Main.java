package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🚀 Démarrage de l'application...");

            // === SOLUTION 1: Chemin absolu (test rapide) ===
            String absolutePath = "C:\\Users\\mohamed\\Downloads\\workshopA6\\workshopA6\\src\\main\\java\\resources\\view\\user\\user-list.fxml";
            File fxmlFile = new File(absolutePath);

            if (fxmlFile.exists()) {
                System.out.println("✅ Fichier trouvé: " + absolutePath);
                URL url = fxmlFile.toURI().toURL();
                Parent root = FXMLLoader.load(url);

                Scene scene = new Scene(root, 1100, 750);
                primaryStage.setTitle("Tahwissa - Gestion des Utilisateurs");
                primaryStage.setScene(scene);
                primaryStage.show();
                System.out.println("✅ Application démarrée avec succès!");
            } else {
                System.err.println("❌ Fichier non trouvé: " + absolutePath);
                System.err.println("📁 Chemins à vérifier:");

                // === Vérifie tous les chemins possibles ===
                checkPath("/views/user/user-list.fxml");
                checkPath("views/user/user-list.fxml");
                checkPath("user-list.fxml");
                checkPath("/user-list.fxml");
            }

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