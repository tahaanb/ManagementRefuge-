package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.refuge.model.Adoptant;
import ma.refuge.model.Animal;
import ma.refuge.service.AdoptantService;
import ma.refuge.service.AnimalService;

import java.util.Optional;

public class AnimalController {

    @FXML private TableView<Animal> animalTable;
    @FXML private TableColumn<Animal, String> nomColumn;
    @FXML private TableColumn<Animal, String> especeColumn;
    @FXML private TableColumn<Animal, String> raceColumn;
    @FXML private TableColumn<Animal, Integer> ageColumn;
    @FXML private TableColumn<Animal, String> statutColumn;
    @FXML private TableColumn<Animal, String> adoptantColumn;

    @FXML private TextField nomField;
    @FXML private TextField especeField;
    @FXML private TextField raceField;
    @FXML private TextField ageField;

    @FXML private ComboBox<String> statutCombo;
    @FXML private ComboBox<Adoptant> adoptantCombo;

    private final AnimalService animalService = new AnimalService();
    private final AdoptantService adoptantService = new AdoptantService();
    private final ObservableList<Animal> animaux = FXCollections.observableArrayList();
    private final ObservableList<Adoptant> adoptants = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        statutCombo.setItems(FXCollections.observableArrayList("DISPONIBLE", "ADOPTE"));

