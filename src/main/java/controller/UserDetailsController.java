package controller;

import entities.User;
import services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import controller.user.UserFormController;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class UserDetailsController {

    @FXML private Label avatarIcon;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleBadge;
    @FXML private Label idLabel;
    @FXML private Label phoneLabel;
    @FXML private Label verifiedLabel;
    @FXML private Label activeLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label updatedAtLabel;
    @FXML private Label cityLabel;
    @FXML private Label countryLabel;

    // 👇 AJOUTE CETTE LIGNE - Le bouton closeBtn manquait!
    @FXML private Button closeBtn;

    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    private UserService userService;
    private UserListController userListController;
    private User currentUser;

    @FXML
    public void initialize() {
        userService = new UserService();
    }

    public void setUserListController(UserListController controller) {
        this.userListController = controller;
    }

    public void setUser(User user) {
        this.currentUser = user;
        displayUserDetails();
    }

    private void displayUserDetails() {
        if (currentUser == null) return;

        // Informations de base
        fullNameLabel.setText(currentUser.getFullName());
        emailLabel.setText(currentUser.getEmail());
        idLabel.setText(String.valueOf(currentUser.getId()));
        phoneLabel.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "Non renseigné");

        // Rôle avec couleur
        String role = currentUser.getRole();
        roleBadge.setText(role);

        String roleColor;
        switch (role) {
            case "ADMIN": roleColor = "#E74C3C"; break;
            case "AGENT": roleColor = "#E67E22"; break;
            default: roleColor = "#3498DB"; break;
        }
        roleBadge.setStyle("-fx-background-color: " + roleColor +
                "; -fx-text-fill: white; -fx-padding: 3 15; -fx-background-radius: 15;");

        // Statuts
        if (currentUser.isVerified()) {
            verifiedLabel.setText("✓ Vérifié");
            verifiedLabel.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-padding: 3 12; -fx-background-radius: 12;");
        } else {
            verifiedLabel.setText("✗ Non vérifié");
            verifiedLabel.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-padding: 3 12; -fx-background-radius: 12;");
        }

        if (currentUser.isActive()) {
            activeLabel.setText("● Actif");
            activeLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        } else {
            activeLabel.setText("○ Inactif");
            activeLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
        }

        // Dates
        if (currentUser.getCreatedAt() != null) {
            createdAtLabel.setText(currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
        if (currentUser.getUpdatedAt() != null) {
            updatedAtLabel.setText(currentUser.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }

        // Localisation
        cityLabel.setText(currentUser.getCity() != null ? currentUser.getCity() : "Non renseigné");
        countryLabel.setText(currentUser.getCountry() != null ? currentUser.getCountry() : "Non renseigné");
    }

    @FXML
    private void handleEdit() {
        closeDialog();
        if (userListController != null) {
            // Ouvrir le formulaire d'édition
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/view/user/user-form.fxml")
                );
                javafx.scene.Parent root = loader.load();

                UserFormController controller = loader.getController();
                controller.setUserListController(userListController);
                controller.initForEdit(currentUser);

                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
                stage.setScene(new javafx.scene.Scene(root));
                stage.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'utilisateur");
        confirm.setContentText("Voulez-vous vraiment supprimer " + currentUser.getEmail() + " ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.supprimer(currentUser.getId());
                    closeDialog();
                    if (userListController != null) {
                        userListController.refreshTable();
                    }
                    showSuccess("Utilisateur supprimé avec succès!");
                } catch (SQLException e) {
                    showError("Erreur", "Impossible de supprimer: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}