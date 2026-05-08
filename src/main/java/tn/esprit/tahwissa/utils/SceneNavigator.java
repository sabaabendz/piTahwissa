package tn.esprit.tahwissa.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.io.IOException;

public class SceneNavigator {

    private static Stage mainStage;

    public static void setMainStage(Stage stage) {
        SceneNavigator.mainStage = stage;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void navigate(String fxmlPath, String title) {
        navigate(null, fxmlPath, title);
    }

    public static void navigate(javafx.scene.Node node, String fxmlPath, String title) {
        try {
            System.out.println("🔄 Navigating to: " + fxmlPath);
            URL res = SceneNavigator.class.getResource(fxmlPath);
            if (res == null) {
                System.err.println("❌ FXML path not found: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(res);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            
            Stage stage = mainStage;
            if (stage == null && node != null) {
                stage = (Stage) node.getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle(title);
                stage.centerOnScreen();
                // stage.setMaximized(true); // Maybe not by default
                stage.show();
                System.out.println("✅ Successfully loaded " + fxmlPath);
            } else {
                System.err.println("❌ SceneNavigator: Stage is NOT found!");
            }
        } catch (IOException e) {
            System.err.println("❌ Error navigating to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
