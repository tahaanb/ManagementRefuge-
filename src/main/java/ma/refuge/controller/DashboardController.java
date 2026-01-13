package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import ma.refuge.model.Adoptant;
import ma.refuge.model.Animal;
import ma.refuge.model.FicheSante;
import ma.refuge.model.Historique;
import ma.refuge.service.AdoptantService;
import ma.refuge.service.AnimalService;
import ma.refuge.service.FicheSanteService;
import ma.refuge.service.HistoriqueService;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private BorderPane root;

    // Cartes de statistiques (Labels)
    @FXML private Label animalCount;
    @FXML private Label adoptedCount;
    @FXML private Label adoptantCount;
    @FXML private Label activeAdoptants;
    @FXML private Label ficheSanteCount;
    @FXML private Label urgentFiches;
    @FXML private Label adoptionRate;
    @FXML private Label avgAdoptionTime;
    @FXML private Label lastUpdated;

    // Graphiques
    @FXML private AreaChart<String, Number> adoptionChart; // Changé en AreaChart pour l'effet Waouh
    @FXML private PieChart speciesPieChart;

    // Tableau des activités récentes
    @FXML private TableView<Historique> recentActivityTable;
    @FXML private TableColumn<Historique, LocalDateTime> dateCol;
    @FXML private TableColumn<Historique, String> actionCol;
    @FXML private TableColumn<Historique, String> descCol;

    private final AnimalService animalService = new AnimalService();
    private final AdoptantService adoptantService = new AdoptantService();
    private final FicheSanteService ficheSanteService = new FicheSanteService();
    private final HistoriqueService historiqueService = new HistoriqueService();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        refreshData();
        setupTableColumns();
    }

    @FXML
    public void refreshData() {
        try {
            loadStatistics();
            setupCharts();
            loadRecentActivities();
            updateLastUpdated();
        } catch (Exception e) {
            System.err.println("Erreur lors du rafraîchissement du dashboard: " + e.getMessage());
        }
    }

    private void loadStatistics() {
        // --- Statistiques Animaux ---
        List<Animal> animaux = animalService.listerAnimaux();
        int total = animaux.size();
        long adopted = animaux.stream()
                .filter(a -> "ADOPTE".equalsIgnoreCase(a.getStatutAdoption()))
                .count();

        if (animalCount != null) animalCount.setText(String.valueOf(total));
        if (adoptedCount != null) adoptedCount.setText(adopted + " adoptés");

        // --- Statistiques Adoptants ---
        List<Adoptant> adoptants = adoptantService.listerAdoptants();
        if (adoptantCount != null) adoptantCount.setText(String.valueOf(adoptants.size()));

        // --- Statistiques Santé ---
        List<FicheSante> fiches = ficheSanteService.listerToutesLesFiches();
        long urgents = fiches.stream()
                .filter(f -> f.getNotesImportantes() != null && f.getNotesImportantes().toLowerCase().contains("urgent"))
                .count();
        if (ficheSanteCount != null) ficheSanteCount.setText(String.valueOf(fiches.size()));
        if (urgentFiches != null) urgentFiches.setText(urgents + " Urgences");

        // --- Taux et Temps Moyen ---
        if (adoptionRate != null) {
            double rate = total > 0 ? (double) adopted / total * 100 : 0;
            adoptionRate.setText(String.format("%.1f%%", rate));
        }
        if (avgAdoptionTime != null) {
            avgAdoptionTime.setText("Moyenne: " + calculerTempsMoyenAdoption(animaux) + " jours");
        }
    }

    private void setupCharts() {
        // --- Graphique d'Adoption (AreaChart) ---
        if (adoptionChart != null) {
            adoptionChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Flux d'adoptions");

            Map<YearMonth, Long> stats = animalService.listerAnimaux().stream()
                    .filter(a -> a.getDateAdoption() != null)
                    .collect(Collectors.groupingBy(a -> YearMonth.from(a.getDateAdoption()), Collectors.counting()));

            stats.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> series.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));

            adoptionChart.getData().add(series);
        }

        // --- PieChart Espèces ---
        if (speciesPieChart != null) {
            Map<String, Long> counts = animalService.listerAnimaux().stream()
                    .collect(Collectors.groupingBy(Animal::getEspece, Collectors.counting()));

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            counts.forEach((k, v) -> data.add(new PieChart.Data(k, v)));
            speciesPieChart.setData(data);
        }
    }

    private void setupTableColumns() {
        if (recentActivityTable == null) return;

        // Configuration des colonnes
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Formattage de la date dans le tableau
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")));
            }
        });

        // Couleur dynamique des lignes selon l'action
        recentActivityTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Historique item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getAction().contains("SUPPRESSION")) {
                    setStyle("-fx-background-color: #ffeaa7;"); // Alerte douce
                } else if (item.getAction().contains("AJOUT")) {
                    setStyle("-fx-background-color: #f1f2f6;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void loadRecentActivities() {
        if (recentActivityTable != null) {
            List<Historique> list = historiqueService.getHistorique();
            // On prend les 15 derniers pour le dashboard
            ObservableList<Historique> recent = FXCollections.observableArrayList(
                    list.stream().sorted((h1, h2) -> h2.getDate().compareTo(h1.getDate())).limit(15).toList()
            );
            recentActivityTable.setItems(recent);
        }
    }

    private void updateLastUpdated() {
        if (lastUpdated != null) {
            lastUpdated.setText("Dernière synchro : " + LocalDateTime.now().format(dateTimeFormatter));
        }
    }

    private int calculerTempsMoyenAdoption(List<Animal> animaux) {
        List<Long> delais = animaux.stream()
                .filter(a -> a.getDateArrivee() != null && a.getDateAdoption() != null)
                .map(a -> java.time.temporal.ChronoUnit.DAYS.between(a.getDateArrivee(), a.getDateAdoption()))
                .filter(d -> d >= 0)
                .toList();

        return (int) Math.round(delais.stream().mapToLong(Long::longValue).average().orElse(0));
    }
}