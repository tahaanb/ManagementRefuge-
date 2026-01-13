package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ma.refuge.model.Adoptant;
import ma.refuge.model.Animal;
import ma.refuge.service.AdoptantService;
import ma.refuge.service.AnimalService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class AnimalController {

    // --- Éléments FXML ---
    @FXML private TableView<Animal> animalTable;
    @FXML private TableColumn<Animal, String> photoColumn;
    @FXML private TableColumn<Animal, String> nomColumn;
    @FXML private TableColumn<Animal, String> especeColumn;
    @FXML private TableColumn<Animal, String> statutColumn;

    @FXML private TextField nomField;
    @FXML private TextField especeField;
    @FXML private TextField raceField;
    @FXML private TextField ageField;
    @FXML private ImageView previewImage;

    @FXML private ComboBox<String> statutCombo;
    @FXML private ComboBox<Adoptant> adoptantCombo; // Attention : doit être présent dans le FXML
    @FXML private Button ficheSanteBtn;

    // --- Services et Données ---
    private final AnimalService animalService = new AnimalService();
    private final AdoptantService adoptantService = new AdoptantService();
    private final ObservableList<Animal> animaux = FXCollections.observableArrayList();
    private final ObservableList<Adoptant> adoptants = FXCollections.observableArrayList();

    private File selectedImageFile;


    @FXML private Button adoptBtn; // Nouveau bouton dans le FXML

    @FXML
    public void initialize() {
        // Init Statut
        statutCombo.setItems(FXCollections.observableArrayList("DISPONIBLE", "ADOPTE"));

        // Init Tableau avec CellFactory pour les images et les badges
        setupTableDesign();

        // Sécurité boutons
        ficheSanteBtn.disableProperty().bind(animalTable.getSelectionModel().selectedItemProperty().isNull());
        if(adoptBtn != null) {
            adoptBtn.disableProperty().bind(
                    animalTable.getSelectionModel().selectedItemProperty().isNull()
                            .or(adoptantCombo.getSelectionModel().selectedItemProperty().isNull())
            );
        }

        chargerAnimaux();
        chargerAdoptants();

        animalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            remplirChamps(newVal); // Cette méthode gère maintenant le vidage de l'image
        });
    }


    private void setupTableDesign() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        especeColumn.setCellValueFactory(new PropertyValueFactory<>("espece"));

        // --- IMAGE DANS LE TABLEAU (L'effet Waouh) ---
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photoPath"));
        photoColumn.setCellFactory(param -> new TableCell<>() {
            private final ImageView imgView = new ImageView();
            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image img = new Image("file:" + path, 45, 45, true, true);
                        imgView.setImage(img);
                        Circle clip = new Circle(22.5, 22.5, 22.5);
                        imgView.setClip(clip);
                        setGraphic(imgView);
                    } catch (Exception e) { setGraphic(null); }
                }
            }
        });

        // --- BADGE DE STATUT ---
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statutAdoption"));
        statutColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add(item.equals("DISPONIBLE") ? "badge-dispo" : "badge-adopte");
                    setGraphic(badge);
                }
            }
        });
    }
    private void setupPhotoColumn() {
        photoColumn.setCellFactory(param -> new TableCell<>() {
            private final ImageView imgView = new ImageView();
            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null) {
                    setGraphic(null);
                } else {
                    try {
                        // Utilisation de "file:" pour charger depuis le disque local
                        Image img = new Image("file:" + path, 50, 50, true, true);
                        imgView.setImage(img);
                        Circle clip = new Circle(25, 25, 25); // Image ronde
                        imgView.setClip(clip);
                        setGraphic(imgView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    @FXML
    public void choisirPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir la photo de l'animal");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) nomField.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            previewImage.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML

    public void ajouterAnimal() {
        String nom = nomField.getText().trim();
        String espece = especeField.getText().trim();
        String race = raceField.getText().trim();
        String ageText = ageField.getText().trim();

        // 1. Validation UX : Guidage utilisateur
        if (nom.isEmpty() || espece.isEmpty() || ageText.isEmpty()) {
            afficherAlerte("Champ Manquant", "💡 Astuce : Le nom et l'espèce sont indispensables pour identifier l'animal.", Alert.AlertType.WARNING);
            return;
        }

        // 2. Vérification Doublon (Nom + Espèce + Race)
        boolean exists = animalService.listerAnimaux().stream()
                .anyMatch(a -> a.getNom().equalsIgnoreCase(nom)
                        && a.getEspece().equalsIgnoreCase(espece)
                        && a.getRace().equalsIgnoreCase(race));

        if (exists) {
            afficherAlerte("Animal Déjà Présent", "🚫 Cet animal semble déjà être enregistré dans le refuge.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            Animal animal = new Animal(nom, espece, race, age);
            animal.setStatutAdoption("DISPONIBLE");

            // 3. Gestion de l'image avec chemin absolu pour l'affichage TableView
            if (selectedImageFile != null) {
                String fileName = System.currentTimeMillis() + "_" + nom + ".jpg";
                animalService.sauvegarderImagePhysique(selectedImageFile, fileName);
                // On stocke le chemin absolu pour être sûr que JavaFX le trouve
                File savedFile = new File("uploads/animals/" + fileName);
                animal.setPhotoPath(savedFile.getAbsolutePath());
            }

            animalService.ajouterAnimal(animal);
            afficherAlerte("Succès !", "🐾 " + nom + " a rejoint la famille du refuge.", Alert.AlertType.INFORMATION);

            reinitialiserInterface();
        } catch (NumberFormatException e) {
            afficherAlerte("Format Incorrect", "L'âge doit être un nombre entier.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            afficherAlerte("Erreur Système", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void adopterAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        Adoptant adoptant = adoptantCombo.getValue();

        if (selected == null || adoptant == null) return;

        if ("ADOPTE".equals(selected.getStatutAdoption())) {
            afficherAlerte("Déjà adopté", "Cet animal a déjà trouvé un foyer !", Alert.AlertType.WARNING);
            return;
        }

        selected.setAdoptant(adoptant);
        selected.setStatutAdoption("ADOPTE");
        animalService.modifierAnimal(selected);

        afficherAlerte("Adoption Réussie !", selected.getNom() + " part avec " + adoptant.getNom(), Alert.AlertType.INFORMATION);
        chargerAnimaux();
        viderChamps();
    }



    @FXML
    public void modifierAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            selected.setNom(nomField.getText());
            selected.setEspece(especeField.getText());
            selected.setRace(raceField.getText());
            selected.setAge(Integer.parseInt(ageField.getText()));
            if (statutCombo != null) selected.setStatutAdoption(statutCombo.getValue());

            animalService.modifierAnimal(selected);
            chargerAnimaux();
            afficherAlerte("Succès", "Modification effectuée", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de la modification", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void supprimerAnimal() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + selected.getNom() + " ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    animalService.supprimerAnimal(selected.getId());
                    chargerAnimaux();
                    viderChamps();
                }
            });
        }
    }

    @FXML
    private void ouvrirFicheSante() {
        Animal selected = animalTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FicheSante.fxml"));
            Parent root = loader.load();
            FicheSanteController controller = loader.getController();
            controller.setAnimal(selected);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Fiche Santé - " + selected.getNom());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(animalTable.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Impossible d'ouvrir la fiche santé.", Alert.AlertType.ERROR);
        }
    }

    private void chargerAnimaux() {
        animaux.setAll(animalService.listerAnimaux());
        animalTable.setItems(animaux);
    }

    private void chargerAdoptants() {
        if (adoptantCombo != null) {
            adoptants.setAll(adoptantService.listerAdoptants());
            adoptantCombo.setItems(adoptants);
        }
    }

    private void remplirChamps(Animal animal) {
        if (animal != null) {
            nomField.setText(animal.getNom());
            especeField.setText(animal.getEspece());
            raceField.setText(animal.getRace());
            ageField.setText(String.valueOf(animal.getAge()));
            statutCombo.setValue(animal.getStatutAdoption());

            if (animal.getPhotoPath() != null && !animal.getPhotoPath().isEmpty()) {
                previewImage.setImage(new Image("file:" + animal.getPhotoPath()));
            } else {
                previewImage.setImage(null); // Nettoie l'image si l'animal n'en a pas
            }
        } else {
            viderChamps();
        }
    }

    private void viderChamps() {
        nomField.clear();
        especeField.clear();
        raceField.clear();
        ageField.clear();
        statutCombo.setValue(null);
        adoptantCombo.setValue(null);
        previewImage.setImage(null); // Correction du bug : l'image disparaît bien
        selectedImageFile = null;
    }

    private void reinitialiserInterface() {
        chargerAnimaux();
        viderChamps();
    }

    private void afficherAlerte(String titre, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}