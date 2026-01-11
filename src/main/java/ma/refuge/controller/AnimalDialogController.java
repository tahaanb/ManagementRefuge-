package ma.refuge.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ma.refuge.model.Animal;
import ma.refuge.service.AnimalService;

import java.io.File;

public class AnimalDialogController {

    private static final AnimalService animalService = new AnimalService();

    public static void showAddDialog(Runnable onSuccess) {
        showDialog(null, onSuccess);
    }

    public static void showEditDialog(Animal animal, Runnable onSuccess) {
        showDialog(animal, onSuccess);
    }

    private static void showDialog(Animal animal, Runnable onSuccess) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(animal == null ? "Ajouter un animal" : "Modifier " + animal.getNom());

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white;");

        // Titre
        Label title = new Label(animal == null ? "➕ Nouvel Animal" : "✏ Modifier l'Animal");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Section Image
        VBox imageSection = new VBox(10);
        imageSection.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); -fx-background-radius: 15px;");

        if (animal != null && animal.getPhotoPath() != null) {
            imageView.setImage(ImageController.loadImage(animal.getPhotoPath()));
        } else {
            imageView.setImage(ImageController.loadImage(null));
        }

        Button selectImageBtn = new Button("📷 Choisir une photo");
        selectImageBtn.getStyleClass().add("btn-secondary");

        final File[] selectedImageFile = {null};

        selectImageBtn.setOnAction(e -> {
            File file = ImageController.selectImage();
            if (file != null) {
                selectedImageFile[0] = file;
                imageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
            }
        });

        imageSection.getChildren().addAll(imageView, selectImageBtn);

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        TextField nomField = new TextField();
        nomField.setPromptText("Nom de l'animal");
        if (animal != null) nomField.setText(animal.getNom());

        TextField especeField = new TextField();
        especeField.setPromptText("Espèce (Chien, Chat, etc.)");
        if (animal != null) especeField.setText(animal.getEspece());

        TextField raceField = new TextField();
        raceField.setPromptText("Race");
        if (animal != null) raceField.setText(animal.getRace());

        TextField ageField = new TextField();
        ageField.setPromptText("Âge");
        if (animal != null) ageField.setText(String.valueOf(animal.getAge()));

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description de l'animal (optionnel)");
        descriptionArea.setPrefRowCount(3);

        form.add(new Label("Nom:"), 0, 0);
        form.add(nomField, 1, 0);
        form.add(new Label("Espèce:"), 0, 1);
        form.add(especeField, 1, 1);
        form.add(new Label("Race:"), 0, 2);
        form.add(raceField, 1, 2);
        form.add(new Label("Âge:"), 0, 3);
        form.add(ageField, 1, 3);
        form.add(new Label("Description:"), 0, 4);
        form.add(descriptionArea, 1, 4);

        // Boutons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("btn-secondary");
        cancelBtn.setOnAction(e -> dialog.close());

        Button saveBtn = new Button(animal == null ? "Ajouter" : "Modifier");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            if (validateAndSave(animal, nomField, especeField, raceField,
                    ageField, selectedImageFile[0], dialog)) {
                onSuccess.run();
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        root.getChildren().addAll(title, imageSection, form, buttonBox);

        Scene scene = new Scene(root, 500, 700);
        scene.getStylesheets().add(AnimalDialogController.class
                .getResource("/view/style-modern.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    private static boolean validateAndSave(Animal existingAnimal, TextField nomField,
                                           TextField especeField, TextField raceField,
                                           TextField ageField, File imageFile, Stage dialog) {
        String nom = nomField.getText().trim();
        String espece = especeField.getText().trim();
        String race = raceField.getText().trim();
        String ageText = ageField.getText().trim();

        // Validation
        if (nom.isEmpty() || espece.isEmpty() || race.isEmpty() || ageText.isEmpty()) {
            showError("Tous les champs obligatoires doivent être remplis");
            return false;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 50) {
                showError("L'âge doit être entre 1 et 50 ans");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("L'âge doit être un nombre valide");
            return false;
        }

        try {
            if (existingAnimal == null) {
                // Nouvel animal
                Animal newAnimal = new Animal(nom, espece, race, age);
                newAnimal.setStatutAdoption("DISPONIBLE");

                animalService.ajouterAnimal(newAnimal);

                // Sauvegarder l'image après avoir l'ID
                if (imageFile != null) {
                    String photoPath = ImageController.saveImage(imageFile, newAnimal.getId());
                    if (photoPath != null) {
                        newAnimal.setPhotoPath(photoPath);
                        animalService.modifierAnimal(newAnimal);
                    }
                }

                showSuccess("Animal ajouté avec succès!");
            } else {
                // Modification
                existingAnimal.setNom(nom);
                existingAnimal.setEspece(espece);
                existingAnimal.setRace(race);
                existingAnimal.setAge(age);

                if (imageFile != null) {
                    String photoPath = ImageController.saveImage(imageFile, existingAnimal.getId());
                    if (photoPath != null) {
                        existingAnimal.setPhotoPath(photoPath);
                    }
                }

                animalService.modifierAnimal(existingAnimal);
                showSuccess("Animal modifié avec succès!");
            }

            dialog.close();
            return true;
        } catch (Exception e) {
            showError("Erreur lors de la sauvegarde: " + e.getMessage());
            return false;
        }
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur de saisie");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Opération réussie");
        alert.setContentText(message);
        alert.showAndWait();
    }
}