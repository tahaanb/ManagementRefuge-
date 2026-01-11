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
import ma.refuge.model.FicheSante;
import ma.refuge.service.FicheSanteService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnimalDetailsController {

    private static final FicheSanteService ficheSanteService = new FicheSanteService();

    public static void showDetails(Animal animal) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Détails de " + animal.getNom());

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f7fafc;");

        // En-tête avec image et infos principales
        VBox header = createHeader(animal);
        root.setTop(header);

        // Onglets pour les différentes sections
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Onglet Informations générales
        Tab infoTab = new Tab("📋 Informations");
        infoTab.setContent(createInfoSection(animal));

        // Onglet Fiche santé
        Tab healthTab = new Tab("🏥 Fiche Santé");
        healthTab.setContent(createHealthSection(animal));

        // Onglet Historique
        Tab historyTab = new Tab("📅 Historique");
        historyTab.setContent(createHistorySection(animal));

        tabPane.getTabs().addAll(infoTab, healthTab, historyTab);
        root.setCenter(tabPane);

        // Bouton de fermeture
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button closeBtn = new Button("Fermer");
        closeBtn.getStyleClass().add("btn-secondary");
        closeBtn.setOnAction(e -> dialog.close());
        footer.getChildren().add(closeBtn);
        root.setBottom(footer);

        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(AnimalDetailsController.class
                .getResource("/view/style-modern.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    private static VBox createHeader(Animal animal) {
        VBox header = new VBox(20);
        header.setPadding(new Insets(30));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);");

        // Image
        ImageView imageView = ImageController.createImageView(animal.getPhotoPath(), 200, 200);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5); " +
                "-fx-background-radius: 100px; -fx-border-radius: 100px;");

        // Nom et badge
        HBox nameBox = new HBox(15);
        nameBox.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(animal.getNom());
        nameLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label statusBadge = new Label(animal.getStatutAdoption());
        statusBadge.getStyleClass().add("status-badge");
        statusBadge.getStyleClass().add(
                "ADOPTE".equals(animal.getStatutAdoption()) ? "status-adopted" : "status-available"
        );

        nameBox.getChildren().addAll(nameLabel, statusBadge);

        // Infos rapides
        HBox quickInfo = new HBox(30);
        quickInfo.setAlignment(Pos.CENTER);

        Label especeInfo = new Label("🐾 " + animal.getEspece());
        Label raceInfo = new Label("📝 " + animal.getRace());
        Label ageInfo = new Label("🎂 " + animal.getAge() + " ans");

        especeInfo.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        raceInfo.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        ageInfo.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        quickInfo.getChildren().addAll(especeInfo, raceInfo, ageInfo);

        header.getChildren().addAll(imageView, nameBox, quickInfo);
        return header;
    }

    private static ScrollPane createInfoSection(Animal animal) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Carte Informations de base
        VBox basicInfo = createInfoCard("Informations de base",
                "Nom", animal.getNom(),
                "Espèce", animal.getEspece(),
                "Race", animal.getRace(),
                "Âge", animal.getAge() + " ans",
                "Statut", animal.getStatutAdoption()
        );

        // Carte Adoptant (si adopté)
        if (animal.getAdoptant() != null) {
            VBox adoptantInfo = createInfoCard("Informations de l'adoptant",
                    "Nom complet", animal.getAdoptant().getNom() + " " + animal.getAdoptant().getPrenom(),
                    "Téléphone", animal.getAdoptant().getTelephone(),
                    "Email", animal.getAdoptant().getEmail()
            );
            content.getChildren().add(adoptantInfo);
        }

        content.getChildren().add(basicInfo);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private static ScrollPane createHealthSection(Animal animal) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Formulaire d'ajout de fiche santé
        VBox addForm = new VBox(15);
        addForm.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label formTitle = new Label("➕ Ajouter une entrée de santé");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Date");

        TextField typeField = new TextField();
        typeField.setPromptText("Type (Vaccination, Visite, Traitement...)");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description détaillée");
        descriptionArea.setPrefRowCount(3);

        Button addBtn = new Button("💾 Enregistrer");
        addBtn.getStyleClass().add("btn-success");
        addBtn.setOnAction(e -> {
            if (typeField.getText().trim().isEmpty() || descriptionArea.getText().trim().isEmpty()) {
                showError("Veuillez remplir tous les champs");
                return;
            }

            FicheSante fiche = new FicheSante();
            fiche.setDate(datePicker.getValue());
            fiche.setDescription(typeField.getText() + " - " + descriptionArea.getText());

            try {
                ficheSanteService.ajouterFicheSante(animal.getId(), fiche);
                showSuccess("Entrée de santé ajoutée avec succès!");
                typeField.clear();
                descriptionArea.clear();
            } catch (Exception ex) {
                showError("Erreur: " + ex.getMessage());
            }
        });

        addForm.getChildren().addAll(formTitle, datePicker, typeField, descriptionArea, addBtn);

        // Liste des fiches santé existantes
        VBox historyList = new VBox(10);
        Label historyTitle = new Label("📋 Historique des soins");
        historyTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 0;");
        historyList.getChildren().add(historyTitle);

        if (animal.getFichesSante() != null && !animal.getFichesSante().isEmpty()) {
            for (FicheSante fiche : animal.getFichesSante()) {
                VBox ficheCard = createHealthRecordCard(fiche);
                historyList.getChildren().add(ficheCard);
            }
        } else {
            Label noRecords = new Label("Aucune fiche santé enregistrée");
            noRecords.setStyle("-fx-text-fill: #718096; -fx-font-style: italic;");
            historyList.getChildren().add(noRecords);
        }

        content.getChildren().addAll(addForm, historyList);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private static VBox createHealthRecordCard(FicheSante fiche) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label date = new Label("📅 " + fiche.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        date.setStyle("-fx-font-weight: bold; -fx-text-fill: #667eea;");

        Label description = new Label(fiche.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #2d3748;");

        card.getChildren().addAll(date, description);
        return card;
    }

    private static ScrollPane createHistorySection(Animal animal) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(30));

        Label title = new Label("📅 Historique complet");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Timeline des événements
        VBox timeline = new VBox(10);

        // Événement de création
        VBox eventCard = createEventCard(
                "Arrivée au refuge",
                "L'animal a été enregistré dans le système",
                LocalDate.now().minusMonths(2) // Exemple
        );
        timeline.getChildren().add(eventCard);

        // Si adopté, ajouter l'événement d'adoption
        if ("ADOPTE".equals(animal.getStatutAdoption()) && animal.getAdoptant() != null) {
            VBox adoptionCard = createEventCard(
                    "Adoption",
                    "Adopté par " + animal.getAdoptant().getNom() + " " + animal.getAdoptant().getPrenom(),
                    LocalDate.now().minusDays(10) // Exemple
            );
            timeline.getChildren().add(adoptionCard);
        }

        content.getChildren().addAll(title, timeline);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private static VBox createInfoCard(String title, String... keyValues) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        card.getChildren().add(cardTitle);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);

        for (int i = 0; i < keyValues.length; i += 2) {
            Label key = new Label(keyValues[i] + ":");
            key.setStyle("-fx-font-weight: bold; -fx-text-fill: #718096;");

            Label value = new Label(keyValues[i + 1]);
            value.setStyle("-fx-text-fill: #2d3748;");

            grid.add(key, 0, i / 2);
            grid.add(value, 1, i / 2);
        }

        card.getChildren().add(grid);
        return card;
    }

    private static VBox createEventCard(String event, String description, LocalDate date) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
                "-fx-border-color: #667eea; -fx-border-width: 0 0 0 4; -fx-border-radius: 12;");

        Label eventLabel = new Label(event);
        eventLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-text-fill: #718096;");
        descLabel.setWrapText(true);

        Label dateLabel = new Label("📅 " + date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12px;");

        card.getChildren().addAll(eventLabel, descLabel, dateLabel);
        return card;
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}