package controller;

import entities.User;
import services.UserService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserController {

    @FXML private Label userNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label dbStatusLabel;

    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> roleFilter;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Boolean> activeColumn;
    @FXML private TableColumn<User, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<User, String> actionsColumn;

    @FXML private VBox userFormContainer;
    @FXML private Label formTitleLabel;
    @FXML private TextField formEmailField;
    @FXML private PasswordField formPasswordField;
    @FXML private TextField formFirstNameField;
    @FXML private TextField formLastNameField;
    @FXML private ChoiceBox<String> formRoleField;
    @FXML private Button formSubmitButton;
    @FXML private Label formErrorLabel;

    private UserService userService;
    private ObservableList<User> userList;
    private User selectedUser;

    @FXML
    public void initialize() {
        System.out.println("🚀 Initialisation du contrôleur User...");

        userService = new UserService();
        userList = FXCollections.observableArrayList();
        selectedUser = null;

        // Init choix de rôles
        if (roleFilter != null) {
            roleFilter.setItems(FXCollections.observableArrayList("Tous les rôles", "USER", "AGENT", "ADMIN"));
            roleFilter.setValue("Tous les rôles");
        }
        if (formRoleField != null) {
            formRoleField.setItems(FXCollections.observableArrayList("USER", "AGENT", "ADMIN"));
            formRoleField.setValue("USER");
        }

        configureTable();
        loadUsers();
        configureListeners();
        updateStatistics();
        testDatabaseConnection();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        createdAtColumn.setCellFactory(column -> new TableCell<User, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });

        activeColumn.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                } else {
                    setText(active ? "Actif" : "Inactif");
                    setStyle(active ? "-fx-text-fill: #27AE60; -fx-font-weight: bold;" : "-fx-text-fill: #E74C3C;");
                }
            }
        });

        actionsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper("actions"));
        actionsColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button editBtn = new Button("✏️ Modifier");
                    Button deleteBtn = new Button("🗑️ Supprimer");

                    editBtn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-cursor: hand;");
                    deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-cursor: hand;");

                    User user = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> handleEdit(user));
                    deleteBtn.setOnAction(e -> handleDelete(user));

                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void configureListeners() {
        if (roleFilter != null) {
            roleFilter.setValue("Tous les rôles");
            roleFilter.setOnAction(e -> filterUsers());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterUsers();
            });
        }
    }

    private void loadUsers() {
        try {
            userList.clear();
            userList.addAll(userService.read());
            userTable.setItems(userList);
            System.out.println("✅ " + userList.size() + " utilisateurs chargés");
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    private void filterUsers() {
        if (userList == null) return;

        String searchText = (searchField != null && searchField.getText() != null)
                ? searchField.getText().toLowerCase()
                : "";
        String selectedRole = (roleFilter != null && roleFilter.getValue() != null)
                ? roleFilter.getValue()
                : "";

        ObservableList<User> filtered = FXCollections.observableArrayList();

        for (User user : userList) {
            String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
            String first = user.getFirstName() != null ? user.getFirstName().toLowerCase() : "";
            String last = user.getLastName() != null ? user.getLastName().toLowerCase() : "";

            boolean matchSearch = searchText.isEmpty() ||
                    email.contains(searchText) || first.contains(searchText) || last.contains(searchText);

            boolean matchRole = selectedRole.isEmpty() ||
                    selectedRole.equals("Tous les rôles") ||
                    (user.getRole() != null && user.getRole().equals(selectedRole));

            if (matchSearch && matchRole) {
                filtered.add(user);
            }
        }

        userTable.setItems(filtered);
        System.out.println("🔍 Filtre: " + filtered.size() + " résultats");
    }

    private void updateStatistics() {
        try {
            int total = userService.read().size();
            if (totalUsersLabel != null) {
                totalUsersLabel.setText(String.valueOf(total));
            }

            long active = userList.stream().filter(User::isActive).count();
            if (activeUsersLabel != null) {
                activeUsersLabel.setText(String.valueOf(active));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void testDatabaseConnection() {
        try {
            userService.read();
            if (dbStatusLabel != null) {
                dbStatusLabel.setText("✅ Connecté à pidev");
                dbStatusLabel.setStyle("-fx-text-fill: #27AE60;");
            }
            System.out.println("✅ Connexion DB réussie");
        } catch (SQLException e) {
            if (dbStatusLabel != null) {
                dbStatusLabel.setText("❌ Erreur de connexion");
                dbStatusLabel.setStyle("-fx-text-fill: #E74C3C;");
            }
            System.err.println("❌ Erreur DB: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        selectedUser = null;
        if (formTitleLabel != null) formTitleLabel.setText("➕ Ajouter un utilisateur");
        if (formSubmitButton != null) formSubmitButton.setText("Ajouter");
        clearForm();
        showForm(true);
    }

    @FXML
    private void handleEdit(User user) {
        if (user == null) return;

        selectedUser = user;
        if (formTitleLabel != null) formTitleLabel.setText("✏️ Modifier l'utilisateur");
        if (formSubmitButton != null) formSubmitButton.setText("Modifier");

        if (formEmailField != null) formEmailField.setText(user.getEmail());
        if (formFirstNameField != null) formFirstNameField.setText(user.getFirstName());
        if (formLastNameField != null) formLastNameField.setText(user.getLastName());
        if (formRoleField != null) formRoleField.setValue(user.getRole());
        if (formPasswordField != null) formPasswordField.clear();
        if (formErrorLabel != null) formErrorLabel.setText("");

        showForm(true);
    }

    @FXML
    private void handleDelete(User user) {
        if (user == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'utilisateur");
        confirm.setContentText("Voulez-vous vraiment supprimer " + user.getEmail() + " ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.supprimer(user.getId());
                    loadUsers();
                    updateStatistics();
                    showInfo("Succès", "Utilisateur supprimé avec succès!");
                } catch (SQLException e) {
                    showError("Erreur", "Impossible de supprimer: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleSubmitUser() {
        // Validation
        if (formEmailField == null || formEmailField.getText() == null || formEmailField.getText().trim().isEmpty()) {
            showFormError("L'email est obligatoire");
            return;
        }
        if (formFirstNameField == null || formFirstNameField.getText() == null || formFirstNameField.getText().trim().isEmpty()) {
            showFormError("Le prénom est obligatoire");
            return;
        }
        if (formLastNameField == null || formLastNameField.getText() == null || formLastNameField.getText().trim().isEmpty()) {
            showFormError("Le nom est obligatoire");
            return;
        }
        if (formRoleField == null || formRoleField.getValue() == null) {
            showFormError("Le rôle est obligatoire");
            return;
        }
        if (selectedUser == null && (formPasswordField == null || formPasswordField.getText() == null || formPasswordField.getText().trim().isEmpty())) {
            showFormError("Le mot de passe est obligatoire pour un nouvel utilisateur");
            return;
        }

        try {
            if (selectedUser == null) {
                // Ajout
                User newUser = new User(
                        formEmailField.getText().trim(),
                        formPasswordField.getText().trim(),
                        formFirstNameField.getText().trim(),
                        formLastNameField.getText().trim(),
                        formRoleField.getValue()
                );
                userService.ajouter(newUser);
                showInfo("Succès", "Utilisateur ajouté avec succès!");
            } else {
                // Modification
                selectedUser.setEmail(formEmailField.getText().trim());
                selectedUser.setFirstName(formFirstNameField.getText().trim());
                selectedUser.setLastName(formLastNameField.getText().trim());
                selectedUser.setRole(formRoleField.getValue());

                if (formPasswordField != null && formPasswordField.getText() != null && !formPasswordField.getText().trim().isEmpty()) {
                    selectedUser.setPassword(formPasswordField.getText().trim());
                }

                userService.update(selectedUser);
                showInfo("Succès", "Utilisateur modifié avec succès!");
            }

            loadUsers();
            updateStatistics();
            closeForm();

        } catch (SQLException e) {
            String error = e.getMessage().toLowerCase();
            if (error.contains("duplicate") || error.contains("unique")) {
                showFormError("Cet email est déjà utilisé");
            } else {
                showError("Erreur", "Erreur base de données: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        filterUsers();
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Confirmation de déconnexion");
        confirm.setContentText("Voulez-vous vraiment vous déconnecter?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("👋 Déconnexion...");
                System.exit(0);
            }
        });
    }

    @FXML
    private void closeForm() {
        showForm(false);
        clearForm();
    }

    private void showForm(boolean show) {
        if (userFormContainer != null) {
            userFormContainer.setManaged(show);
            userFormContainer.setVisible(show);
        }
    }

    private void clearForm() {
        if (formEmailField != null) formEmailField.clear();
        if (formPasswordField != null) formPasswordField.clear();
        if (formFirstNameField != null) formFirstNameField.clear();
        if (formLastNameField != null) formLastNameField.clear();
        if (formRoleField != null) formRoleField.setValue("USER");
        if (formErrorLabel != null) formErrorLabel.setText("");
    }

    private void showFormError(String message) {
        if (formErrorLabel != null) {
            formErrorLabel.setText("❌ " + message);
        } else {
            showError("Erreur de validation", message);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

