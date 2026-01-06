package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ma.refuge.model.Historique;
import ma.refuge.service.HistoriqueService;

public class HistoriqueController {

    @FXML private TableView<Historique> historiqueTable;

    private final HistoriqueService historiqueService = new HistoriqueService();
    private final ObservableList<Historique> historiques = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        chargerHistorique();
    }

    private void chargerHistorique() {
        historiques.setAll(historiqueService.listerHistorique());
        historiqueTable.setItems(historiques);
    }
}
