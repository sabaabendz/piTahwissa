package controller.user;

import entities.User;
import services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

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
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        // Valeur par défaut
        roleField.setValue("USER");
        countryField.setText("Tunisie");
        activeCheckbox.setSelected(true);
    }

    public void setUserListController(UserListController controller) {
        this.userListController = controller;
    }

    public void initForAdd() {
        isEditMode = false;
        formTitle.setText("Ajouter un utilisateur");
        formSubtitle.setText("Remplissez les informations du nouvel utilisateur");
        formIcon.setText("➕");
        saveBtn.setText("Ajouter");
        clearForm();
    }

    public void initForEdit(User user) {
        isEditMode = true;
        currentUser = user;

        formTitle.setText("Modifier l'utilisateur");
        formSubtitle.setText("Modifiez les informations de l'utilisateur");
        formIcon.setText("✏️");
        saveBtn.setText("Modifier");

        userIdField.setText(String.valueOf(user.getId()));
        emailField.setText(user.getEmail());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        phoneField.setText(user.getPhone());
        roleField.setValue(user.getRole());
        verifiedCheckbox.setSelected(user.isVerified());
        activeCheckbox.setSelected(user.isActive());
        cityField.setText(user.getCity());
        countryField.setText(user.getCountry());
        passwordField.setPromptText("Laisser vide pour conserver");
    }

    @FXML
    private void handleSave() {
        // Validation
        if (emailField.getText().trim().isEmpty()) {
            showError("L'email est obligatoire");
            return;
        }
        if (firstNameField.getText().trim().isEmpty()) {
            showError("Le prénom est obligatoire");
            return;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Le nom est obligatoire");
            return;
        }
        if (!isEditMode && passwordField.getText().trim().isEmpty()) {
            showError("Le mot de passe est obligatoire pour un nouvel utilisateur");
            return;
        }
        if (roleField.getValue() == null) {
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

                newUser.setPhone(phoneField.getText().trim());
                newUser.setVerified(verifiedCheckbox.isSelected());
                newUser.setActive(activeCheckbox.isSelected());
                newUser.setCity(cityField.getText().trim());
                newUser.setCountry(countryField.getText().trim());

                userService.ajouter(newUser);
                showSuccess("Utilisateur ajouté avec succès!");
            } else {
                // Modification
                currentUser.setEmail(emailField.getText().trim());
                currentUser.setFirstName(firstNameField.getText().trim());
                currentUser.setLastName(lastNameField.getText().trim());
                currentUser.setPhone(phoneField.getText().trim());
                currentUser.setRole(roleField.getValue());
                currentUser.setVerified(verifiedCheckbox.isSelected());
                currentUser.setActive(activeCheckbox.isSelected());
                currentUser.setCity(cityField.getText().trim());
                currentUser.setCountry(countryField.getText().trim());

                if (!passwordField.getText().trim().isEmpty()) {
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
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        emailField.clear();
        passwordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        roleField.setValue("USER");
        verifiedCheckbox.setSelected(false);
        activeCheckbox.setSelected(true);
        cityField.clear();
        countryField.setText("Tunisie");
        userIdField.clear();
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
    }

    private void showError(String message) {
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