package tn.esprit.tahwissa.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.tahwissa.models.Voyage;
import tn.esprit.tahwissa.services.VoyageService;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.io.IOException;

import javafx.scene.control.*;


public class VoyageManagementController implements Initializable {

    @FXML private TableView<Voyage> voyagesTable;
    @FXML private TableColumn<Voyage, Integer> idCol;
    @FXML private TableColumn<Voyage, String> titreCol;
    @FXML private TableColumn<Voyage, String> destinationCol;
    @FXML private TableColumn<Voyage, String> categorieCol;
    @FXML private TableColumn<Voyage, BigDecimal> prixCol;
    @FXML private TableColumn<Voyage, LocalDate> departCol;
    @FXML private TableColumn<Voyage, LocalDate> retourCol;
    @FXML private TableColumn<Voyage, Integer> placesCol;
    @FXML private TableColumn<Voyage, String> statutCol;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button refreshBtn;

    private VoyageService voyageService;
    private ObservableList<Voyage> voyageList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            voyageService = new VoyageService();
            setupTable();
            loadData();
            setupButtons();
        } catch (SQLException e) {
            showError("Database error", e.getMessage());
        }
    }

    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        departCol.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        retourCol.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        placesCol.setCellValueFactory(new PropertyValueFactory<>("placesDisponibles"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void loadData() throws SQLException {
        voyageList.setAll(voyageService.getAllVoyages());
        voyagesTable.setItems(voyageList);
    }

    private void setupButtons() {
        addBtn.setOnAction(e -> showVoyageDialog(null));
        editBtn.setOnAction(e -> {
            Voyage selected = voyagesTable.getSelectionModel().getSelectedItem();
            if (selected != null) showVoyageDialog(selected);
            else showAlert("Sélection", "Veuillez sélectionner un voyage.");
        });
        deleteBtn.setOnAction(e -> deleteVoyage());
        refreshBtn.setOnAction(e -> {
            try {
                loadData();
            } catch (SQLException ex) {
                showError("Refresh error", ex.getMessage());
            }
        });
    }

    private void showVoyageDialog(Voyage voyage) {
        Dialog<Voyage> dialog = new Dialog<>();
        dialog.setTitle(voyage == null ? "Ajouter Voyage" : "Modifier Voyage");
        dialog.setHeaderText(voyage == null ? "Nouveau voyage" : "Modifier voyage #" + voyage.getId());

        // Create form fields
        TextField titreField = new TextField(voyage != null ? voyage.getTitre() : "");
        TextField destinationField = new TextField(voyage != null ? voyage.getDestination() : "");
        TextField categorieField = new TextField(voyage != null ? voyage.getCategorie() : "");
        TextField prixField = new TextField(voyage != null ? voyage.getPrixUnitaire().toString() : "");
        DatePicker departPicker = new DatePicker(voyage != null ? voyage.getDateDepart() : LocalDate.now());
        DatePicker retourPicker = new DatePicker(voyage != null ? voyage.getDateRetour() : LocalDate.now().plusDays(7));
        TextField placesField = new TextField(voyage != null ? String.valueOf(voyage.getPlacesDisponibles()) : "10");
        TextField statutField = new TextField(voyage != null ? voyage.getStatut() : "ACTIF");
        TextField descriptionField = new TextField(voyage != null ? voyage.getDescription() : "");
        TextField imageField = new TextField(voyage != null ? voyage.getImageUrl() : "");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Destination:"), 0, 1);
        grid.add(destinationField, 1, 1);
        grid.add(new Label("Catégorie:"), 0, 2);
        grid.add(categorieField, 1, 2);
        grid.add(new Label("Prix unitaire:"), 0, 3);
        grid.add(prixField, 1, 3);
        grid.add(new Label("Date départ:"), 0, 4);
        grid.add(departPicker, 1, 4);
        grid.add(new Label("Date retour:"), 0, 5);
        grid.add(retourPicker, 1, 5);
        grid.add(new Label("Places:"), 0, 6);
        grid.add(placesField, 1, 6);
        grid.add(new Label("Statut:"), 0, 7);
        grid.add(statutField, 1, 7);
        grid.add(new Label("Description:"), 0, 8);
        grid.add(descriptionField, 1, 8);
        grid.add(new Label("Image URL:"), 0, 9);
        grid.add(imageField, 1, 9);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    BigDecimal prix = new BigDecimal(prixField.getText());
                    int places = Integer.parseInt(placesField.getText());
                    Voyage v = voyage != null ? voyage : new Voyage();
                    v.setTitre(titreField.getText());
                    v.setDestination(destinationField.getText());
                    v.setCategorie(categorieField.getText());
                    v.setPrixUnitaire(prix);
                    v.setDateDepart(departPicker.getValue());
                    v.setDateRetour(retourPicker.getValue());
                    v.setPlacesDisponibles(places);
                    v.setStatut(statutField.getText());
                    v.setDescription(descriptionField.getText());
                    v.setImageUrl(imageField.getText());
                    return v;
                } catch (Exception e) {
                    showError("Erreur", "Vérifiez les champs numériques.");
                    return null;
                }
            }
            return null;
        });

        Optional<Voyage> result = dialog.showAndWait();
        result.ifPresent(v -> {
            try {
                if (v.getId() == 0) {
                    voyageService.addVoyage(v);
                } else {
                    voyageService.updateVoyage(v);
                }
                loadData();
            } catch (SQLException e) {
                showError("Erreur", e.getMessage());
            }
        });
    }

    private void deleteVoyage() {
        Voyage selected = voyagesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner un voyage.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer voyage");
        confirm.setContentText("Voulez-vous vraiment supprimer " + selected.getTitre() + " ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                voyageService.deleteVoyage(selected.getId());
                loadData();
            } catch (SQLException e) {
                showError("Erreur", e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
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