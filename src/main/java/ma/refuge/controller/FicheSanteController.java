package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ma.refuge.model.Animal;
import ma.refuge.model.FicheSante;
import ma.refuge.service.AnimalService;
import ma.refuge.service.FicheSanteService;

import java.io.File;
import java.time.LocalDate;
import java.util.Optional;

public class FicheSanteController {

    // Services
    private final FicheSanteService ficheSanteService = new FicheSanteService();
    private final AnimalService animalService = new AnimalService();

    // Données
    private Animal animal;
    private final ObservableList<FicheSante> fiches = FXCollections.observableArrayList();
    private boolean enModeEdition = false;
    private FicheSante ficheCourante = null;

    // Éléments UI
    @FXML private Label animalNomLabel;
    @FXML private ImageView animalImageView;
    @FXML private Label animalEspeceLabel;
    @FXML private Label animalAgeLabel;
    
    // Formulaire
    @FXML private ComboBox<FicheSante.TypeConsultation> typeCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField veterinaireField;
    @FXML private TextArea diagnosticArea;
    @FXML private TextArea traitementArea;
    @FXML private TextArea observationsArea;
    @FXML private DatePicker prochainRappelPicker;
    @FXML private TextArea notesImportantesArea;
    @FXML private TextField documentPathField;
    
    // Tableau des fiches
    @FXML private TableView<FicheSante> fichesTable;
    @FXML private TableColumn<FicheSante, LocalDate> dateCol;
    @FXML private TableColumn<FicheSante, String> typeCol;
    @FXML private TableColumn<FicheSante, String> veterinaireCol;

