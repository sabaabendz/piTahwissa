package tn.esprit.tahwissa.controllers;

import tn.esprit.tahwissa.services.UserService;
import tn.esprit.tahwissa.services.PythonBiometricService;
import tn.esprit.tahwissa.services.MailService;
import tn.esprit.tahwissa.services.PasswordResetService;
import tn.esprit.tahwissa.utils.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.tahwissa.models.User;
import tn.esprit.tahwissa.utils.SceneNavigator;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Label statusLabel;
    @FXML private Label dbStatusLabel;
    @FXML private VBox registerForm;
    @FXML private VBox loginSection;
    @FXML private Button loginButton;
    private Button registerButton;

    private UserService userService;
    private PythonBiometricService biometricService;
    private MailService mailService;
    private PasswordResetService passwordResetService;
    // private boolean biometricVerified = false;

    @FXML
    public void initialize() {
        System.out.println("âœ… LoginController initialisÃ©");
        userService = new UserService();
        biometricService = new PythonBiometricService();
        mailService = new MailService();
        passwordResetService = new PasswordResetService();

        // Init choix de rÃ´le
        if (roleChoiceBox != null && roleChoiceBox.getItems().isEmpty()) {
            roleChoiceBox.setItems(javafx.collections.FXCollections.observableArrayList("Voyageur", "Guide", "Agent"));
        }
        if (roleChoiceBox != null) {
            roleChoiceBox.setValue("Voyageur");
        }

        // Tester la connexion DB
        testDatabaseConnection();
    }

    @FXML
    private void handleLogin() {
        System.out.println("ðŸ” Tentative de connexion...");

        String email = emailField != null ? emailField.getText().trim() : "";
        String password = passwordField != null ? passwordField.getText().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("âŒ Champs vides");
            showStatus("âŒ Veuillez remplir tous les champs", "#e74c3c");
            return;
        }
        if (!email.contains("@")) {
            System.out.println("âŒ Email invalide: " + email);
            showStatus("âŒ Email invalide", "#e74c3c");
            return;
        }

        try {
            System.out.println("ðŸ” Recherche de l'utilisateur: " + email);
            User user = userService.findByEmail(email);

            if (user == null) {
                System.out.println("âŒ Utilisateur introuvable");
                showStatus("âŒ Email ou mot de passe incorrect", "#e74c3c");
                return;
            }

            System.out.println("âœ… Utilisateur trouvÃ©: " + user.getFirstName() + " " + user.getLastName());
            System.out.println("   RÃ´le: " + user.getRole());
            System.out.println("   VÃ©rifiÃ©: " + user.isVerified());
            System.out.println("   Actif: " + user.isActive());

            if (user.getPassword() == null || !user.getPassword().equals(password)) {
                System.out.println("âŒ Mot de passe incorrect");
                showStatus("âŒ Email ou mot de passe incorrect", "#e74c3c");
                return;
            }

            String role = user.getRole() != null ? user.getRole().toUpperCase() : "";
            System.out.println("ðŸ” VÃ©rification du rÃ´le: " + role);

            System.out.println("âœ… Authentification rÃ©ussie!");
            showStatus("âœ… Connexion rÃ©ussie! Bienvenue " + user.getFirstName(), "#27ae60");

            // Petit dÃ©lai pour que l'utilisateur voie le message
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            // Redirection pour tous les rôles vers le MainLayoutWithTabs
                            System.out.println("🔑 Rôle " + role + " → Dashboard");
                            openDashboard(role, user);
                        } catch (Exception e) {
                            System.err.println("âŒ Erreur lors de l'ouverture de la page: " + e.getMessage());
                            e.printStackTrace();
                            showStatus("âŒ Erreur lors de l'ouverture de la page", "#e74c3c");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            System.err.println("âŒ Erreur lors de la connexion:");
            System.err.println("   Type: " + e.getClass().getName());
            System.err.println("   Message: " + e.getMessage());
            showStatus("âŒ Erreur: " + e.getMessage(), "#e74c3c");
            e.printStackTrace();
        }
    }

    private void openDashboard(String role, User user) throws java.io.IOException {
        System.out.println("📂 Chargement de l'espace principal...");

        // Enregistrer l'utilisateur dans la session
        SessionManager.getInstance().setCurrentUser(user);

        // Clients get their own dedicated layout (no sidebar)
        if ("USER".equals(role) || "CLIENT".equals(role)) {
            tn.esprit.tahwissa.utils.SceneNavigator.navigate(
                "/fxml/client/ClientLayout.fxml",
                "Tahwissa – Espace Client"
            );
        } else {
            // Admin and Agent use the sidebar layout
            tn.esprit.tahwissa.utils.SceneNavigator.navigate(
                "/fxml/MainLayoutWithTabs.fxml",
                "Tahwissa - " + user.getFirstName() + " (" + role + ")"
            );
        }
    }

    private javafx.stage.Stage resolveStage() {
        if (loginButton != null && loginButton.getScene() != null) {
            return (javafx.stage.Stage) loginButton.getScene().getWindow();
        }
        if (registerButton != null && registerButton.getScene() != null) {
            return (javafx.stage.Stage) registerButton.getScene().getWindow();
        }
        return null;
    }

    @FXML
    private void handleRegisterToggle() {
        openRegisterPage();
    }

    @FXML
    private void handleOpenRegister() {
        openRegisterPage();
    }

    private void openRegisterPage() {
        try {
            SceneNavigator.navigate(loginButton, "/fxml/register.fxml", "Tahwissa - Inscription");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture de la page d'inscription: " + e.getMessage());
            e.printStackTrace();
            showStatus("❌ Erreur lors de l'ouverture de la page d'inscription", "#e74c3c");
        }
    }

    @FXML
    private void handleRegister() {
        System.out.println("ðŸ” DEBUG - handleRegister() appelÃ©e");

        // â•â•â•â•â•â•â•â•â•â•â• VALIDATION DES CHAMPS OBLIGATOIRES â•â•â•â•â•â•â•â•â•â•â•

        // Validation PrÃ©nom
        if (firstNameField == null || firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            System.out.println(" Prenom manquant");
            showStatus("Le prenom est obligatoire", "#EF4444");
            if (firstNameField != null) firstNameField.requestFocus();
            return;
        }

        String firstName = firstNameField.getText().trim();
        if (firstName.length() < 2) {
            showStatus(" Le prenom doit contenir au moins 2 caractÃ¨res", "#EF4444");
            firstNameField.requestFocus();
            return;
        }
        if (!firstName.matches("[a-zA-ZÃ€-Ã¿\\s-]+")) {
            showStatus("âŒ Le prÃ©nom ne doit contenir que des lettres", "#EF4444");
            firstNameField.requestFocus();
            return;
        }

        // Validation Nom
        if (lastNameField == null || lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            System.out.println("âŒ Nom manquant");
            showStatus("âŒ Le nom est obligatoire", "#EF4444");
            if (lastNameField != null) lastNameField.requestFocus();
            return;
        }

        String lastName = lastNameField.getText().trim();
        if (lastName.length() < 2) {
            showStatus("âŒ Le nom doit contenir au moins 2 caractÃ¨res", "#EF4444");
            lastNameField.requestFocus();
            return;
        }
        if (!lastName.matches("[a-zA-ZÃ€-Ã¿\\s-]+")) {
            showStatus("âŒ Le nom ne doit contenir que des lettres", "#EF4444");
            lastNameField.requestFocus();
            return;
        }

        // Validation Email
        if (registerEmailField == null || registerEmailField.getText() == null || registerEmailField.getText().trim().isEmpty()) {
            System.out.println("âŒ Email manquant");
            showStatus("âŒ L'email est obligatoire", "#EF4444");
            if (registerEmailField != null) registerEmailField.requestFocus();
            return;
        }

        String email = registerEmailField.getText().trim().toLowerCase();
        // Regex email stricte
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("âŒ Email invalide: " + email);
            showStatus("âŒ Format d'email invalide (ex: nom@domaine.com)", "#EF4444");
            registerEmailField.requestFocus();
            return;
        }

        // Validation TÃ©lÃ©phone
        if (phoneNumberField == null || phoneNumberField.getText() == null || phoneNumberField.getText().trim().isEmpty()) {
            System.out.println("âŒ TÃ©lÃ©phone manquant");
            showStatus("âŒ Le numÃ©ro de tÃ©lÃ©phone est obligatoire", "#EF4444");
            if (phoneNumberField != null) phoneNumberField.requestFocus();
            return;
        }

        String phone = phoneNumberField.getText().trim();
        if (!phone.matches("^[+]?[0-9\\s-]{8,20}$")) {
            showStatus("âŒ Format de tÃ©lÃ©phone invalide (ex: +216 XX XXX XXX)", "#EF4444");
            phoneNumberField.requestFocus();
            return;
        }

        // Validation Ville
        if (cityField == null || cityField.getText() == null || cityField.getText().trim().isEmpty()) {
            System.out.println("âŒ Ville manquante");
            showStatus("âŒ La ville est obligatoire", "#EF4444");
            if (cityField != null) cityField.requestFocus();
            return;
        }

        String city = cityField.getText().trim();
        if (city.length() < 2) {
            showStatus("âŒ Le nom de la ville doit contenir au moins 2 caractÃ¨res", "#EF4444");
            cityField.requestFocus();
            return;
        }

        // Validation Pays
        if (countryField == null || countryField.getText() == null || countryField.getText().trim().isEmpty()) {
            System.out.println("âŒ Pays manquant");
            showStatus("âŒ Le pays est obligatoire", "#EF4444");
            if (countryField != null) countryField.requestFocus();
            return;
        }

        String country = countryField.getText().trim();
        if (country.length() < 2) {
            showStatus("âŒ Le nom du pays doit contenir au moins 2 caractÃ¨res", "#EF4444");
            countryField.requestFocus();
            return;
        }

        // Validation Mot de passe
        if (registerPasswordField == null || registerPasswordField.getText() == null || registerPasswordField.getText().trim().isEmpty()) {
            System.out.println("âŒ Mot de passe manquant");
            showStatus("âŒ Le mot de passe est obligatoire", "#EF4444");
            if (registerPasswordField != null) registerPasswordField.requestFocus();
            return;
        }

        String password = registerPasswordField.getText().trim();
        if (password.length() < 6) {
            showStatus("âŒ Le mot de passe doit contenir au moins 6 caractÃ¨res", "#EF4444");
            registerPasswordField.requestFocus();
            return;
        }
        if (password.length() > 50) {
            showStatus("âŒ Le mot de passe est trop long (max 50 caractÃ¨res)", "#EF4444");
            registerPasswordField.requestFocus();
            return;
        }

        // Validation RÃ´le
        if (roleChoiceBox == null || roleChoiceBox.getValue() == null || roleChoiceBox.getValue().trim().isEmpty()) {
            System.out.println("âŒ RÃ´le manquant");
            showStatus("âŒ Veuillez sÃ©lectionner un rÃ´le", "#EF4444");
            if (roleChoiceBox != null) roleChoiceBox.requestFocus();
            return;
        }

        // â•â•â•â•â•â•â•â•â•â•â• TOUTES LES VALIDATIONS RÃ‰USSIES â•â•â•â•â•â•â•â•â•â•â•
        System.out.println("âœ… Toutes les validations passÃ©es avec succÃ¨s");
        System.out.println("ðŸ“ DonnÃ©es saisies:");
        System.out.println("  - PrÃ©nom: " + firstName);
        System.out.println("  - Nom: " + lastName);
        System.out.println("  - Email: " + email);
        System.out.println("  - TÃ©lÃ©phone: " + phone);
        System.out.println("  - Ville: " + city);
        System.out.println("  - Pays: " + country);
        System.out.println("  - RÃ´le: " + roleChoiceBox.getValue());
        System.out.println("  - Mot de passe: " + password.length() + " caractÃ¨res");

        // â•â•â•â•â•â•â•â•â•â•â• VÃ‰RIFICATION BIOMÃ‰TRIQUE PYTHON â•â•â•â•â•â•â•â•â•â•â•
        System.out.println("ðŸ” DÃ©marrage de la vÃ©rification biomÃ©trique...");

        // VÃ©rifier que Python est disponible
        if (!biometricService.isPythonAvailable()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Python requis");
            alert.setHeaderText("Python/OpenCV indisponible");
            alert.setContentText("La vÃ©rification biomÃ©trique nÃ©cessite Python 3.x avec OpenCV.\n\n" +
                               "DÃ©tails: " + biometricService.getLastError() + "\n\n" +
                               "Voulez-vous continuer sans vÃ©rification biomÃ©trique ?");

            ButtonType btnYes = new ButtonType("Continuer sans vÃ©rification", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(btnYes, btnNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnYes) {
                    createUserAccount(firstName, lastName, email, password, phone, city, country, roleChoiceBox.getValue());
                }
            });
            return;
        }

        // Afficher un message d'attente
        showStatus("ðŸŽ¥ Lancement de la vÃ©rification biomÃ©trique...", "#4F46E5");

        final String finalFirstName = firstName;
        final String finalLastName = lastName;
        final String finalEmail = email;
        final String finalPassword = password;
        final String finalPhone = phone;
        final String finalCity = city;
        final String finalCountry = country;
        final String finalRole = roleChoiceBox.getValue();

        Task<PythonBiometricService.VerificationResult> verificationTask =
            new Task<PythonBiometricService.VerificationResult>() {
            @Override
            protected PythonBiometricService.VerificationResult call() {
                return biometricService.verifyWithWebcam(15, null);
            }
        };

        verificationTask.setOnSucceeded(event -> {
            PythonBiometricService.VerificationResult result = verificationTask.getValue();

            System.out.println("ðŸ“Š RÃ©sultat de la vÃ©rification: " + result);

            if (result.isSuccess()) {
                System.out.println("âœ… VÃ©rification biomÃ©trique rÃ©ussie!");
                showStatus("âœ… Visage vÃ©rifiÃ©! CrÃ©ation du compte...", "#10B981");

                Platform.runLater(() -> {
                    createUserAccount(finalFirstName, finalLastName, finalEmail, finalPassword,
                                    finalPhone, finalCity, finalCountry, finalRole);
                });
            } else {
                System.out.println("âŒ VÃ©rification biomÃ©trique Ã©chouÃ©e: " + result.getMessage());
                showStatus("âŒ " + result.getMessage(), "#EF4444");

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("VÃ©rification Ã©chouÃ©e");
                    alert.setHeaderText("La vÃ©rification biomÃ©trique a Ã©chouÃ©");
                    alert.setContentText(result.getMessage() + "\n\nVoulez-vous rÃ©essayer ?");

                    ButtonType btnRetry = new ButtonType("RÃ©essayer", ButtonBar.ButtonData.YES);
                    ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
                    alert.getButtonTypes().setAll(btnRetry, btnCancel);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == btnRetry) {
                            handleRegister();
                        }
                    });
                });
            }
        });

        verificationTask.setOnFailed(event -> {
            Throwable error = verificationTask.getException();
            System.err.println("âŒ Erreur lors de la vÃ©rification: " + error.getMessage());
            error.printStackTrace();

            showStatus("âŒ Erreur lors de la vÃ©rification biomÃ©trique", "#EF4444");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de la vÃ©rification biomÃ©trique");
                alert.setContentText(error.getMessage());
                alert.showAndWait();
            });
        });

        new Thread(verificationTask).start();
        return;

        // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
        // âš ï¸ CRÃ‰ATION DIRECTE DU COMPTE (SANS VÃ‰RIFICATION BIOMÃ‰TRIQUE)
        // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

        // CrÃ©er le compte directement
        // createUserAccount(firstName, lastName, email, password, phone, city, country, roleChoiceBox.getValue());
    }

    /**
     * CrÃ©e le compte utilisateur aprÃ¨s vÃ©rification biomÃ©trique
     */
    private void createUserAccount(String firstName, String lastName, String email, String password,
                                   String phone, String city, String country, String role) {
        try {
            System.out.println("ðŸ”„ CrÃ©ation de l'utilisateur aprÃ¨s vÃ©rification biomÃ©trique...");

            // CrÃ©er un nouvel utilisateur
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

            System.out.println("âœ… Objet User crÃ©Ã©: " + newUser);
            System.out.println("ðŸ”„ Appel de userService.ajouter()...");

            // Ajouter l'utilisateur Ã  la base de donnÃ©es
            userService.ajouter(newUser);

            System.out.println("âœ… Utilisateur ajoutÃ© avec succÃ¨s dans la base de donnÃ©es!");
            showStatus("âœ… Inscription rÃ©ussie! Votre identitÃ© a Ã©tÃ© vÃ©rifiÃ©e. Vous pouvez vous connecter.", "#27ae60");

            // âš ï¸ COMMENTÃ‰ - Flag biomÃ©trique non utilisÃ© actuellement
            // biometricVerified = false;

            // Revenir au mode connexion aprÃ¨s 3 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        System.out.println("ðŸ”„ Retour au mode connexion...");
                        handleRegisterToggle();
                    });
                } catch (InterruptedException e) {
                    System.err.println("âŒ Erreur dans le thread: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("âŒ ERREUR lors de l'inscription:");
            System.err.println("   Type: " + e.getClass().getName());
            System.err.println("   Message: " + e.getMessage());
            showStatus("âŒ Erreur lors de l'inscription: " + e.getMessage(), "#e74c3c");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        String email = emailField != null ? emailField.getText().trim() : "";
        if (email.isEmpty()) {
            showStatus("Veuillez saisir votre email pour la reinitialisation", "#e67e22");
            return;
        }

        showStatus("⏳ Envoi du code de reinitialisation...", "#3498db");

        Task<Boolean> sendTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                User user = userService.findByEmail(email);
                if (user == null) return false;
                PasswordResetService.ResetToken token = passwordResetService.createResetToken(email);
                String fullName = user.getFirstName() + " " + user.getLastName();
                mailService.sendPasswordResetEmail(email, fullName.trim(), token.getCode());
                return true;
            }
        };

        sendTask.setOnSucceeded(event -> {
            boolean userFound = sendTask.getValue();
            if (!userFound) {
                showStatus("Si un compte existe, un email de reinitialisation a ete envoye.", "#3498db");
                return;
            }
            showStatus("✅ Code envoye! Verifiez votre email.", "#27ae60");
            Platform.runLater(() -> showResetCodeDialog(email));
        });

        sendTask.setOnFailed(event -> {
            Throwable error = sendTask.getException();
            String message = error != null ? error.getMessage() : "Erreur inconnue";
            showStatus("Erreur envoi email: " + message, "#e74c3c");
        });

        new Thread(sendTask).start();
    }

    private void showResetCodeDialog(String email) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reinitialisation du mot de passe");
        dialog.setHeaderText("Entrez le code recu par email et votre nouveau mot de passe");

        ButtonType confirmBtn = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBtn, cancelBtn);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(20));

        Label codeLabel = new Label("Code de verification (recu par email):");
        TextField codeField = new TextField();
        codeField.setPromptText("Exemple: 123456");

        Label newPassLabel = new Label("Nouveau mot de passe:");
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Minimum 6 caracteres");

        Label confirmPassLabel = new Label("Confirmer le mot de passe:");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Repetez le mot de passe");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");

        content.getChildren().addAll(codeLabel, codeField, newPassLabel, newPassField,
                confirmPassLabel, confirmPassField, errorLabel);
        dialog.getDialogPane().setContent(content);

        javafx.scene.Node confirmButton = dialog.getDialogPane().lookupButton(confirmBtn);
        confirmButton.setDisable(true);
        codeField.textProperty().addListener((obs, o, n) ->
                confirmButton.setDisable(n.trim().isEmpty() || newPassField.getText().trim().isEmpty()));
        newPassField.textProperty().addListener((obs, o, n) ->
                confirmButton.setDisable(n.trim().isEmpty() || codeField.getText().trim().isEmpty()));

        dialog.showAndWait().ifPresent(result -> {
            if (result != confirmBtn) return;

            String enteredCode = codeField.getText().trim();
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();

            if (newPass.length() < 6) {
                showStatus("Le mot de passe doit contenir au moins 6 caracteres", "#e74c3c");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                showStatus("Les mots de passe ne correspondent pas", "#e74c3c");
                return;
            }

            PasswordResetService.ResetToken token = passwordResetService.getToken(email);
            if (token == null || !token.getCode().equals(enteredCode)) {
                showStatus("Code de verification incorrect", "#e74c3c");
                return;
            }
            if (java.time.Instant.now().isAfter(token.getExpiresAt())) {
                showStatus("Le code a expire. Veuillez recommencer.", "#e74c3c");
                return;
            }

            try {
                User user = userService.findByEmail(email);
                if (user != null) {
                    userService.updatePassword(user.getId(), newPass);
                    showStatus("✅ Mot de passe reinitialise! Vous pouvez vous connecter.", "#27ae60");
                }
            } catch (Exception e) {
                showStatus("Erreur lors de la mise a jour: " + e.getMessage(), "#e74c3c");
            }
        });
    }


    private void testDatabaseConnection() {
        if (dbStatusLabel != null) {
            try {
                userService.read(); // simple ping
                dbStatusLabel.setText("ðŸŸ¢ ConnectÃ© Ã  pidev");
                dbStatusLabel.setStyle("-fx-text-fill: #27ae60;");
            } catch (Exception e) {
                dbStatusLabel.setText("ðŸ”´ Erreur de connexion");
                dbStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        }
    }

    private void showStatus(String message, String color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }

    private void clearRegisterForm() {
        if (firstNameField != null) firstNameField.clear();
        if (lastNameField != null) lastNameField.clear();
        if (registerEmailField != null) registerEmailField.clear();
        if (registerPasswordField != null) registerPasswordField.clear();
        if (phoneNumberField != null) phoneNumberField.clear();
        if (cityField != null) cityField.clear();
        if (countryField != null) countryField.clear();
        if (roleChoiceBox != null) roleChoiceBox.setValue("Voyageur");
    }
}

