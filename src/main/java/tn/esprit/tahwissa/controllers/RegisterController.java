package tn.esprit.tahwissa.controllers;

import tn.esprit.tahwissa.models.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.tahwissa.services.PythonBiometricService;
import tn.esprit.tahwissa.services.UserService;
import tn.esprit.tahwissa.utils.SessionManager;
import tn.esprit.tahwissa.utils.SceneNavigator;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private Label statusLabel;
    @FXML private Button registerButton;
    @FXML private Button backButton;

    private UserService userService;
    private PythonBiometricService biometricService;

    @FXML
    public void initialize() {
        userService = new UserService();
        biometricService = new PythonBiometricService();

        if (roleChoiceBox != null && roleChoiceBox.getItems().isEmpty()) {
            roleChoiceBox.setItems(javafx.collections.FXCollections.observableArrayList("Voyageur", "Guide", "Agent"));
        }

        boolean canPickRole = isAdminSession();
        if (roleChoiceBox != null) {
            roleChoiceBox.setValue("Voyageur");
            roleChoiceBox.setDisable(!canPickRole);
        }
        if (countryField != null && (countryField.getText() == null || countryField.getText().isBlank())) {
            countryField.setText("Tunisie");
        }
    }

    private boolean isAdminSession() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null || currentUser.getRole() == null) {
                return false;
            }
            String role = currentUser.getRole().toUpperCase();
            return role.equals("ADMIN") || role.equals("AGENT");
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void handleBackToLogin() {
        openLoginPage();
    }

    @FXML
    private void handleRegister() {
        try {
            String firstName = safeText(firstNameField);
            if (firstName.isEmpty()) {
                showError("Le prénom est obligatoire");
                focus(firstNameField);
                return;
            }
            if (firstName.length() < 2) {
                showError("Le prénom doit contenir au moins 2 lettres");
                focus(firstNameField);
                return;
            }

            String lastName = safeText(lastNameField);
            if (lastName.isEmpty()) {
                showError("Le nom est obligatoire");
                focus(lastNameField);
                return;
            }
            if (lastName.length() < 2) {
                showError("Le nom doit contenir au moins 2 lettres");
                focus(lastNameField);
                return;
            }

            String email = safeText(emailField).toLowerCase();
            if (email.isEmpty()) {
                showError("L'email est obligatoire");
                focus(emailField);
                return;
            }
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                showError("Format d'email invalide");
                focus(emailField);
                return;
            }

            String phone = safeText(phoneField);
            if (phone.isEmpty()) {
                showError("Le numéro de téléphone est obligatoire");
                focus(phoneField);
                return;
            }

            String city = safeText(cityField);
            if (city.isEmpty()) {
                showError("La ville est obligatoire");
                focus(cityField);
                return;
            }

            String country = safeText(countryField);
            if (country.isEmpty()) {
                showError("Le pays est obligatoire");
                focus(countryField);
                return;
            }

            String password = safeText(passwordField);
            if (password.isEmpty()) {
                showError("Le mot de passe est obligatoire");
                focus(passwordField);
                return;
            }
            if (password.length() < 6) {
                showError("Le mot de passe doit avoir au moins 6 caractères");
                focus(passwordField);
                return;
            }

            String role = "USER";
            if (roleChoiceBox != null && !roleChoiceBox.isDisabled()) {
                String selectedRole = roleChoiceBox.getValue();
                if (selectedRole != null && !selectedRole.isBlank()) {
                    role = selectedRole.toUpperCase().equals("VOYAGEUR") ? "USER" : selectedRole.toUpperCase();
                }
            }
            final String finalRole = role;

            if (!biometricService.isPythonAvailable()) {
                System.out.println("⚠️ Biometric check skipped: Python unavailable.");
                createUserAccount(firstName, lastName, email, password, phone, city, country, finalRole);
                return;
            }

            showStatus("🎥 Lancement de la vérification biométrique...", "#4F46E5");

            Task<PythonBiometricService.VerificationResult> verificationTask = new Task<>() {
                @Override
                protected PythonBiometricService.VerificationResult call() {
                    return biometricService.verifyWithWebcam(15, null);
                }
            };

            verificationTask.setOnSucceeded(event -> {
                PythonBiometricService.VerificationResult result = verificationTask.getValue();
                if (result.isSuccess()) {
                    showStatus("✅ Visage vérifié! Création du compte...", "#10B981");
                    Platform.runLater(() -> createUserAccount(firstName, lastName, email, password, phone, city, country, finalRole));
                } else {
                    showStatus("❌ " + result.getMessage(), "#EF4444");
                    Platform.runLater(() -> showRetryAlert(result.getMessage()));
                }
            });

            verificationTask.setOnFailed(event -> {
                Throwable error = verificationTask.getException();
                String message = error != null ? error.getMessage() : "Erreur inconnue";
                showStatus("❌ Erreur lors de la vérification biométrique", "#EF4444");
                Platform.runLater(() -> showErrorAlert("Erreur lors de la vérification biométrique", message));
            });

            new Thread(verificationTask).start();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    private void createUserAccount(String firstName, String lastName, String email, String password,
                                   String phone, String city, String country, String role) {
        try {
            User newUser = new User();
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setPhone(phone);
            newUser.setCity(city);
            newUser.setCountry(country);
            newUser.setRole(role);
            newUser.setVerified(true);
            newUser.setActive(true);

            userService.ajouter(newUser);
            showStatus("✅ Inscription réussie! Redirection...", "#27ae60");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    Platform.runLater(this::openLoginPage);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } catch (Exception e) {
            showError("Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    private void openLoginPage() {
        try {
            SceneNavigator.navigate(registerButton, "/fxml/login.fxml", "Tahwissa - Connexion");
        } catch (Exception e) {
            showError("Erreur lors du retour à la connexion");
        }
    }

    private void showRetryAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Vérification échouée");
        alert.setHeaderText("La vérification biométrique a échoué");
        alert.setContentText(message + "\n\nVoulez-vous réessayer ?");

        ButtonType btnRetry = new ButtonType("Réessayer", ButtonBar.ButtonData.YES);
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(btnRetry, btnCancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnRetry) {
                handleRegister();
            }
        });
    }

    private void showErrorAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showStatus(String message, String color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }

    private static String safeText(TextInputControl control) {
        return control == null || control.getText() == null ? "" : control.getText().trim();
    }

    private static void focus(Control control) {
        if (control != null) {
            control.requestFocus();
        }
    }
}
