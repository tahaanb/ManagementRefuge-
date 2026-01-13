package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.refuge.model.Historique;
import ma.refuge.service.HistoriqueService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoriqueController {

    // Références aux éléments de l'interface
    @FXML private Label totalActionsLabel;
    @FXML private Label dailyActionsLabel;
    @FXML private Label alertsLabel;
    @FXML private TableView<Historique> historiqueTable;
    @FXML private TableColumn<Historique, String> dateColumn;
    @FXML private TableColumn<Historique, String> actionColumn;
    @FXML private TableColumn<Historique, String> descriptionColumn;
    @FXML private TableColumn<Historique, String> statusColumn;
    @FXML private Label pageInfo;
    
    // Graphiques
    @FXML
    private AreaChart<String, Number> activityChart;
    @FXML private PieChart typePieChart;
    @FXML private BarChart<String, Number> monthlyBarChart;
    
    // Données et services
    private final HistoriqueService historiqueService = new HistoriqueService();
    private final ObservableList<Historique> historiques = FXCollections.observableArrayList();
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        // Configuration du tableau
        setupTable();
        
        // Chargement des données
        chargerDonnees();
        
        // Configuration des graphiques
        initialiserGraphiques();
        
        // Mise à jour de l'interface
        updatePaginationInfo();
    }
    
    private void setupTable() {
        // Configuration des colonnes du tableau
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Style des cellules
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "terminé":
                            setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            break;
                        case "en cours":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                        case "en attente":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }
    
    private void chargerDonnees() {
        // Chargement des données depuis le service
        List<Historique> toutesLesDonnees = historiqueService.getHistorique();
        
        // Calcul du nombre total de pages
        totalPages = (int) Math.ceil((double) toutesLesDonnees.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        
        // Mise à jour de la pagination si nécessaire
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        
        // Filtrage des données pour la page courante
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, toutesLesDonnees.size());
        
        if (fromIndex <= toIndex) {
            historiques.setAll(toutesLesDonnees.subList(fromIndex, toIndex));
        } else {
            historiques.clear();
        }
        
        historiqueTable.setItems(historiques);
    }
    
    private void initialiserGraphiques() {
        // Données factices pour la démonstration
        initialiserGraphiqueActivite();
        initialiserCamembertTypes();
        initialiserGraphiqueMensuel();
    }
    
    private void initialiserGraphiqueActivite() {
        // Série de données pour le graphique d'activité
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Activité");
        
        // Données factices pour la démonstration
        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        int[] activites = {12, 19, 8, 15, 12, 5, 10};
        
        for (int i = 0; i < jours.length; i++) {
            series.getData().add(new XYChart.Data<>(jours[i], activites[i]));
        }
        
        activityChart.getData().add(series);
        
        // Personnalisation du style
        activityChart.setLegendVisible(false);
        activityChart.setCreateSymbols(true);
        activityChart.setAnimated(false);
    }
    
    private void initialiserCamembertTypes() {
        // Données factices pour la démonstration
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Consultations", 45),
            new PieChart.Data("Vaccinations", 25),
            new PieChart.Data("Chirurgies", 15),
            new PieChart.Data("Traitements", 10),
            new PieChart.Data("Autres", 5)
        );
        
        typePieChart.setData(pieChartData);
        typePieChart.setLabelsVisible(true);
        typePieChart.setLegendVisible(true);
    }
    
    private void initialiserGraphiqueMensuel() {
        // Série de données pour le graphique mensuel
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Actions");
        
        // Données factices pour la démonstration
        String[] mois = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        int[] actions = {45, 52, 48, 65, 72, 86, 94, 78, 85, 92, 88, 95};
        
        for (int i = 0; i < Math.min(mois.length, actions.length); i++) {
            series.getData().add(new XYChart.Data<>(mois[i], actions[i]));
        }
        
        monthlyBarChart.getData().add(series);
        monthlyBarChart.setLegendVisible(false);
    }
    
    // Gestion de la pagination
    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            chargerDonnees();
            updatePaginationInfo();
        }
    }
    
    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            chargerDonnees();
            updatePaginationInfo();
        }
    }
    
    private void updatePaginationInfo() {
        pageInfo.setText(String.format("Page %d sur %d", currentPage, totalPages));
    }
    
    // Méthode pour rafraîchir les données (peut être appelée depuis d'autres contrôleurs)
    public void rafraichirDonnees() {
        chargerDonnees();
        initialiserGraphiques();
    }
}
