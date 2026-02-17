package tn.esprit.tahwisa.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import tn.esprit.tahwisa.models.Destination;
import tn.esprit.tahwisa.models.PointInteret;
import tn.esprit.tahwisa.services.DestinationService;
import tn.esprit.tahwisa.services.PointInteretService;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PointInteretViewController implements Initializable {

    @FXML private TableView<PointInteret> tablePointsInteret;
    @FXML private TableColumn<PointInteret, Integer> colId;
    @FXML private TableColumn<PointInteret, String> colNom;
    @FXML private TableColumn<PointInteret, String> colType;
    @FXML private TableColumn<PointInteret, String> colDestination;
    @FXML private TableColumn<PointInteret, String> colDescription;
    @FXML private TableColumn<PointInteret, Void> colActions;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbTypeFilter;
    @FXML private Label lblTotalPoints;
    @FXML private Label lblMonuments;
    @FXML private Label lblPlages;
    @FXML private Label lblMusees;
    @FXML private Label lblTableCount;

    private PointInteretService pointInteretService;
    private DestinationService destinationService;
    private ObservableList<PointInteret> pointsList;
    private ObservableList<PointInteret> filteredList;
    private Map<Integer, String> destinationsMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pointInteretService = new PointInteretService();
        destinationService = new DestinationService();
        pointsList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
        loadDestinationsMap();
        setupTypeFilter();
        setupTable();
        loadData();
        updateStatistics();
    }

    private void loadDestinationsMap() {
        try {
            for (Destination d : destinationService.afficherDestinations())
                destinationsMap.put(d.getIdDestination(), d.getNom());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupTypeFilter() {
        cmbTypeFilter.setItems(FXCollections.observableArrayList(
                "Tous les types","monument","plage","musée","restaurant","parc","hôtel","autre"));
        cmbTypeFilter.setValue("Tous les types");
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPointInteret"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else { setText(item); setStyle("-fx-text-fill:" + getTypeColor(item) + ";-fx-font-weight:bold;"); }
            }
        });

        colDestination.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) setText(null);
                else setText(destinationsMap.getOrDefault(((PointInteret)getTableRow().getItem()).getDestinationId(), "N/A"));
            }
        });

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");
            private final HBox box = new HBox(6, btnEdit, btnDelete);
            {
                String es = "-fx-background-color:#DBEAFE;-fx-text-fill:#2563EB;-fx-font-size:15px;-fx-cursor:hand;-fx-background-radius:6px;-fx-min-width:34px;-fx-min-height:30px;-fx-padding:3px 8px;";
                String ds = "-fx-background-color:#FEE2E2;-fx-text-fill:#DC2626;-fx-font-size:15px;-fx-cursor:hand;-fx-background-radius:6px;-fx-min-width:34px;-fx-min-height:30px;-fx-padding:3px 8px;";
                btnEdit.setStyle(es); btnDelete.setStyle(ds);
                btnEdit.setOnMouseEntered(e -> btnEdit.setStyle(es.replace("#DBEAFE","#BFDBFE")));
                btnEdit.setOnMouseExited(e -> btnEdit.setStyle(es));
                btnDelete.setOnMouseEntered(e -> btnDelete.setStyle(ds.replace("#FEE2E2","#FECACA")));
                btnDelete.setOnMouseExited(e -> btnDelete.setStyle(ds));
                box.setAlignment(javafx.geometry.Pos.CENTER);
                btnEdit.setOnAction(e -> { if(getIndex()>=0 && getIndex()<getTableView().getItems().size()) editPointInteret(getTableView().getItems().get(getIndex())); });
                btnDelete.setOnAction(e -> { if(getIndex()>=0 && getIndex()<getTableView().getItems().size()) deletePointInteret(getTableView().getItems().get(getIndex())); });
            }
            @Override protected void updateItem(Void item, boolean empty) { super.updateItem(item, empty); setGraphic(empty ? null : box); }
        });
    }

    private String getTypeColor(String type) {
        return switch (type.toLowerCase()) {
            case "monument" -> "#7C3AED"; case "plage" -> "#2563EB";
            case "musée" -> "#9333EA"; case "restaurant" -> "#D97706";
            case "parc" -> "#059669"; default -> "#6B7280";
        };
    }

    private void loadData() {
        try {
            List<PointInteret> points = pointInteretService.afficherPointsInteret();
            pointsList.setAll(points); filteredList.setAll(points);
            tablePointsInteret.setItems(filteredList);
            lblTableCount.setText(points.size() + " point(s)");
        } catch (Exception e) { showError("Erreur", e.getMessage()); }
    }

    private void updateStatistics() {
        lblTotalPoints.setText(String.valueOf(pointsList.size()));
        lblMonuments.setText(String.valueOf(pointsList.stream().filter(p -> "monument".equalsIgnoreCase(p.getType())).count()));
        lblPlages.setText(String.valueOf(pointsList.stream().filter(p -> "plage".equalsIgnoreCase(p.getType())).count()));
        lblMusees.setText(String.valueOf(pointsList.stream().filter(p -> "musée".equalsIgnoreCase(p.getType())).count()));
    }

    @FXML private void handleSearch() { applyFilters(); }
    @FXML private void handleTypeFilter() { applyFilters(); }

    private void applyFilters() {
        String kw = txtSearch.getText().toLowerCase().trim();
        String tf = cmbTypeFilter.getValue();
        List<PointInteret> results = pointsList.stream()
                .filter(p -> (kw.isEmpty() || p.getNom().toLowerCase().contains(kw) || (p.getDescription()!=null && p.getDescription().toLowerCase().contains(kw)))
                        && (tf==null || tf.equals("Tous les types") || tf.equalsIgnoreCase(p.getType())))
                .collect(Collectors.toList());
        filteredList.setAll(results);
        tablePointsInteret.setItems(filteredList);
        lblTableCount.setText(results.size() + " point(s)");
    }

    @FXML public void showCreateForm() { loadFormView(null); }
    @FXML private void refreshTable() { loadData(); updateStatistics(); txtSearch.clear(); cmbTypeFilter.setValue("Tous les types"); }

    private void editPointInteret(PointInteret pi) { if (pi != null) loadFormView(pi); }

    private void deletePointInteret(PointInteret pi) {
        if (pi == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation"); alert.setHeaderText("Supprimer : " + pi.getNom()); alert.setContentText("Irréversible.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try { pointInteretService.supprimerPointInteret(pi.getIdPointInteret()); loadData(); updateStatistics(); showSuccess("Supprimé !"); }
            catch (Exception e) { showError("Erreur", e.getMessage()); }
        }
    }

    private void loadFormView(PointInteret pi) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreatePointInteretForm.fxml"));
            Parent formView = loader.load();
            CreatePointInteretController controller = loader.getController();
            controller.setParentController(this);
            if (pi != null) controller.setPointInteret(pi);
            StackPane contentArea = (StackPane) tablePointsInteret.getScene().getRoot().lookup("#contentArea");
            contentArea.getChildren().clear(); contentArea.getChildren().add(formView);
        } catch (IOException e) { showError("Erreur", "Impossible de charger le formulaire"); e.printStackTrace(); }
    }

    private void showError(String title, String msg) { Alert a = new Alert(Alert.AlertType.ERROR); a.setTitle(title); a.setContentText(msg); a.showAndWait(); }
    private void showSuccess(String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Succès"); a.setContentText(msg); a.showAndWait(); }
}
