package com.tahwissa.controller;

import com.tahwissa.dto.EventReactionStat;
import com.tahwissa.dto.GlobalStats;
import com.tahwissa.dto.ReactionCountByDate;
import com.tahwissa.service.StatisticsService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatistiqueController {

    @FXML private HBox kpiRow;
    @FXML private StackPane paneTrend;
    @FXML private StackPane panePieChart;
    @FXML private StackPane paneBarChart;
    @FXML private VBox paneSummaryPanels;
    @FXML private TableView<EventReactionStat> tableReactions;
    @FXML private TableColumn<EventReactionStat, String> colEvent;
    @FXML private TableColumn<EventReactionStat, Number> colLikes;
    @FXML private TableColumn<EventReactionStat, Number> colDislikes;

    private final StatisticsService statisticsService = new StatisticsService();
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM");

    @FXML
    public void initialize() {
        GlobalStats stats = statisticsService.getGlobalStats();
        buildKpiCards(stats);
        buildTrendSection();
        buildLikesDislikesSummary();
        buildEventReactionBars();
        buildSummaryPanels(stats);
        loadReactionsByEventTable();
    }

    private void buildKpiCards(GlobalStats stats) {
        kpiRow.getChildren().clear();
        addKpiCard("👥", "Utilisateurs", String.valueOf(stats.getTotalUsers()), "dashboard-kpi-blue");
        addKpiCard("📅", "Événements", String.valueOf(stats.getTotalEvents()), "dashboard-kpi-purple");
        addKpiCard("🎟️", "Réservations", String.valueOf(stats.getTotalReservations()), "dashboard-kpi-red");
        addKpiCard("🔔", "Réclamations", String.valueOf(stats.getTotalReclamations()), "dashboard-kpi-orange");
        addKpiCard("❤️", "Réactions", String.valueOf(stats.getTotalLikes() + stats.getTotalDislikes()), "dashboard-kpi-green");
    }

    private void addKpiCard(String icon, String title, String value, String colorClass) {
        VBox card = new VBox(14);
        card.getStyleClass().addAll("dashboard-kpi-card", colorClass);
        card.setPadding(new Insets(24, 22, 24, 22));
        card.setMinWidth(172);
        card.setPrefWidth(192);

        VBox iconWrap = new VBox();
        iconWrap.getStyleClass().add("dashboard-kpi-icon-wrap");
        iconWrap.setAlignment(Pos.CENTER);
        iconWrap.setMinSize(56, 56);
        iconWrap.setPrefSize(56, 56);
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("dashboard-kpi-icon");
        iconWrap.getChildren().add(iconLabel);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("dashboard-kpi-value");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dashboard-kpi-title");

        card.getChildren().addAll(iconWrap, valueLabel, titleLabel);
        kpiRow.getChildren().add(card);
    }

    private void buildTrendSection() {
        List<ReactionCountByDate> byDate = statisticsService.getReactionsByDate(14);
        HBox barRow = new HBox(8);
        barRow.setAlignment(Pos.BOTTOM_CENTER);
        barRow.setPadding(new Insets(24, 16, 16, 16));

        long maxCount = 1;
        for (ReactionCountByDate d : byDate) {
            if (d.getCount() > maxCount) maxCount = d.getCount();
        }

        if (byDate.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                VBox bar = new VBox();
                bar.getStyleClass().addAll("dashboard-trend-bar", "dashboard-trend-bar-empty");
                bar.setMinHeight(8);
                bar.setPrefHeight(48);
                bar.setPrefWidth(36);
                barRow.getChildren().add(bar);
            }
            Label noData = new Label("Aucune réaction sur les 14 derniers jours");
            noData.getStyleClass().add("dashboard-trend-empty");
            VBox wrap = new VBox(16, barRow, noData);
            wrap.setAlignment(Pos.CENTER);
            wrap.setPadding(new Insets(8));
            paneTrend.getChildren().setAll(wrap);
            return;
        }

        for (ReactionCountByDate d : byDate) {
            VBox col = new VBox(8);
            col.setAlignment(Pos.BOTTOM_CENTER);
            double ratio = maxCount > 0 ? (double) d.getCount() / maxCount : 0;
            int height = (int) (Math.max(12, 140 * ratio));
            VBox bar = new VBox();
            bar.getStyleClass().addAll("dashboard-trend-bar", "dashboard-trend-bar-fill");
            bar.setMinHeight(8);
            bar.setPrefHeight(height);
            bar.setPrefWidth(40);
            Label dayLabel = new Label(d.getDate().format(SHORT_DATE));
            dayLabel.getStyleClass().add("dashboard-trend-label");
            Label countLabel = new Label(String.valueOf(d.getCount()));
            countLabel.getStyleClass().add("dashboard-trend-count");
            col.getChildren().addAll(bar, dayLabel, countLabel);
            barRow.getChildren().add(col);
        }

        paneTrend.getChildren().setAll(barRow);
    }

    private void buildLikesDislikesSummary() {
        GlobalStats stats = statisticsService.getGlobalStats();
        int likes = stats.getTotalLikes();
        int dislikes = stats.getTotalDislikes();
        int total = likes + dislikes;

        VBox content = new VBox(16);
        content.setPadding(new Insets(12));
        content.setAlignment(Pos.CENTER);

        if (total > 0) {
            double likeRatio = (double) likes / total;
            double dislikeRatio = (double) dislikes / total;

            Label likeLabel = new Label("Likes  " + likes + "  (" + String.format("%.0f", likeRatio * 100) + "%)");
            likeLabel.getStyleClass().add("stat-repartition-label");
            ProgressBar likeBar = new ProgressBar(likeRatio);
            likeBar.getStyleClass().add("stat-repartition-bar");
            likeBar.setPrefWidth(260);
            likeBar.setStyle("-fx-accent: #4F46E5;");

            Label dislikeLabel = new Label("Dislikes  " + dislikes + "  (" + String.format("%.0f", dislikeRatio * 100) + "%)");
            dislikeLabel.getStyleClass().add("stat-repartition-label");
            ProgressBar dislikeBar = new ProgressBar(dislikeRatio);
            dislikeBar.getStyleClass().add("stat-repartition-bar");
            dislikeBar.setPrefWidth(260);
            dislikeBar.setStyle("-fx-accent: #DC2626;");

            content.getChildren().addAll(
                    new VBox(6, likeLabel, likeBar),
                    new VBox(6, dislikeLabel, dislikeBar)
            );
        } else {
            Label empty = new Label("Aucune réaction");
            empty.getStyleClass().add("stat-repartition-empty");
            content.getChildren().add(empty);
        }

        panePieChart.getChildren().setAll(content);
    }

    private void buildEventReactionBars() {
        List<EventReactionStat> list = statisticsService.getReactionsByEvent();
        VBox listContent = new VBox(8);
        listContent.setPadding(new Insets(8));

        long maxReactions = 1;
        for (EventReactionStat s : list) {
            long sum = s.getLikeCount() + s.getDislikeCount();
            if (sum > maxReactions) maxReactions = sum;
        }

        int maxItems = Math.min(list.size(), 12);
        for (int i = 0; i < maxItems; i++) {
            EventReactionStat s = list.get(i);
            String name = s.getTitreEvenement().length() > 26
                    ? s.getTitreEvenement().substring(0, 23) + "..."
                    : s.getTitreEvenement();
            Label nameLabel = new Label(name);
            nameLabel.getStyleClass().add("stat-event-name");
            nameLabel.setMaxWidth(280);

            double likeRatio = maxReactions > 0 ? (double) s.getLikeCount() / maxReactions : 0;
            double dislikeRatio = maxReactions > 0 ? (double) s.getDislikeCount() / maxReactions : 0;

            HBox barRow = new HBox(10);
            barRow.setAlignment(Pos.CENTER_LEFT);
            ProgressBar likeBar = new ProgressBar(likeRatio);
            likeBar.getStyleClass().add("stat-event-bar");
            likeBar.setPrefWidth(90);
            likeBar.setMinHeight(16);
            likeBar.setStyle("-fx-accent: #4F46E5;");
            Label likeCount = new Label("👍 " + s.getLikeCount());
            likeCount.getStyleClass().add("stat-event-count");
            ProgressBar dislikeBar = new ProgressBar(dislikeRatio);
            dislikeBar.getStyleClass().add("stat-event-bar");
            dislikeBar.setPrefWidth(90);
            dislikeBar.setMinHeight(16);
            dislikeBar.setStyle("-fx-accent: #DC2626;");
            Label dislikeCount = new Label("👎 " + s.getDislikeCount());
            dislikeCount.getStyleClass().add("stat-event-count");
            barRow.getChildren().addAll(likeBar, likeCount, dislikeBar, dislikeCount);

            VBox row = new VBox(4, nameLabel, barRow);
            row.getStyleClass().add("stat-event-row");
            listContent.getChildren().add(row);
        }

        if (list.isEmpty()) {
            Label empty = new Label("Aucun événement avec réactions");
            empty.getStyleClass().add("stat-repartition-empty");
            listContent.getChildren().add(empty);
        }

        ScrollPane scroll = new ScrollPane(listContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        paneBarChart.getChildren().setAll(scroll);
    }

    private void buildSummaryPanels(GlobalStats stats) {
        paneSummaryPanels.getChildren().clear();
        addSummaryPanel("👥", "Registered " + stats.getTotalUsers(), "Total des utilisateurs", "dashboard-summary-green");
        addSummaryPanel("❤️", "Total " + (stats.getTotalLikes() + stats.getTotalDislikes()), "Likes et dislikes", "dashboard-summary-blue");
        addSummaryPanel("📝", "Registered " + stats.getTotalReclamations(), "Réclamations enregistrées", "dashboard-summary-purple");
    }

    private void addSummaryPanel(String icon, String value, String description, String colorClass) {
        HBox panel = new HBox(14);
        panel.getStyleClass().addAll("dashboard-summary-panel", colorClass);
        panel.setPadding(new Insets(16));
        panel.setAlignment(Pos.CENTER_LEFT);

        VBox iconWrap = new VBox();
        iconWrap.getStyleClass().add("dashboard-summary-icon-wrap");
        iconWrap.setAlignment(Pos.CENTER);
        iconWrap.setMinSize(44, 44);
        iconWrap.setPrefSize(44, 44);
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("dashboard-summary-icon");
        iconWrap.getChildren().add(iconLabel);

        VBox textBox = new VBox(4);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("dashboard-summary-value");
        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("dashboard-summary-desc");
        textBox.getChildren().addAll(valueLabel, descLabel);

        panel.getChildren().addAll(iconWrap, textBox);
        paneSummaryPanels.getChildren().add(panel);
    }

    private void loadReactionsByEventTable() {
        colEvent.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitreEvenement()));
        colLikes.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getLikeCount()));
        colDislikes.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getDislikeCount()));

        List<EventReactionStat> list = statisticsService.getReactionsByEvent();
        tableReactions.setItems(FXCollections.observableArrayList(list));
    }
}
