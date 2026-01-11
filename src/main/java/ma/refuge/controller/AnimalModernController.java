package ma.refuge.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import ma.refuge.model.Animal;
import ma.refuge.service.AnimalService;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AnimalModernController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> especeFilter;
    @FXML private ComboBox<String> statutFilter;
    @FXML private FlowPane animalsContainer;
    @FXML private Label totalAnimalsLabel;
    @FXML private Label availableAnimalsLabel;
    @FXML private Label adoptedAnimalsLabel;

    private final AnimalService animalService = new AnimalService();
    private List<Animal> allAnimals;

    @FXML
    public void initialize() {
        setupFilters();
        loadAnimals();
        updateStatistics();
    }

    private void setupFilters() {
        especeFilter.getItems().addAll("Toutes", "Chien", "Chat", "Lapin", "Oiseau", "Autre");
        especeFilter.setValue("Toutes");

        statutFilter.getItems().addAll("Tous", "DISPONIBLE", "ADOPTE");
        statutFilter.setValue("Tous");
    }

    private void loadAnimals() {
        allAnimals = animalService.listerAnimaux();
        displayAnimals(allAnimals);
    }

    private void displayAnimals(List<Animal> animals) {
        animalsContainer.getChildren().clear();

        for (Animal animal : animals) {
            VBox card = createAnimalCard(animal);
            animalsContainer.getChildren().add(card);
        }
    }

    private VBox createAnimalCard(Animal animal) {
        VBox card = new VBox(15);
        card.getStyleClass().add("animal-card");
        card.setPrefWidth(280);
        card.setPadding(new Insets(0));

        // Image de l'animal
        ImageView imageView = ImageController.createImageView(
                animal.getPhotoPath(), 280, 200
        );
        imageView.getStyleClass().add("animal-image");

        // Badge de statut
        HBox imageContainer = new HBox();
        imageContainer.getChildren().add(imageView);

        Label statusBadge = new Label(animal.getStatutAdoption());
        statusBadge.getStyleClass().add("status-badge");
        if ("ADOPTE".equals(animal.getStatutAdoption())) {
            statusBadge.getStyleClass().add("status-adopted");
        } else {
            statusBadge.getStyleClass().add("status-available");
        }

        StackPane imageStack = new StackPane();
        imageStack.getChildren().addAll(imageView, statusBadge);
        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(statusBadge, new Insets(10));

        // Informations de l'animal
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(15));

        Label nameLabel = new Label(animal.getNom());
        nameLabel.getStyleClass().add("animal-name");

        HBox detailsBox = new HBox(10);
        Label especeLabel = new Label("🐾 " + animal.getEspece());
        especeLabel.getStyleClass().add("animal-detail");
        Label ageLabel = new Label("🎂 " + animal.getAge() + " ans");
        ageLabel.getStyleClass().add("animal-detail");
        detailsBox.getChildren().addAll(especeLabel, ageLabel);

        Label raceLabel = new Label("Race: " + animal.getRace());
        raceLabel.getStyleClass().add("animal-race");

        // Si adopté, afficher l'adoptant
        if (animal.getAdoptant() != null) {
            Label adoptantLabel = new Label("👤 " + animal.getAdoptant().getNom() +
                    " " + animal.getAdoptant().getPrenom());
            adoptantLabel.getStyleClass().add("animal-adoptant");
            infoBox.getChildren().addAll(nameLabel, detailsBox, raceLabel, adoptantLabel);
        } else {
            infoBox.getChildren().addAll(nameLabel, detailsBox, raceLabel);
        }

        // Boutons d'action
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(0, 15, 15, 15));
        actionBox.setAlignment(Pos.CENTER);

        Button viewBtn = new Button("👁 Voir");
        viewBtn.getStyleClass().addAll("action-btn", "btn-view");
        viewBtn.setOnAction(e -> viewAnimalDetails(animal));

        Button editBtn = new Button("✏ Modifier");
        editBtn.getStyleClass().addAll("action-btn", "btn-edit");
        editBtn.setOnAction(e -> editAnimal(animal));

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().addAll("action-btn", "btn-delete");
        deleteBtn.setOnAction(e -> deleteAnimal(animal));

        actionBox.getChildren().addAll(viewBtn, editBtn, deleteBtn);

        card.getChildren().addAll(imageStack, infoBox, actionBox);

        // Effet hover
        card.setOnMouseEntered(e -> card.setStyle("-fx-transform: scale(1.05);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-transform: scale(1);"));

        return card;
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String espece = especeFilter.getValue();
        String statut = statutFilter.getValue();

        List<Animal> filtered = allAnimals.stream()
                .filter(a -> searchText.isEmpty() ||
                        a.getNom().toLowerCase().contains(searchText) ||
                        a.getEspece().toLowerCase().contains(searchText) ||
                        a.getRace().toLowerCase().contains(searchText))
                .filter(a -> "Toutes".equals(espece) || a.getEspece().equals(espece))
                .filter(a -> "Tous".equals(statut) || a.getStatutAdoption().equals(statut))
                .collect(Collectors.toList());

        displayAnimals(filtered);
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        especeFilter.setValue("Toutes");
        statutFilter.setValue("Tous");
        displayAnimals(allAnimals);
    }

    private void updateStatistics() {
        totalAnimalsLabel.setText(String.valueOf(allAnimals.size()));

        long available = allAnimals.stream()
                .filter(a -> "DISPONIBLE".equals(a.getStatutAdoption()))
                .count();
        availableAnimalsLabel.setText(String.valueOf(available));

        long adopted = allAnimals.stream()
                .filter(a -> "ADOPTE".equals(a.getStatutAdoption()))
                .count();
        adoptedAnimalsLabel.setText(String.valueOf(adopted));
    }

    @FXML
    private void openAddAnimalDialog() {
        AnimalDialogController.showAddDialog(this::loadAnimals);
    }

    private void viewAnimalDetails(Animal animal) {
        AnimalDetailsController.showDetails(animal);
    }

    private void editAnimal(Animal animal) {
        AnimalDialogController.showEditDialog(animal, this::loadAnimals);
    }

    private void deleteAnimal(Animal animal) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer l'animal");
        confirmation.setContentText("Voulez-vous vraiment supprimer " + animal.getNom() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                animalService.supprimerAnimal(animal.getId());
                loadAnimals();
                updateStatistics();
            }
        });
    }
}