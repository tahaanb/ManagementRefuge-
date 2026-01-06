package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.refuge.model.Adoptant;
import ma.refuge.service.AdoptantService;

public class AdoptantController {

    @FXML private TableView<Adoptant> adoptantTable;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;

    private final AdoptantService adoptantService = new AdoptantService();
    private final ObservableList<Adoptant> adoptants = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        chargerAdoptants();
    }

    @FXML
    public void ajouterAdoptant() {
        Adoptant adoptant = new Adoptant(
                nomField.getText(),
                prenomField.getText(),
                telephoneField.getText(),
                emailField.getText()
        );

        adoptantService.ajouterAdoptant(adoptant);
        chargerAdoptants();
        viderChamps();
    }

    @FXML
    public void supprimerAdoptant() {
        Adoptant selected = adoptantTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            adoptantService.supprimerAdoptant(selected.getId());
            chargerAdoptants();
        }
    }

    private void chargerAdoptants() {
        adoptants.setAll(adoptantService.listerAdoptants());
        adoptantTable.setItems(adoptants);
    }

    private void viderChamps() {
        nomField.clear();
        prenomField.clear();
        telephoneField.clear();
        emailField.clear();
    }
}
