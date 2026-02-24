package controller;

import entities.User;
import services.UserService;
import services.FaceRecognitionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
    @FXML private Button captureFaceBtn;
    @FXML private Label faceStatusLabel;

    private UserService userService;
    private FaceRecognitionService faceRecognitionService;
    private UserListController userListController;
    private User currentUser;
    private boolean isEditMode = false;
    private double[] pendingFaceEmbedding;

    @FXML
    public void initialize() {
        userService = new UserService();
        faceRecognitionService = new FaceRecognitionService();
        if (errorLabel != null) {
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
        }

        if (roleField != null) {
            roleField.setItems(FXCollections.observableArrayList("USER", "AGENT", "ADMIN"));
            roleField.setValue("USER");
        }
        if (countryField != null) {
            countryField.setText("Tunisie");
        }
        if (activeCheckbox != null) {
            activeCheckbox.setSelected(true);
        }
        if (faceStatusLabel != null) {
            faceStatusLabel.setText("Visage non capture");
            faceStatusLabel.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 11px;");
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
        pendingFaceEmbedding = null;
        if (faceStatusLabel != null) {
            faceStatusLabel.setText("Visage non capture");
        }
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
        pendingFaceEmbedding = null;
        if (faceStatusLabel != null) {
            faceStatusLabel.setText("Capture facultative en modification");
        }
    }

    /**
     * Vérifie que l'utilisateur est un humain via la webcam
     * @return true si humain vérifié, false sinon
     */
    private boolean verifyHumanWithWebcam() {
        try {
            // Chemins Python (corrigés)
            String pythonPath = "C:\\Users\\mohamed\\Downloads\\workshopA6\\workshopA6\\.venv\\Scripts\\python.exe";
            String scriptPath = "C:\\Users\\mohamed\\Downloads\\workshopA6\\workshopA6\\scripts\\human_verification.py";

            System.out.println("🔍 Vérification Python: " + pythonPath);

            // Vérifier qu'OpenCV est disponible
            Process testProcess = Runtime.getRuntime().exec(pythonPath + " -c \"import cv2; print('OK')\"");
            int testCode = testProcess.waitFor();

            if (testCode != 0) {
                // Lire l'erreur
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(testProcess.getErrorStream())
                );
                String errorLine;
                StringBuilder errorMsg = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorMsg.append(errorLine).append("\n");
                }

                System.err.println("❌ OpenCV non disponible: " + errorMsg);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur Python");
                    alert.setHeaderText("OpenCV non installé");
                    alert.setContentText("Le module OpenCV n'est pas disponible.\n" +
                            "Exécutez: pip install opencv-python\n\n" +
                            "Détails: " + errorMsg);
                    alert.showAndWait();
                });
                return false;
            }

            // Afficher un message d'information
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Vérification biométrique");
                alert.setHeaderText("Vérification d'humanité");
                alert.setContentText("La webcam va s'activer pour vérifier que vous êtes un humain.\n" +
                        "Placez-vous face à la caméra et suivez les instructions.\n\n" +
                        "Appuyez sur ESPACE quand votre visage est détecté.");
                alert.showAndWait();
            });

            // Exécuter le script Python en mode webcam (15 secondes max)
            ProcessBuilder pb = new ProcessBuilder(
                    pythonPath,
                    scriptPath,
                    "webcam",
                    "15"
            );

            // Rediriger les flux d'erreur pour les voir dans la console
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Lire la sortie du script
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String jsonOutput = reader.readLine();
            System.out.println("📤 Réponse du script: " + jsonOutput);

            // Lire le reste de la sortie (logs Python)
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("🐍 Python: " + line);
            }

            // Attendre la fin du processus
            int exitCode = process.waitFor();
            System.out.println("🏁 Code de sortie Python: " + exitCode);

            // Analyser le résultat
            if (jsonOutput != null && jsonOutput.contains("\"success\": true")) {
                return true;
            } else {
                // Afficher le message d'erreur
                if (jsonOutput != null && jsonOutput.contains("\"message\":")) {
                    String message = jsonOutput.split("\"message\": \"")[1].split("\"")[0];
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Échec de vérification");
                        alert.setHeaderText("Vérification échouée");
                        alert.setContentText(message);
                        alert.showAndWait();
                    });
                }
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur technique");
                alert.setHeaderText("Erreur lors de la vérification");
                alert.setContentText("Impossible de lancer la vérification: " + e.getMessage() +
                        "\n\nVérifiez que votre webcam est connectée et que Python est installé.");
                alert.showAndWait();
            });
            return false;
        }
    }

    @FXML
    private void handleCaptureFace() {
        if (!faceRecognitionService.isPythonAvailable()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Python requis");
            alert.setHeaderText("face_recognition indisponible");
            alert.setContentText("Détails: " + faceRecognitionService.getLastError());
            alert.showAndWait();
            return;
        }

        if (captureFaceBtn != null) {
            captureFaceBtn.setDisable(true);
        }
        if (faceStatusLabel != null) {
            faceStatusLabel.setText("Capture en cours...");
            faceStatusLabel.setStyle("-fx-text-fill: #4F46E5; -fx-font-size: 11px;");
        }

        Task<FaceRecognitionService.FaceResult> task = new Task<FaceRecognitionService.FaceResult>() {
            @Override
            protected FaceRecognitionService.FaceResult call() {
                return faceRecognitionService.enrollWithWebcam(12);
            }
        };

        task.setOnSucceeded(event -> {
            FaceRecognitionService.FaceResult result = task.getValue();
            if (result.isSuccess() && result.getEmbedding() != null) {
                pendingFaceEmbedding = result.getEmbedding();
                if (faceStatusLabel != null) {
                    faceStatusLabel.setText("Visage capture avec succes");
                    faceStatusLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 11px;");
                }
            } else {
                pendingFaceEmbedding = null;
                if (faceStatusLabel != null) {
                    faceStatusLabel.setText(result.getMessage());
                    faceStatusLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 11px;");
                }
            }
            if (captureFaceBtn != null) {
                captureFaceBtn.setDisable(false);
            }
        });

        task.setOnFailed(event -> {
            pendingFaceEmbedding = null;
            if (faceStatusLabel != null) {
                faceStatusLabel.setText("Erreur lors de la capture");
                faceStatusLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 11px;");
            }
            if (captureFaceBtn != null) {
                captureFaceBtn.setDisable(false);
            }
        });

        new Thread(task).start();
    }

    @FXML
    private void handleSave() {
        // Validation des champs obligatoires
        if (emailField == null || emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            showError("L'email est obligatoire");
            return;
        }
        if (firstNameField == null || firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            showError("Le prénom est obligatoire");
            return;
        }
        if (lastNameField == null || lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            showError("Le nom est obligatoire");
            return;
        }
        if (!isEditMode && (passwordField == null || passwordField.getText() == null || passwordField.getText().trim().isEmpty())) {
            showError("Le mot de passe est obligatoire pour un nouvel utilisateur");
            return;
        }
        if (roleField == null || roleField.getValue() == null) {
            showError("Le rôle est obligatoire");
            return;
        }

        if (!isEditMode && pendingFaceEmbedding == null) {
            showError("Veuillez capturer le visage avant de creer l'utilisateur");
            return;
        }

        // 🔴 VÉRIFICATION HUMAINE - Seulement pour les nouveaux utilisateurs
        if (!isEditMode) {
            boolean isHuman = verifyHumanWithWebcam();
            if (!isHuman) {
                showError("Vérification humaine échouée. Inscription annulée.");
                return;
            }
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

                if (phoneField != null && phoneField.getText() != null) {
                    newUser.setPhone(phoneField.getText().trim());
                }
                if (verifiedCheckbox != null) {
                    newUser.setVerified(verifiedCheckbox.isSelected());
                }
                if (activeCheckbox != null) {
                    newUser.setActive(activeCheckbox.isSelected());
                }
                if (cityField != null && cityField.getText() != null) {
                    newUser.setCity(cityField.getText().trim());
                }
                if (countryField != null && countryField.getText() != null) {
                    newUser.setCountry(countryField.getText().trim());
                }

                userService.ajouter(newUser);

                User saved = userService.findByEmail(newUser.getEmail());
                if (saved != null && pendingFaceEmbedding != null) {
                    userService.saveFaceEmbedding(saved.getId(), pendingFaceEmbedding);
                }

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
                if (phoneField != null && phoneField.getText() != null) {
                    currentUser.setPhone(phoneField.getText().trim());
                }
                currentUser.setRole(roleField.getValue());
                if (verifiedCheckbox != null) {
                    currentUser.setVerified(verifiedCheckbox.isSelected());
                }
                if (activeCheckbox != null) {
                    currentUser.setActive(activeCheckbox.isSelected());
                }
                if (cityField != null && cityField.getText() != null) {
                    currentUser.setCity(cityField.getText().trim());
                }
                if (countryField != null && countryField.getText() != null) {
                    currentUser.setCountry(countryField.getText().trim());
                }

                if (passwordField != null && passwordField.getText() != null && !passwordField.getText().trim().isEmpty()) {
                    currentUser.setPassword(passwordField.getText().trim());
                }

                userService.update(currentUser);

                if (pendingFaceEmbedding != null && currentUser != null) {
                    userService.saveFaceEmbedding(currentUser.getId(), pendingFaceEmbedding);
                }

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
        if (faceStatusLabel != null) {
            faceStatusLabel.setText("Visage non capture");
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