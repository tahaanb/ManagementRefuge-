package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.refuge.model.Animal;
import ma.refuge.service.AnimalService;

public class AnimalController {

    @FXML private TableView<Animal> animalTable;
    @FXML private TableColumn<Animal, String> nomColumn;
    @FXML private TableColumn<Animal, String> especeColumn;
    @FXML private TableColumn<Animal, String> raceColumn;
    @FXML private TableColumn<Animal, String> statutColumn;

    @FXML private TextField nomField;
    @FXML private TextField especeField;
    @FXML private TextField raceField;
    @FXML private TextField ageField;

    @FXML private ComboBox<String> statutCombo;

    private final AnimalService animalService = new AnimalService();
    private final ObservableList<Animal> animaux = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        statutCombo.setItems(FXCollections.observableArrayList("DISPONIBLE", "ADOPTE"));

        if (nomColumn != null) nomColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        if (especeColumn != null) especeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEspece()));
        if (raceColumn != null) raceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRace()));
        if (statutColumn != null) statutColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatutAdoption()));

        chargerAnimaux();
    }

    @FXML
    public void ajouterAnimal() {
        Animal animal = new Animal(
                nomField.getText(),
                especeField.getText(),
                raceField.getText(),
                Integer.parseInt(ageField.getText())
        );
        animal.setStatutAdoption("DISPONIBLE");
        animalService.ajouterAnimal(animal);
        chargerAnimaux();
        viderChamps();
    }

    @FXML
    public void modifierAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNom(nomField.getText());
            selected.setEspece(especeField.getText());
            selected.setRace(raceField.getText());
            selected.setAge(Integer.parseInt(ageField.getText()));
            selected.setStatutAdoption(statutCombo.getValue());
            animalService.modifierAnimal(selected);
            chargerAnimaux();
        }
    }

    @FXML
    public void supprimerAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            animalService.supprimerAnimal(selected.getId());
            chargerAnimaux();
        }
    }

    private void chargerAnimaux() {
        animaux.setAll(animalService.listerAnimaux());
        animalTable.setItems(animaux);
    }

    private void viderChamps() {
        nomField.clear();
        especeField.clear();
        raceField.clear();
        ageField.clear();
    }
}
