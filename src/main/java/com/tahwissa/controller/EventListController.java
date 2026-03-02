package com.tahwissa.controller;

import com.tahwissa.entity.Evenement;
import com.tahwissa.service.EvenementService;
import com.tahwissa.service.EventReactionService;
import com.tahwissa.utils.EventImageUtils;
import com.tahwissa.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class EventListController {
    @FXML private FlowPane eventsCardContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField txtSearch;
    @FXML private Button btnCreate;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final EvenementService evenementService = new EvenementService();
    private final EventReactionService eventReactionService = new EventReactionService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isAdmin()) {
            btnCreate.setVisible(false);
            btnCreate.setManaged(false);
            btnEdit.setVisible(false);
            btnEdit.setManaged(false);
            btnDelete.setVisible(false);
            btnDelete.setManaged(false);
        }
        refreshEventCards();
    }

    private List<Evenement> getCurrentEvenements() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            return SessionManager.getInstance().isAdmin()
                    ? evenementService.getAllEvenements()
                    : evenementService.getAvailableEvenements();
        }
        return evenementService.searchEvenements(keyword);
    }

    private void refreshEventCards() {
        eventsCardContainer.getChildren().clear();
        List<Evenement> list = getCurrentEvenements();
        for (Evenement e : list) {
            eventsCardContainer.getChildren().add(buildEventCard(e));
        }
    }

    private VBox buildEventCard(Evenement e) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");
        card.setUserData(e);
        card.setPrefWidth(320);
        card.setMinWidth(280);
        card.setMaxWidth(360);

        // Optional image at top
        if (e.getImageFilename() != null && !e.getImageFilename().isBlank()) {
            Image img = EventImageUtils.loadEventImage(e.getImageFilename());
            if (img != null) {
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(318);
                imgView.setFitHeight(140);
                imgView.setPreserveRatio(true);
                imgView.setStyle("-fx-background-radius: 12 12 0 0;");
                StackPane imgWrap = new StackPane(imgView);
                imgWrap.setMaxWidth(Double.MAX_VALUE);
                imgWrap.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 12 12 0 0;");
                card.getChildren().add(imgWrap);
                VBox.setMargin(imgWrap, new Insets(0, 0, 8, 0));
            }
        }

        // Top: category badge + status
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label categoryLabel = new Label(e.getCategorie() != null ? e.getCategorie() : "Événement");
        categoryLabel.getStyleClass().add("event-card-category");
        Label statutLabel = new Label(e.getStatut() != null ? e.getStatut() : "");
        statutLabel.getStyleClass().add("event-card-statut");
        if ("DISPONIBLE".equals(e.getStatut())) {
            statutLabel.getStyleClass().add("statut-disponible");
        } else if ("COMPLET".equals(e.getStatut())) {
            statutLabel.getStyleClass().add("statut-complet");
        } else {
            statutLabel.getStyleClass().add("statut-other");
        }
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(categoryLabel, spacer, statutLabel);

        // Title
        Label titleLabel = new Label(e.getTitre());
        titleLabel.getStyleClass().add("event-card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Description (truncated)
        String desc = e.getDescription();
        if (desc == null) desc = "";
        if (desc.length() > 120) desc = desc.substring(0, 117) + "...";
        Label descLabel = new Label(desc);
        descLabel.getStyleClass().add("event-card-desc");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(Double.MAX_VALUE);

        // Info grid: lieu, date, heure, prix, places
        VBox infoBox = new VBox(6);
        infoBox.getStyleClass().add("event-card-info");
        LocalDate d = e.getDateEvent();
        LocalTime t = e.getHeureEvent();
        String dateStr = d != null ? d.format(DATE_FMT) : "—";
        String timeStr = t != null ? t.format(TIME_FMT) : "—";
        infoBox.getChildren().addAll(
                infoRow("Lieu", e.getLieu() != null ? e.getLieu() : "—"),
                infoRow("Date", dateStr),
                infoRow("Heure", timeStr),
                infoRow("Prix", String.format("%.2f DT", e.getPrix())),
                infoRow("Places", String.valueOf(e.getNbPlaces()))
        );

        // Reactions (like/dislike) – shown for logged-in users
        HBox reactionsRow = buildReactionsRow(e);

        // Actions
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        if (isAdmin) {
            Button btnEditCard = new Button("Modifier");
            btnEditCard.getStyleClass().addAll("btn", "btn-outline");
            btnEditCard.setOnAction(ev -> loadForm(e));
            Button btnDeleteCard = new Button("Supprimer");
            btnDeleteCard.getStyleClass().addAll("btn", "btn-danger");
            btnDeleteCard.setOnAction(ev -> confirmDelete(e));
            actions.getChildren().addAll(btnEditCard, btnDeleteCard);
        } else {
            Button btnReserver = new Button("Réserver");
            btnReserver.getStyleClass().addAll("btn", "btn-primary");
            btnReserver.setOnAction(ev -> openReservationForm(e));
            actions.getChildren().add(btnReserver);
        }

        card.getChildren().add(topRow);
        card.getChildren().add(titleLabel);
        card.getChildren().add(descLabel);
        card.getChildren().add(infoBox);
        if (reactionsRow != null) {
            card.getChildren().add(reactionsRow);
        }
        card.getChildren().add(actions);

        VBox.setMargin(topRow, new Insets(0, 0, 8, 0));
        VBox.setMargin(titleLabel, new Insets(0, 0, 6, 0));
        VBox.setMargin(descLabel, new Insets(0, 0, 12, 0));
        VBox.setMargin(infoBox, new Insets(0, 0, 12, 0));
        if (reactionsRow != null) {
            VBox.setMargin(reactionsRow, new Insets(4, 0, 4, 0));
        }
        VBox.setMargin(actions, new Insets(8, 0, 0, 0));

        return card;
    }

    private HBox infoRow(String label, String value) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label + ":");
        lbl.getStyleClass().add("event-card-info-label");
        Label val = new Label(value);
        val.getStyleClass().add("event-card-info-value");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    private HBox buildReactionsRow(Evenement e) {
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId < 0) {
            return null;
        }
        int likes = eventReactionService.getLikeCount(e.getIdEvenement());
        int dislikes = eventReactionService.getDislikeCount(e.getIdEvenement());
        String userReaction = eventReactionService.getUserReactionType(e.getIdEvenement(), userId).orElse(null);

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Button btnLike = new Button("👍 " + likes);
        btnLike.getStyleClass().addAll("btn", "btn-ghost", "btn-reaction");
        if ("LIKE".equals(userReaction)) {
            btnLike.getStyleClass().add("btn-reaction-active-like");
        }
        btnLike.setOnAction(ev -> {
            eventReactionService.toggleLike(e.getIdEvenement(), userId);
            refreshEventCards();
        });

        Button btnDislike = new Button("👎 " + dislikes);
        btnDislike.getStyleClass().addAll("btn", "btn-ghost", "btn-reaction");
        if ("DISLIKE".equals(userReaction)) {
            btnDislike.getStyleClass().add("btn-reaction-active-dislike");
        }
        btnDislike.setOnAction(ev -> {
            eventReactionService.toggleDislike(e.getIdEvenement(), userId);
            refreshEventCards();
        });

        row.getChildren().addAll(btnLike, btnDislike);
        return row;
    }

    private void confirmDelete(Evenement e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'événement : " + e.getTitre());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet événement ? Cette action est irréversible.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (evenementService.deleteEvenement(e.getIdEvenement())) {
                showSuccess("Événement supprimé avec succès");
                refreshEventCards();
            } else {
                showError("Erreur lors de la suppression");
            }
        }
    }

    private void openReservationForm(Evenement e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReservationForm.fxml"));
            Parent content = loader.load();
            ReservationFormController ctrl = loader.getController();
            ctrl.setEvenement(e);
            StackPane contentArea = (StackPane) eventsCardContainer.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Erreur lors du chargement du formulaire de réservation");
        }
    }

    @FXML
    private void handleSearch() {
        refreshEventCards();
    }

    @FXML
    private void handleCreate() {
        loadForm(null);
    }

    @FXML
    private void handleEdit() {
        // No-op when using per-card Edit; keep for FXML binding if needed
    }

    @FXML
    private void handleDelete() {
        // No-op when using per-card Delete
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        refreshEventCards();
    }

    private void loadForm(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EventForm.fxml"));
            Parent content = loader.load();
            if (evenement != null) {
                EventFormController controller = loader.getController();
                controller.setEvenement(evenement);
            }
            StackPane contentArea = (StackPane) eventsCardContainer.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du formulaire");
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