        if (nomColumn != null) nomColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        if (especeColumn != null) especeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEspece()));
        if (raceColumn != null) raceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRace()));
        if (ageColumn != null) ageColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAge()).asObject());
        if (statutColumn != null) statutColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatutAdoption()));
        if (adoptantColumn != null) adoptantColumn.setCellValueFactory(c -> {
            Adoptant adoptant = c.getValue().getAdoptant();
            return new javafx.beans.property.SimpleStringProperty(adoptant != null ? adoptant.getNom() + " " + adoptant.getPrenom() : "");
        });

        chargerAnimaux();
        chargerAdoptants();

        // Listener pour remplir les champs lors de la sélection d'une ligne
        animalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirChamps(newSelection);
            }
        });
    }

    @FXML
    public void ajouterAnimal() {
        // Validation des champs
        String nom = nomField.getText().trim();
        String espece = especeField.getText().trim();
        String race = raceField.getText().trim();
        String ageText = ageField.getText().trim();

        // Vérification des champs vides
        if (nom.isEmpty() || espece.isEmpty() || race.isEmpty() || ageText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Champs obligatoires manquants");
            alert.setContentText("Tous les champs (Nom, Espèce, Race, Âge) sont obligatoires.");
            alert.showAndWait();
            return;
        }

        // Validation de l'âge
        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 50) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText("Âge invalide");
                alert.setContentText("L'âge doit être un nombre positif entre 1 et 50 ans.");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Âge invalide");
            alert.setContentText("L'âge doit être un nombre entier.");
            alert.showAndWait();
            return;
        }

        // Validation de la longueur des champs
        if (nom.length() > 50 || espece.length() > 50 || race.length() > 50) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Données trop longues");
            alert.setContentText("Les champs ne doivent pas dépasser 50 caractères.");
            alert.showAndWait();
            return;
        }

        // Vérification que l'animal n'existe pas déjà
        boolean animalExists = animalService.listerAnimaux().stream()
                .anyMatch(a -> a.getNom().equalsIgnoreCase(nom) &&
                              a.getEspece().equalsIgnoreCase(espece) &&
                              a.getRace().equalsIgnoreCase(race));
        if (animalExists) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Animal déjà existant");
            alert.setContentText("Un animal avec le même nom, espèce et race existe déjà dans le système.");
            alert.showAndWait();
            return;
        }

        // Création et sauvegarde de l'animal
        try {
            Animal animal = new Animal(nom, espece, race, age);
            animal.setStatutAdoption("DISPONIBLE");
            animalService.ajouterAnimal(animal);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Animal ajouté");
            successAlert.setContentText("L'animal \"" + nom + "\" a été ajouté avec succès.");
            successAlert.showAndWait();

            chargerAnimaux();
            viderChamps();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout");
            alert.setContentText("Une erreur s'est produite lors de l'ajout de l'animal : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void modifierAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun animal sélectionné");
            alert.setContentText("Veuillez sélectionner un animal dans le tableau avant de modifier.");
            alert.showAndWait();
            return;
        }

        // Validation des champs
        String nom = nomField.getText().trim();
        String espece = especeField.getText().trim();
        String race = raceField.getText().trim();
        String ageText = ageField.getText().trim();
        String statut = statutCombo.getValue();

        // Vérification des champs vides
        if (nom.isEmpty() || espece.isEmpty() || race.isEmpty() || ageText.isEmpty() || statut == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Champs obligatoires manquants");
            alert.setContentText("Tous les champs (Nom, Espèce, Race, Âge, Statut) sont obligatoires.");
            alert.showAndWait();
            return;
        }

        // Validation de l'âge
        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 50) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText("Âge invalide");
                alert.setContentText("L'âge doit être un nombre positif entre 1 et 50 ans.");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Âge invalide");
            alert.setContentText("L'âge doit être un nombre entier.");
            alert.showAndWait();
            return;
        }

        // Validation de la longueur des champs
        if (nom.length() > 50 || espece.length() > 50 || race.length() > 50) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Données trop longues");
            alert.setContentText("Les champs ne doivent pas dépasser 50 caractères.");
            alert.showAndWait();
            return;
        }

        // Vérification que l'animal n'est pas déjà adopté si on essaie de le modifier
        if ("ADOPTE".equals(selected.getStatutAdoption()) && !"ADOPTE".equals(statut)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Changement de statut");
            alert.setContentText("Cet animal est déjà adopté. Voulez-vous vraiment changer son statut ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        // Modification de l'animal
        try {
            selected.setNom(nom);
            selected.setEspece(espece);
            selected.setRace(race);
            selected.setAge(age);
            selected.setStatutAdoption(statut);

            // Logique métier : si le statut devient DISPONIBLE, l'adoptant doit être null
            if ("DISPONIBLE".equals(statut)) {
                selected.setAdoptant(null);
            }

            animalService.modifierAnimal(selected);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Animal modifié");
            successAlert.setContentText("L'animal \"" + nom + "\" a été modifié avec succès.");
            successAlert.showAndWait();

            chargerAnimaux();
            viderChamps();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la modification");
            alert.setContentText("Une erreur s'est produite lors de la modification de l'animal : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void supprimerAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer l'animal");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer l'animal \"" + selected.getNom() + "\" ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                animalService.supprimerAnimal(selected.getId());
                chargerAnimaux();
                viderChamps();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun animal sélectionné");
            alert.setContentText("Veuillez sélectionner un animal dans le tableau avant de supprimer.");
            alert.showAndWait();
        }
    }

    private void chargerAnimaux() {
        animaux.setAll(animalService.listerAnimaux());
        animalTable.setItems(animaux);
    }

    private void chargerAdoptants() {
        adoptants.setAll(adoptantService.listerAdoptants());
        adoptantCombo.setItems(adoptants);
    }

    private void remplirChamps(Animal animal) {
        nomField.setText(animal.getNom());
        especeField.setText(animal.getEspece());
        raceField.setText(animal.getRace());
        ageField.setText(String.valueOf(animal.getAge()));
        statutCombo.setValue(animal.getStatutAdoption());
        // Pour l'adoptant, on pourrait pré-sélectionner s'il y en a un, mais pour la modification on laisse vide
        adoptantCombo.setValue(null);
    }

    @FXML
    public void adopterAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        Adoptant adoptant = adoptantCombo.getValue();

        // Validation de la sélection
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun animal sélectionné");
            alert.setContentText("Veuillez sélectionner un animal dans le tableau.");
            alert.showAndWait();
            return;
        }

        if (adoptant == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun adoptant sélectionné");
            alert.setContentText("Veuillez sélectionner un adoptant dans la liste déroulante.");
            alert.showAndWait();
            return;
        }

        // Vérification que l'animal n'est pas déjà adopté
        if ("ADOPTE".equals(selected.getStatutAdoption())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Animal déjà adopté");
            alert.setContentText("Cet animal est déjà adopté par " +
                (selected.getAdoptant() != null ? selected.getAdoptant().getNom() + " " + selected.getAdoptant().getPrenom() : "quelqu'un") +
                ". Vous ne pouvez pas l'adopter à nouveau.");
            alert.showAndWait();
            return;
        }

        // Confirmation de l'adoption
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation d'adoption");
        confirmAlert.setHeaderText("Adopter l'animal");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir adopter \"" + selected.getNom() +
            "\" (" + selected.getEspece() + ") par " + adoptant.getNom() + " " + adoptant.getPrenom() + " ?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        // Processus d'adoption
        try {
            selected.setAdoptant(adoptant);
            selected.setStatutAdoption("ADOPTE");
            animalService.modifierAnimal(selected);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Adoption réussie");
            successAlert.setContentText("L'animal \"" + selected.getNom() + "\" a été adopté par " +
                adoptant.getNom() + " " + adoptant.getPrenom() + " avec succès.");
            successAlert.showAndWait();

            chargerAnimaux();
            adoptantCombo.setValue(null); // Reset la sélection
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'adoption");
            alert.setContentText("Une erreur s'est produite lors de l'adoption : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void viderChamps() {
        nomField.clear();
        especeField.clear();
        raceField.clear();
        ageField.clear();
    }
}
