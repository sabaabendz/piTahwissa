package controller.user;

import entities.User;
import services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import controller.UserListController;

public class UserFormController {

    @FXML private Label formTitle;
    @FXML private Label formIcon;
    @FXML private Label formSubtitle;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private ChoiceBox<String> roleField;
    @FXML private CheckBox verifiedCheckbox;
    @FXML private CheckBox activeCheckbox;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private TextField userIdField;
    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Button closeBtn;

    private UserService userService;
    private UserListController userListController;
    private User currentUser;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        userService = new UserService();
        if (errorLabel != null) {
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
        }

        // Valeur par défaut
        if (roleField != null) {
            roleField.setValue("USER");
        }
        if (countryField != null) {
            countryField.setText("Tunisie");
        }
        if (activeCheckbox != null) {
            activeCheckbox.setSelected(true);
        }
    }

    public void setUserListController(UserListController controller) {
        this.userListController = controller;
    }

    public void initForAdd() {
        isEditMode = false;
        if (formTitle != null) {
            formTitle.setText("Ajouter un utilisateur");
        }
        if (formSubtitle != null) {
            formSubtitle.setText("Remplissez les informations du nouvel utilisateur");
        }
        if (formIcon != null) {
            formIcon.setText("➕");
        }
        if (saveBtn != null) {
            saveBtn.setText("Ajouter");
        }
        clearForm();
    }

    public void initForEdit(User user) {
        isEditMode = true;
        currentUser = user;

        if (formTitle != null) {
            formTitle.setText("Modifier l'utilisateur");
        }
        if (formSubtitle != null) {
            formSubtitle.setText("Modifiez les informations de l'utilisateur");
        }
        if (formIcon != null) {
            formIcon.setText("✏️");
        }
        if (saveBtn != null) {
            saveBtn.setText("Modifier");
        }

        if (userIdField != null) {
            userIdField.setText(String.valueOf(user.getId()));
        }
        if (emailField != null) {
            emailField.setText(user.getEmail());
        }
        if (firstNameField != null) {
            firstNameField.setText(user.getFirstName());
        }
        if (lastNameField != null) {
            lastNameField.setText(user.getLastName());
        }
        if (phoneField != null) {
            phoneField.setText(user.getPhone());
        }
        if (roleField != null) {
            roleField.setValue(user.getRole());
        }
        if (verifiedCheckbox != null) {
            verifiedCheckbox.setSelected(user.isVerified());
        }
        if (activeCheckbox != null) {
            activeCheckbox.setSelected(user.isActive());
        }
        if (cityField != null) {
            cityField.setText(user.getCity());
        }
        if (countryField != null) {
            countryField.setText(user.getCountry());
        }
        if (passwordField != null) {
            passwordField.setPromptText("Laisser vide pour conserver");
        }
    }

    @FXML
    private void handleSave() {
        // Validation
        if (emailField == null || emailField.getText().trim().isEmpty()) {
            showError("L'email est obligatoire");
            return;
        }
        if (firstNameField == null || firstNameField.getText().trim().isEmpty()) {
            showError("Le prénom est obligatoire");
            return;
        }
        if (lastNameField == null || lastNameField.getText().trim().isEmpty()) {
            showError("Le nom est obligatoire");
            return;
        }
        if (!isEditMode && (passwordField == null || passwordField.getText().trim().isEmpty())) {
            showError("Le mot de passe est obligatoire pour un nouvel utilisateur");
            return;
        }
        if (roleField == null || roleField.getValue() == null) {
            showError("Le rôle est obligatoire");
            return;
        }

        try {
            if (!isEditMode) {
                // Ajout
                User newUser = new User(
                        emailField.getText().trim(),
                        passwordField.getText().trim(),
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        roleField.getValue()
                );

                if (phoneField != null) {
                    newUser.setPhone(phoneField.getText().trim());
                }
                if (verifiedCheckbox != null) {
                    newUser.setVerified(verifiedCheckbox.isSelected());
                }
                if (activeCheckbox != null) {
                    newUser.setActive(activeCheckbox.isSelected());
                }
                if (cityField != null) {
                    newUser.setCity(cityField.getText().trim());
                }
                if (countryField != null) {
                    newUser.setCountry(countryField.getText().trim());
                }

                userService.ajouter(newUser);
                showSuccess("Utilisateur ajouté avec succès!");
            } else {
                // Modification
                if (currentUser == null) {
                    showError("Utilisateur introuvable");
                    return;
                }
                currentUser.setEmail(emailField.getText().trim());
                currentUser.setFirstName(firstNameField.getText().trim());
                currentUser.setLastName(lastNameField.getText().trim());
                if (phoneField != null) {
                    currentUser.setPhone(phoneField.getText().trim());
                }
                currentUser.setRole(roleField.getValue());
                if (verifiedCheckbox != null) {
                    currentUser.setVerified(verifiedCheckbox.isSelected());
                }
                if (activeCheckbox != null) {
                    currentUser.setActive(activeCheckbox.isSelected());
                }
                if (cityField != null) {
                    currentUser.setCity(cityField.getText().trim());
                }
                if (countryField != null) {
                    currentUser.setCountry(countryField.getText().trim());
                }

                if (passwordField != null && !passwordField.getText().trim().isEmpty()) {
                    currentUser.setPassword(passwordField.getText().trim());
                }

                userService.update(currentUser);
                showSuccess("Utilisateur modifié avec succès!");
            }

            // Rafraîchir la liste et fermer
            if (userListController != null) {
                userListController.refreshTable();
            }
            closeForm();

        } catch (SQLException e) {
            String error = e.getMessage().toLowerCase();
            if (error.contains("duplicate") || error.contains("unique")) {
                showError("Cet email est déjà utilisé");
            } else {
                showError("Erreur: " + e.getMessage());
            }
        }
    }

    @FXML
    private void closeForm() {
        Stage stage = resolveStage();
        if (stage != null) {
            stage.close();
        }
    }

    private Stage resolveStage() {
        if (saveBtn != null && saveBtn.getScene() != null) {
            return (Stage) saveBtn.getScene().getWindow();
        }
        if (cancelBtn != null && cancelBtn.getScene() != null) {
            return (Stage) cancelBtn.getScene().getWindow();
        }
        if (closeBtn != null && closeBtn.getScene() != null) {
            return (Stage) closeBtn.getScene().getWindow();
        }
        return null;
    }

    private void clearForm() {
        if (emailField != null) {
            emailField.clear();
        }
        if (passwordField != null) {
            passwordField.clear();
        }
        if (firstNameField != null) {
            firstNameField.clear();
        }
        if (lastNameField != null) {
            lastNameField.clear();
        }
        if (phoneField != null) {
            phoneField.clear();
        }
        if (roleField != null) {
            roleField.setValue("USER");
        }
        if (verifiedCheckbox != null) {
            verifiedCheckbox.setSelected(false);
        }
        if (activeCheckbox != null) {
            activeCheckbox.setSelected(true);
        }
        if (cityField != null) {
            cityField.clear();
        }
        if (countryField != null) {
            countryField.setText("Tunisie");
        }
        if (userIdField != null) {
            userIdField.clear();
        }
        if (errorLabel != null) {
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
        }
    }

    private void showError(String message) {
        if (errorLabel == null) {
            return;
        }
        errorLabel.setText("❌ " + message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #E74C3C;");
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