    @FXML
    public void initialize() {
        try {
            // Initialisation des colonnes du tableau
            if (dateCol != null) {
                dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            }
            if (typeCol != null) {
                typeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
            }
            if (veterinaireCol != null) {
                veterinaireCol.setCellValueFactory(new PropertyValueFactory<>("veterinaire"));
            }

            // Initialisation de la combo des types de consultation
            if (typeCombo != null) {
                typeCombo.getItems().setAll(FicheSante.TypeConsultation.values());
            }
            
            // Configuration du sélecteur de date
            if (datePicker != null) {
                datePicker.setValue(LocalDate.now());
            }
            
            // Gestion de la sélection dans le tableau
            if (fichesTable != null) {
                fichesTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            chargerFiche(newSelection);
                        }
                    });
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du contrôleur FicheSanteController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
        chargerDonneesAnimal();
        chargerHistorique();
    }

    private void chargerDonneesAnimal() {
        if (animal != null) {
            animalNomLabel.setText(animal.getNom());
            animalEspeceLabel.setText(animal.getEspece());
            // Mettre à jour l'image si disponible
            if (animal.getPhotoPath() != null && !animal.getPhotoPath().isEmpty()) {
                try {
                    Image image = new Image("file:" + animal.getPhotoPath());
                    animalImageView.setImage(image);
                } catch (Exception e) {
                    // Image par défaut si le chargement échoue
                    animalImageView.setImage(new Image("/images/default-animal.png"));
                }
            }
        }
    }

    private void chargerHistorique() {
        if (animal != null) {
            fiches.setAll(ficheSanteService.listerParAnimal(animal.getId()));
            fichesTable.setItems(fiches);
        }
    }

    @FXML
    private void handleNouvelleFiche() {
        enModeEdition = false;
        ficheCourante = null;
        viderFormulaire();
        fichesTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleEnregistrer() {
        if (!validerFormulaire()) {
            return;
        }

        try {
            if (enModeEdition && ficheCourante != null) {
                // Mise à jour d'une fiche existante
                mettreAJourFicheDepuisFormulaire(ficheCourante);
                ficheSanteService.mettreAJourFicheSante(ficheCourante);
                afficherMessage("Succès", "La fiche de santé a été mise à jour avec succès.");
            } else {
                // Création d'une nouvelle fiche
                FicheSante fiche = new FicheSante();
                mettreAJourFicheDepuisFormulaire(fiche);
                ficheSanteService.ajouterFicheSante(animal.getId(), fiche);
                afficherMessage("Succès", "La fiche de santé a été créée avec succès.");
            }
            
            chargerHistorique();
            viderFormulaire();
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible d'enregistrer la fiche de santé : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer() {
        FicheSante selection = fichesTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Erreur", "Veuillez sélectionner une fiche à supprimer.");
            return;
        }

        if (confirmerSuppression()) {
            try {
                ficheSanteService.supprimerFicheSante(selection.getId());
                chargerHistorique();
                viderFormulaire();
                enModeEdition = false;
                ficheCourante = null;
                afficherMessage("Succès", "La fiche de santé a été supprimée avec succès.");
            } catch (Exception e) {
                afficherErreur("Erreur", "Impossible de supprimer la fiche de santé : " + e.getMessage());
            }
        }
    }

    private void chargerFiche(FicheSante fiche) {
        enModeEdition = true;
        ficheCourante = fiche;
        
        typeCombo.setValue(fiche.getType());
        datePicker.setValue(fiche.getDate());
        veterinaireField.setText(fiche.getVeterinaire());
        diagnosticArea.setText(fiche.getDiagnostic());
        traitementArea.setText(fiche.getTraitement());
        observationsArea.setText(fiche.getObservations());
        prochainRappelPicker.setValue(fiche.getDateProchainRappel());
        notesImportantesArea.setText(fiche.getNotesImportantes());
        documentPathField.setText(fiche.getDocumentPath());
    }

    private void mettreAJourFicheDepuisFormulaire(FicheSante fiche) {
        fiche.setType(typeCombo.getValue());
        fiche.setDate(datePicker.getValue());
        fiche.setVeterinaire(veterinaireField.getText().trim());
        fiche.setDiagnostic(diagnosticArea.getText().trim());
        fiche.setTraitement(traitementArea.getText().trim());
        fiche.setObservations(observationsArea.getText().trim());
        fiche.setDateProchainRappel(prochainRappelPicker.getValue());
        fiche.setNotesImportantes(notesImportantesArea.getText().trim());
        fiche.setDocumentPath(documentPathField.getText().trim());
    }

    private boolean validerFormulaire() {
        if (typeCombo.getValue() == null) {
            afficherErreur("Champ requis", "Le type de consultation est obligatoire.");
            typeCombo.requestFocus();
            return false;
        }
        
        if (datePicker.getValue() == null) {
            afficherErreur("Champ requis", "La date est obligatoire.");
            datePicker.requestFocus();
            return false;
        }
        
        if (veterinaireField.getText() == null || veterinaireField.getText().trim().isEmpty()) {
            afficherErreur("Champ requis", "Le nom du vétérinaire est obligatoire.");
            veterinaireField.requestFocus();
            return false;
        }
        
        if (diagnosticArea.getText() == null || diagnosticArea.getText().trim().isEmpty()) {
            afficherErreur("Champ requis", "Le diagnostic est obligatoire.");
            diagnosticArea.requestFocus();
            return false;
        }
        
        return true;
    }

    private boolean confirmerSuppression() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer la fiche de santé");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette fiche de santé ? Cette action est irréversible.");
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void viderFormulaire() {
        typeCombo.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        veterinaireField.clear();
        diagnosticArea.clear();
        traitementArea.clear();
        observationsArea.clear();
        prochainRappelPicker.setValue(null);
        notesImportantesArea.clear();
        documentPathField.clear();
    }

    private void afficherMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleParcourirFichier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un document");
        
        // Filtres pour les types de fichiers courants
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"),
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        
        // Afficher la boîte de dialogue
        File fichier = fileChooser.showOpenDialog(documentPathField.getScene().getWindow());
        
        if (fichier != null) {
            // Stocker le chemin relatif si possible, sinon le chemin absolu
            String chemin = fichier.getAbsolutePath();
            documentPathField.setText(chemin);
        }
    }
}
