package ma.refuge.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ma.refuge.model.Animal;
import ma.refuge.model.Adoptant;
import ma.refuge.model.Historique;
import ma.refuge.service.AnimalService;
import ma.refuge.service.AdoptantService;
import ma.refuge.service.HistoriqueService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardModernController {

    @FXML private BorderPane root;
    @FXML private VBox statsContainer;
    @FXML private VBox chartsContainer;

    private final AnimalService animalService = new AnimalService();
    private final AdoptantService adoptantService = new AdoptantService();
    private final HistoriqueService historiqueService = new HistoriqueService();

    @FXML
    public void initialize() {
        NavbarController.setStaticRoot(root);
        createStatisticsCards();
        createCharts();
        createRecentActivity();
    }

    private void createStatisticsCards() {
        List<Animal> animals = animalService.listerAnimaux();
        List<Adoptant> adoptants = adoptantService.listerAdoptants();

        long availableCount = animals.stream()
                .filter(a -> "DISPONIBLE".equals(a.getStatutAdoption()))
                .count();

        long adoptedCount = animals.stream()
                .filter(a -> "ADOPTE".equals(a.getStatutAdoption()))
                .count();

        double adoptionRate = animals.isEmpty() ? 0 : (adoptedCount * 100.0 / animals.size());

        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(30));

        // Carte Total Animaux
        VBox totalCard = createStatCard(
                "🐾 Total Animaux",
                String.valueOf(animals.size()),
                "Dans le refuge",
                "#667eea"
        );

        // Carte Disponibles
        VBox availableCard = createStatCard(
                "✨ Disponibles",
                String.valueOf(availableCount),
                "Cherchent une famille",
                "#48bb78"
        );

        // Carte Adoptés
        VBox adoptedCard = createStatCard(
                "❤️ Adoptés",
                String.valueOf(adoptedCount),
                "Ont trouvé un foyer",
                "#ed8936"
        );

        // Carte Taux d'adoption
        VBox rateCard = createStatCard(
                "📊 Taux d'adoption",
                String.format("%.1f%%", adoptionRate),
                "De succès",
                "#764ba2"
        );

        // Carte Adoptants
        VBox adoptantsCard = createStatCard(
                "👥 Adoptants",
                String.valueOf(adoptants.size()),
                "Familles enregistrées",
                "#f56565"
        );

        statsBox.getChildren().addAll(totalCard, availableCard, adoptedCard, rateCard, adoptantsCard);

        if (statsContainer != null) {
            statsContainer.getChildren().add(statsBox);
        }
    }

    private VBox createStatCard(String title, String value, String subtitle, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 30 40; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5); " +
                        "-fx-min-width: 200; " +
                        "-fx-border-color: " + color + "; " +
                        "-fx-border-width: 0 0 4 0; " +
                        "-fx-border-radius: 20;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096; -fx-font-weight: 500;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #a0aec0;");

        card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);

        // Effet hover
        card.setOnMouseEntered(e -> card.setStyle(
                card.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                card.getStyle().replace("-fx-scale-x: 1.05; -fx-scale-y: 1.05;", "")
                        .replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8);",
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);")
        ));

        return card;
    }

    private void createCharts() {
        HBox chartsBox = new HBox(20);
        chartsBox.setPadding(new Insets(20, 30, 30, 30));

        // Graphique par espèce
        PieChart speciesChart = createSpeciesChart();
        speciesChart.setPrefSize(400, 300);

        // Graphique de tendance d'adoption
        BarChart<String, Number> adoptionChart = createAdoptionTrendChart();
        adoptionChart.setPrefSize(400, 300);

        chartsBox.getChildren().addAll(
                wrapChartInCard(speciesChart, "Répartition par espèce"),
                wrapChartInCard(adoptionChart, "Adoptions récentes")
        );

        if (chartsContainer != null) {
            chartsContainer.getChildren().add(chartsBox);
        }
    }

    private PieChart createSpeciesChart() {
        List<Animal> animals = animalService.listerAnimaux();
        Map<String, Integer> speciesCount = new HashMap<>();

        for (Animal animal : animals) {
            speciesCount.put(animal.getEspece(),
                    speciesCount.getOrDefault(animal.getEspece(), 0) + 1);
        }

        PieChart chart = new PieChart();
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            chart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);

        return chart;
    }

    private BarChart<String, Number> createAdoptionTrendChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Mois");
        yAxis.setLabel("Nombre d'adoptions");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Adoptions");

        // Données exemple (à remplacer par vraies données)
        series.getData().add(new XYChart.Data<>("Oct", 5));
        series.getData().add(new XYChart.Data<>("Nov", 8));
        series.getData().add(new XYChart.Data<>("Déc", 12));
        series.getData().add(new XYChart.Data<>("Jan", 6));

        chart.getData().add(series);
        chart.setLegendVisible(false);

        return chart;
    }

    private VBox wrapChartInCard(javafx.scene.Node chart, String title) {
        VBox card = new VBox(15);
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        card.getChildren().addAll(titleLabel, chart);
        return card;
    }

    private void createRecentActivity() {
        VBox activityBox = new VBox(15);
        activityBox.setPadding(new Insets(20, 30, 30, 30));

        Label title = new Label("📋 Activité récente");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        VBox activityList = new VBox(10);
        activityList.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );

        List<Historique> historiques = historiqueService.getHistorique();

        int count = 0;
        for (Historique h : historiques) {
            if (count >= 5) break; // Limiter à 5 entrées

            HBox activityItem = createActivityItem(h);
            activityList.getChildren().add(activityItem);
            count++;
        }

        if (historiques.isEmpty()) {
            Label noActivity = new Label("Aucune activité récente");
            noActivity.setStyle("-fx-text-fill: #a0aec0; -fx-font-style: italic;");
            activityList.getChildren().add(noActivity);
        }

        activityBox.getChildren().addAll(title, activityList);

        if (chartsContainer != null) {
            chartsContainer.getChildren().add(activityBox);
        }
    }

    private HBox createActivityItem(Historique historique) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
                "-fx-background-color: #f7fafc; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #e2e8f0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10;"
        );

        // Icône selon le type d'action
        String icon = getActionIcon(historique.getAction());
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");

        // Informations
        VBox info = new VBox(3);

        Label actionLabel = new Label(historique.getAction());
        actionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d3748; -fx-font-size: 14px;");

        Label descLabel = new Label(historique.getDescription());
        descLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 12px;");
        descLabel.setWrapText(true);

        Label dateLabel = new Label(formatDateTime(historique.getDate()));
        dateLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 11px;");

        info.getChildren().addAll(actionLabel, descLabel, dateLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        item.getChildren().addAll(iconLabel, info);

        return item;
    }

    private String getActionIcon(String action) {
        if (action.contains("AJOUT ANIMAL")) return "➕";
        if (action.contains("ADOPTION")) return "❤️";
        if (action.contains("MODIFICATION")) return "✏️";
        if (action.contains("SUPPRESSION")) return "🗑️";
        if (action.contains("FICHE SANTE")) return "🏥";
        return "📝";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
    }
}