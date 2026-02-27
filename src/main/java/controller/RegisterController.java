package controller;

import entities.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.PythonBiometricService;
import services.UserService;
import utils.SessionManager;

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
        String firstName = safeText(firstNameField);
        if (firstName.isEmpty()) {
            showStatus("❌ Le prénom est obligatoire", "#EF4444");
            focus(firstNameField);
            return;
        }
        if (firstName.length() < 2 || !firstName.matches("[a-zA-ZÀ-ÿ\\s-]+")) {
            showStatus("❌ Le prénom doit contenir au moins 2 lettres", "#EF4444");
            focus(firstNameField);
            return;
        }

        String lastName = safeText(lastNameField);
        if (lastName.isEmpty()) {
            showStatus("❌ Le nom est obligatoire", "#EF4444");
            focus(lastNameField);
            return;
        }
        if (lastName.length() < 2 || !lastName.matches("[a-zA-ZÀ-ÿ\\s-]+")) {
            showStatus("❌ Le nom doit contenir au moins 2 lettres", "#EF4444");
            focus(lastNameField);
            return;
        }

        String email = safeText(emailField).toLowerCase();
        if (email.isEmpty()) {
            showStatus("❌ L'email est obligatoire", "#EF4444");
            focus(emailField);
            return;
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            showStatus("❌ Format d'email invalide", "#EF4444");
            focus(emailField);
            return;
        }

        String phone = safeText(phoneField);
        if (phone.isEmpty()) {
            showStatus("❌ Le numéro de téléphone est obligatoire", "#EF4444");
            focus(phoneField);
            return;
        }
        if (!phone.matches("^[+]?[0-9\\s-]{8,20}$")) {
            showStatus("❌ Format de téléphone invalide", "#EF4444");
            focus(phoneField);
            return;
        }

        String city = safeText(cityField);
        if (city.isEmpty()) {
            showStatus("❌ La ville est obligatoire", "#EF4444");
            focus(cityField);
            return;
        }

        String country = safeText(countryField);
        if (country.isEmpty()) {
            showStatus("❌ Le pays est obligatoire", "#EF4444");
            focus(countryField);
            return;
        }

        String password = safeText(passwordField);
        if (password.isEmpty()) {
            showStatus("❌ Le mot de passe est obligatoire", "#EF4444");
            focus(passwordField);
            return;
        }
        if (password.length() < 6 || password.length() > 50) {
            showStatus("❌ Le mot de passe doit avoir entre 6 et 50 caractères", "#EF4444");
            focus(passwordField);
            return;
        }

        String role = "Voyageur";
        if (roleChoiceBox != null && !roleChoiceBox.isDisabled()) {
            String selectedRole = roleChoiceBox.getValue();
            if (selectedRole != null && !selectedRole.isBlank()) {
                role = selectedRole;
            }
        }

        if (!biometricService.isPythonAvailable()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Python requis");
            alert.setHeaderText("Python/OpenCV indisponible");
            alert.setContentText("La vérification biométrique nécessite Python 3.x avec OpenCV.\n\n" +
                    "Détails: " + biometricService.getLastError() + "\n\n" +
                    "Voulez-vous continuer sans vérification biométrique ?");

            ButtonType btnYes = new ButtonType("Continuer sans vérification", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(btnYes, btnNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnYes) {
                    createUserAccount(firstName, lastName, email, password, phone, city, country, role);
                }
            });
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
                Platform.runLater(() -> createUserAccount(firstName, lastName, email, password, phone, city, country, role));
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
            showStatus("✅ Inscription réussie! Vous pouvez vous connecter.", "#27ae60");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(this::openLoginPage);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } catch (Exception e) {
            showStatus("❌ Erreur lors de l'inscription: " + e.getMessage(), "#e74c3c");
            e.printStackTrace();
        }
    }

    private void openLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = resolveStage();
            if (stage != null) {
                stage.setScene(new Scene(root, 700, 650));
                stage.setTitle("Tahwissa - Connexion");
                stage.show();
            }
        } catch (Exception e) {
            showStatus("❌ Erreur lors du retour à la connexion", "#e74c3c");
            e.printStackTrace();
        }
    }

    private Stage resolveStage() {
        if (registerButton != null && registerButton.getScene() != null) {
            return (Stage) registerButton.getScene().getWindow();
        }
        if (backButton != null && backButton.getScene() != null) {
            return (Stage) backButton.getScene().getWindow();
        }
        return null;
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
