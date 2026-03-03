package tn.esprit.tahwissa.controllers.admin;

import tn.esprit.tahwissa.models.User;
import tn.esprit.tahwissa.services.UserService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserListController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Boolean> statusColumn;
    @FXML private TableColumn<User, Boolean> verifiedColumn;
    @FXML private TableColumn<User, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<User, String> actionsColumn;

    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> roleFilter;
    @FXML private ChoiceBox<String> statusFilter;
    @FXML private Button searchBtn;
    @FXML private Button addUserBtn;
    @FXML private Button backToDashboardBtn;

    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label dbStatusLabel;

    private UserService userService;
    private ObservableList<User> userList;

    @FXML
    public void initialize() {
        System.out.println("\u2705 UserListController initialis\u00e9");

        userService = new UserService();
        userList = FXCollections.observableArrayList();

        configureTable();
        configureFilters();
        loadUsers();
        setupListeners();
        testDatabaseConnection();
        
        // Customization for integrated dashboard
        if (backToDashboardBtn != null) {
            backToDashboardBtn.setText("\u21aa Gestion R\u00e9servation");
            backToDashboardBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        }
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        verifiedColumn.setCellValueFactory(new PropertyValueFactory<>("verified"));
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

        statusColumn.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(active ? "Actif" : "Inactif");
                    if (active) {
                        setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #E74C3C;");
                    }
                }
            }
        });

        verifiedColumn.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean verified, boolean empty) {
                super.updateItem(verified, empty);
                if (empty || verified == null) {
                    setText(null);
                } else {
                    setText(verified ? "\u2713" : "\u2717");
                    setStyle(verified ? "-fx-text-fill: #27AE60;" : "-fx-text-fill: #E74C3C;");
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
                    User user = getTableView().getItems().get(getIndex());

                    Button viewBtn = new Button("\ud83d\udc41\ufe0f");
                    viewBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-cursor: hand;");
                    viewBtn.setOnAction(e -> showUserDetails(user));

                    Button editBtn = new Button("\u270f\ufe0f");
                    editBtn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-cursor: hand;");
                    editBtn.setOnAction(e -> editUser(user));

                    Button deleteBtn = new Button("\ud83d\uddd1\ufe0f");
                    deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-cursor: hand;");
                    deleteBtn.setOnAction(e -> deleteUser(user));

                    HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
                    buttons.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void configureFilters() {
        roleFilter.setItems(FXCollections.observableArrayList("Tous les r\u00f4les", "USER", "AGENT", "ADMIN"));
        statusFilter.setItems(FXCollections.observableArrayList("Tous", "Actif", "Inactif"));
        roleFilter.setValue("Tous les r\u00f4les");
        statusFilter.setValue("Tous");
    }

    private void setupListeners() {
        searchBtn.setOnAction(e -> filterUsers());
        searchField.textProperty().addListener((obs, old, nw) -> filterUsers());
        roleFilter.setOnAction(e -> filterUsers());
        statusFilter.setOnAction(e -> filterUsers());

        addUserBtn.setOnAction(e -> openAddUserForm());
        // Updated for integration
        if (backToDashboardBtn != null) {
          backToDashboardBtn.setOnAction(e -> navigateToReservation());
        }
    }

    private void loadUsers() {
        try {
            userList.clear();
            userList.addAll(userService.read());
            userTable.setItems(userList);
            updateStatistics();
            System.out.println("\u2705 " + userList.size() + " utilisateurs charg\u00e9s");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    private void filterUsers() {
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String selectedRole = roleFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        ObservableList<User> filtered = FXCollections.observableArrayList();

        for (User user : userList) {
            String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
            String first = user.getFirstName() != null ? user.getFirstName().toLowerCase() : "";
            String last = user.getLastName() != null ? user.getLastName().toLowerCase() : "";

            boolean matchSearch = searchText.isEmpty() ||
                    email.contains(searchText) || first.contains(searchText) || last.contains(searchText);

            boolean matchRole = selectedRole == null ||
                    selectedRole.equals("Tous les r\u00f4les") ||
                    (user.getRole() != null && user.getRole().equals(selectedRole));

            boolean matchStatus = selectedStatus == null ||
                    selectedStatus.equals("Tous") ||
                    (selectedStatus.equals("Actif") && user.isActive()) ||
                    (selectedStatus.equals("Inactif") && !user.isActive());

            if (matchSearch && matchRole && matchStatus) {
                filtered.add(user);
            }
        }

        userTable.setItems(filtered);
        updateStatistics();
    }

    private void updateStatistics() {
        int total = userTable.getItems().size();
        totalUsersLabel.setText("Total: " + total + " utilisateur(s)");

        long active = userTable.getItems().stream().filter(User::isActive).count();
        activeUsersLabel.setText("Actifs: " + active);
    }

    private void testDatabaseConnection() {
        try {
            userService.read();
            dbStatusLabel.setText("\u2705 Connect\u00e9 \u00e0 pidev");
            dbStatusLabel.setStyle("-fx-text-fill: #27AE60;");
        } catch (SQLException e) {
            dbStatusLabel.setText("\u274c Erreur de connexion");
            dbStatusLabel.setStyle("-fx-text-fill: #E74C3C;");
        }
    }

    @FXML
    private void openAddUserForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/user/user-form.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUserListController(this);
            controller.initForAdd();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    private void editUser(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/user/user-form.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUserListController(this);
            controller.initForEdit(user);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    private void showUserDetails(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/user/user-details.fxml"));
            Parent root = loader.load();

            UserDetailsController controller = loader.getController();
            controller.setUserListController(this);
            controller.setUser(user);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les d\u00e9tails: " + e.getMessage());
        }
    }

    private void deleteUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'utilisateur");
        confirm.setContentText("Voulez-vous vraiment supprimer " + user.getEmail() + " ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.supprimer(user.getId());
                    loadUsers();
                    showAlert(Alert.AlertType.INFORMATION, "Succ\u00e8s", "Utilisateur supprim\u00e9 avec succ\u00e8s!");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer: " + e.getMessage());
                }
            }
        });
    }

    public void refreshTable() {
        loadUsers();
    }

    private void navigateToReservation() {
        // En mode int\u00e9gr\u00e9, on demande au MainLayoutController de changer d'onglet
        // Pour simplifier, on peut juste d\u00e9clencher un d\u00e9placement d'onglet via la scene
        Scene scene = userTable.getScene();
        if (scene != null) {
             TabPane tp = (TabPane) scene.lookup("#tabPane");
             if (tp != null) {
                 tp.getSelectionModel().select(1); // Select Admin Reservation Dashboard
             }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
