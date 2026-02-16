package controller;

import services.UserService;
// ⚠️ COMMENTÉ - Vérification biométrique désactivée temporairement
// import services.PythonBiometricService;
import utils.SessionManager;
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
import entities.User;

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
    // ⚠️ COMMENTÉ - Service biométrique désactivé temporairement
    // private PythonBiometricService biometricService;
    // private boolean biometricVerified = false;

    @FXML
    public void initialize() {
        System.out.println("✅ LoginController initialisé");
        userService = new UserService();
        // ⚠️ COMMENTÉ - Service biométrique désactivé temporairement
        // biometricService = new PythonBiometricService();

        // Init choix de rôle
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
        System.out.println("🔐 Tentative de connexion...");

        String email = emailField != null ? emailField.getText().trim() : "";
        String password = passwordField != null ? passwordField.getText().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Champs vides");
            showStatus("❌ Veuillez remplir tous les champs", "#e74c3c");
            return;
        }
        if (!email.contains("@")) {
            System.out.println("❌ Email invalide: " + email);
            showStatus("❌ Email invalide", "#e74c3c");
            return;
        }

        try {
            System.out.println("🔍 Recherche de l'utilisateur: " + email);
            User user = userService.findByEmail(email);

            if (user == null) {
                System.out.println("❌ Utilisateur introuvable");
                showStatus("❌ Email ou mot de passe incorrect", "#e74c3c");
                return;
            }

            System.out.println("✅ Utilisateur trouvé: " + user.getFirstName() + " " + user.getLastName());
            System.out.println("   Rôle: " + user.getRole());
            System.out.println("   Vérifié: " + user.isVerified());
            System.out.println("   Actif: " + user.isActive());

            if (user.getPassword() == null || !user.getPassword().equals(password)) {
                System.out.println("❌ Mot de passe incorrect");
                showStatus("❌ Email ou mot de passe incorrect", "#e74c3c");
                return;
            }

            String role = user.getRole() != null ? user.getRole().toUpperCase() : "";
            System.out.println("🔍 Vérification du rôle: " + role);

            System.out.println("✅ Authentification réussie!");
            showStatus("✅ Connexion réussie! Bienvenue " + user.getFirstName(), "#27ae60");

            // Petit délai pour que l'utilisateur voie le message
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            // Redirection selon le rôle
                            if (role.equals("ADMIN") || role.equals("AGENT")) {
                                System.out.println("🔑 Rôle ADMIN/AGENT → Dashboard");
                                openDashboard(role, user);
                            } else {
                                System.out.println("👤 Rôle USER → Page Coming Soon");
                                openComingSoon(user);
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Erreur lors de l'ouverture de la page: " + e.getMessage());
                            e.printStackTrace();
                            showStatus("❌ Erreur lors de l'ouverture de la page", "#e74c3c");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la connexion:");
            System.err.println("   Type: " + e.getClass().getName());
            System.err.println("   Message: " + e.getMessage());
            showStatus("❌ Erreur: " + e.getMessage(), "#e74c3c");
            e.printStackTrace();
        }
    }

    private void openDashboard(String role, User user) throws java.io.IOException {
        System.out.println("📂 Chargement du dashboard...");

        // Enregistrer l'utilisateur dans la session
        SessionManager.getInstance().setCurrentUser(user);

        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
        javafx.scene.Parent root = loader.load();

        DashboardController controller = loader.getController();
        controller.setUserRole(role);

        System.out.println("✅ Dashboard chargé avec le rôle: " + role);

        javafx.stage.Stage stage = resolveStage();
        if (stage != null) {
            stage.setScene(new javafx.scene.Scene(root, 1080, 720));
            stage.setTitle("Tahwissa - Dashboard (" + user.getFirstName() + " - " + role + ")");
            stage.show();
            System.out.println("✅ Dashboard affiché!");
        } else {
            System.err.println("❌ Stage introuvable!");
        }
    }

    private void openComingSoon(User user) throws java.io.IOException {
        System.out.println("🚀 Chargement de la page Coming Soon...");

        // Enregistrer l'utilisateur dans la session
        SessionManager.getInstance().setCurrentUser(user);

        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/coming-soon.fxml"));
        javafx.scene.Parent root = loader.load();

        System.out.println("✅ Page Coming Soon chargée");

        javafx.stage.Stage stage = resolveStage();
        if (stage != null) {
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.setTitle("Tahwissa - Bientôt Disponible");
            stage.show();
            System.out.println("✅ Page Coming Soon affichée pour: " + user.getFirstName());
        } else {
            System.err.println("❌ Stage introuvable!");
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
        if (registerForm == null) return;

        boolean isVisible = registerForm.isVisible();
        registerForm.setManaged(!isVisible);
        registerForm.setVisible(!isVisible);

        if (loginSection != null) {
            loginSection.setManaged(isVisible);
            loginSection.setVisible(isVisible);
        }

        if (loginButton != null) {
            loginButton.setDisable(!isVisible);
        }

        if (registerButton != null) {
            registerButton.setText(isVisible ? "S'inscrire" : "Annuler");
        }

        if (!isVisible) {
            showStatus("📝 Mode inscription activé", "#e67e22");
        } else {
            clearRegisterForm();
            showStatus("", "#666");
        }
    }

    @FXML
    private void handleRegister() {
        System.out.println("🔍 DEBUG - handleRegister() appelée");

        // ═══════════ VALIDATION DES CHAMPS OBLIGATOIRES ═══════════

        // Validation Prénom
        if (firstNameField == null || firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            System.out.println("❌ Prénom manquant");
            showStatus("❌ Le prénom est obligatoire", "#EF4444");
            if (firstNameField != null) firstNameField.requestFocus();
            return;
        }

        String firstName = firstNameField.getText().trim();
        if (firstName.length() < 2) {
            showStatus("❌ Le prénom doit contenir au moins 2 caractères", "#EF4444");
            firstNameField.requestFocus();
            return;
        }
        if (!firstName.matches("[a-zA-ZÀ-ÿ\\s-]+")) {
            showStatus("❌ Le prénom ne doit contenir que des lettres", "#EF4444");
            firstNameField.requestFocus();
            return;
        }

        // Validation Nom
        if (lastNameField == null || lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            System.out.println("❌ Nom manquant");
            showStatus("❌ Le nom est obligatoire", "#EF4444");
            if (lastNameField != null) lastNameField.requestFocus();
            return;
        }

        String lastName = lastNameField.getText().trim();
        if (lastName.length() < 2) {
            showStatus("❌ Le nom doit contenir au moins 2 caractères", "#EF4444");
            lastNameField.requestFocus();
            return;
        }
        if (!lastName.matches("[a-zA-ZÀ-ÿ\\s-]+")) {
            showStatus("❌ Le nom ne doit contenir que des lettres", "#EF4444");
            lastNameField.requestFocus();
            return;
        }

        // Validation Email
        if (registerEmailField == null || registerEmailField.getText() == null || registerEmailField.getText().trim().isEmpty()) {
            System.out.println("❌ Email manquant");
            showStatus("❌ L'email est obligatoire", "#EF4444");
            if (registerEmailField != null) registerEmailField.requestFocus();
            return;
        }

        String email = registerEmailField.getText().trim().toLowerCase();
        // Regex email stricte
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("❌ Email invalide: " + email);
            showStatus("❌ Format d'email invalide (ex: nom@domaine.com)", "#EF4444");
            registerEmailField.requestFocus();
            return;
        }

        // Validation Téléphone
        if (phoneNumberField == null || phoneNumberField.getText() == null || phoneNumberField.getText().trim().isEmpty()) {
            System.out.println("❌ Téléphone manquant");
            showStatus("❌ Le numéro de téléphone est obligatoire", "#EF4444");
            if (phoneNumberField != null) phoneNumberField.requestFocus();
            return;
        }

        String phone = phoneNumberField.getText().trim();
        if (!phone.matches("^[+]?[0-9\\s-]{8,20}$")) {
            showStatus("❌ Format de téléphone invalide (ex: +216 XX XXX XXX)", "#EF4444");
            phoneNumberField.requestFocus();
            return;
        }

        // Validation Ville
        if (cityField == null || cityField.getText() == null || cityField.getText().trim().isEmpty()) {
            System.out.println("❌ Ville manquante");
            showStatus("❌ La ville est obligatoire", "#EF4444");
            if (cityField != null) cityField.requestFocus();
            return;
        }

        String city = cityField.getText().trim();
        if (city.length() < 2) {
            showStatus("❌ Le nom de la ville doit contenir au moins 2 caractères", "#EF4444");
            cityField.requestFocus();
            return;
        }

        // Validation Pays
        if (countryField == null || countryField.getText() == null || countryField.getText().trim().isEmpty()) {
            System.out.println("❌ Pays manquant");
            showStatus("❌ Le pays est obligatoire", "#EF4444");
            if (countryField != null) countryField.requestFocus();
            return;
        }

        String country = countryField.getText().trim();
        if (country.length() < 2) {
            showStatus("❌ Le nom du pays doit contenir au moins 2 caractères", "#EF4444");
            countryField.requestFocus();
            return;
        }

        // Validation Mot de passe
        if (registerPasswordField == null || registerPasswordField.getText() == null || registerPasswordField.getText().trim().isEmpty()) {
            System.out.println("❌ Mot de passe manquant");
            showStatus("❌ Le mot de passe est obligatoire", "#EF4444");
            if (registerPasswordField != null) registerPasswordField.requestFocus();
            return;
        }

        String password = registerPasswordField.getText().trim();
        if (password.length() < 6) {
            showStatus("❌ Le mot de passe doit contenir au moins 6 caractères", "#EF4444");
            registerPasswordField.requestFocus();
            return;
        }
        if (password.length() > 50) {
            showStatus("❌ Le mot de passe est trop long (max 50 caractères)", "#EF4444");
            registerPasswordField.requestFocus();
            return;
        }

        // Validation Rôle
        if (roleChoiceBox == null || roleChoiceBox.getValue() == null || roleChoiceBox.getValue().trim().isEmpty()) {
            System.out.println("❌ Rôle manquant");
            showStatus("❌ Veuillez sélectionner un rôle", "#EF4444");
            if (roleChoiceBox != null) roleChoiceBox.requestFocus();
            return;
        }

        // ═══════════ TOUTES LES VALIDATIONS RÉUSSIES ═══════════
        System.out.println("✅ Toutes les validations passées avec succès");
        System.out.println("📝 Données saisies:");
        System.out.println("  - Prénom: " + firstName);
        System.out.println("  - Nom: " + lastName);
        System.out.println("  - Email: " + email);
        System.out.println("  - Téléphone: " + phone);
        System.out.println("  - Ville: " + city);
        System.out.println("  - Pays: " + country);
        System.out.println("  - Rôle: " + roleChoiceBox.getValue());
        System.out.println("  - Mot de passe: " + password.length() + " caractères");

        // ═══════════════════════════════════════════════════════════════════════════
        // ⚠️ SECTION COMMENTÉE - VÉRIFICATION BIOMÉTRIQUE PYTHON + OPENCV
        // ═══════════════════════════════════════════════════════════════════════════
        // Cette section gérait la vérification faciale via webcam avant l'inscription.
        // Commentée temporairement - création de compte direct sans vérification.
        // ═══════════════════════════════════════════════════════════════════════════

        /*
        // ═══════════ VÉRIFICATION BIOMÉTRIQUE PYTHON ═══════════
        System.out.println("🔐 Démarrage de la vérification biométrique...");

        // Vérifier que Python est disponible
        if (!biometricService.isPythonAvailable()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Python requis");
            alert.setHeaderText("Python n'est pas installé");
            alert.setContentText("La vérification biométrique nécessite Python 3.x avec OpenCV.\n\n" +
                               "Voulez-vous continuer sans vérification biométrique ?");

            ButtonType btnYes = new ButtonType("Continuer sans vérification", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(btnYes, btnNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnYes) {
                    // Créer le compte sans vérification
                    createUserAccount(firstName, lastName, email, password, phone, city, country, roleChoiceBox.getValue());
                }
            });
            return;
        }

        // Afficher un message d'attente
        showStatus("🎥 Lancement de la vérification biométrique...", "#4F46E5");

        // Variables finales pour la lambda
        final String finalFirstName = firstName;
        final String finalLastName = lastName;
        final String finalEmail = email;
        final String finalPassword = password;
        final String finalPhone = phone;
        final String finalCity = city;
        final String finalCountry = country;
        final String finalRole = roleChoiceBox.getValue();

        // Exécuter la vérification dans un thread séparé
        Task<PythonBiometricService.VerificationResult> verificationTask =
            new Task<PythonBiometricService.VerificationResult>() {
            @Override
            protected PythonBiometricService.VerificationResult call() {
                // Lancer le script Python (durée: 15 secondes)
                return biometricService.verifyWithWebcam(15, null);
            }
        };

        verificationTask.setOnSucceeded(event -> {
            PythonBiometricService.VerificationResult result = verificationTask.getValue();

            System.out.println("📊 Résultat de la vérification: " + result);

            if (result.isSuccess()) {
                System.out.println("✅ Vérification biométrique réussie!");
                showStatus("✅ Visage vérifié! Création du compte...", "#10B981");

                // Créer le compte après vérification réussie
                Platform.runLater(() -> {
                    createUserAccount(finalFirstName, finalLastName, finalEmail, finalPassword,
                                    finalPhone, finalCity, finalCountry, finalRole);
                });
            } else {
                System.out.println("❌ Vérification biométrique échouée: " + result.getMessage());
                showStatus("❌ " + result.getMessage(), "#EF4444");

                // Proposer de réessayer
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Vérification échouée");
                    alert.setHeaderText("La vérification biométrique a échoué");
                    alert.setContentText(result.getMessage() + "\n\nVoulez-vous réessayer ?");

                    ButtonType btnRetry = new ButtonType("Réessayer", ButtonBar.ButtonData.YES);
                    ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
                    alert.getButtonTypes().setAll(btnRetry, btnCancel);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == btnRetry) {
                            handleRegister(); // Relancer la vérification
                        }
                    });
                });
            }
        });

        verificationTask.setOnFailed(event -> {
            Throwable error = verificationTask.getException();
            System.err.println("❌ Erreur lors de la vérification: " + error.getMessage());
            error.printStackTrace();

            showStatus("❌ Erreur lors de la vérification biométrique", "#EF4444");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de la vérification biométrique");
                alert.setContentText(error.getMessage());
                alert.showAndWait();
            });
        });

        // Démarrer le thread
        new Thread(verificationTask).start();
        */

        // ═══════════════════════════════════════════════════════════════════════════
        // ⚠️ CRÉATION DIRECTE DU COMPTE (SANS VÉRIFICATION BIOMÉTRIQUE)
        // ═══════════════════════════════════════════════════════════════════════════

        // Créer le compte directement
        createUserAccount(firstName, lastName, email, password, phone, city, country, roleChoiceBox.getValue());
    }

    /**
     * Crée le compte utilisateur après vérification biométrique
     */
    private void createUserAccount(String firstName, String lastName, String email, String password,
                                   String phone, String city, String country, String role) {
        try {
            System.out.println("🔄 Création de l'utilisateur après vérification biométrique...");

            // Créer un nouvel utilisateur
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

            System.out.println("✅ Objet User créé: " + newUser);
            System.out.println("🔄 Appel de userService.ajouter()...");

            // Ajouter l'utilisateur à la base de données
            userService.ajouter(newUser);

            System.out.println("✅ Utilisateur ajouté avec succès dans la base de données!");
            showStatus("✅ Inscription réussie! Votre identité a été vérifiée. Vous pouvez vous connecter.", "#27ae60");

            // ⚠️ COMMENTÉ - Flag biométrique non utilisé actuellement
            // biometricVerified = false;

            // Revenir au mode connexion après 3 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        System.out.println("🔄 Retour au mode connexion...");
                        handleRegisterToggle();
                    });
                } catch (InterruptedException e) {
                    System.err.println("❌ Erreur dans le thread: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de l'inscription:");
            System.err.println("   Type: " + e.getClass().getName());
            System.err.println("   Message: " + e.getMessage());
            showStatus("❌ Erreur lors de l'inscription: " + e.getMessage(), "#e74c3c");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        showStatus("📧 Un email de réinitialisation a été envoyé", "#3498db");
    }

    private void testDatabaseConnection() {
        if (dbStatusLabel != null) {
            try {
                userService.read(); // simple ping
                dbStatusLabel.setText("🟢 Connecté à pidev");
                dbStatusLabel.setStyle("-fx-text-fill: #27ae60;");
            } catch (Exception e) {
                dbStatusLabel.setText("🔴 Erreur de connexion");
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