package ma.refuge.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.refuge.model.Adoptant;
import ma.refuge.service.AdoptantService;

import java.util.Optional;
import java.util.regex.Pattern;

public class AdoptantController {

    @FXML private TableView<Adoptant> adoptantTable;
    @FXML private TableColumn<Adoptant, String> nomColumn;
    @FXML private TableColumn<Adoptant, String> prenomColumn;
    @FXML private TableColumn<Adoptant, String> telephoneColumn;
    @FXML private TableColumn<Adoptant, String> emailColumn;

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;

    private final AdoptantService adoptantService = new AdoptantService();
    private final ObservableList<Adoptant> adoptants = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (nomColumn != null) nomColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        if (prenomColumn != null) prenomColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPrenom()));
        if (telephoneColumn != null) telephoneColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelephone()));
        if (emailColumn != null) emailColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        chargerAdoptants();

        // Listener pour remplir les champs lors de la sélection d'une ligne
        adoptantTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirChamps(newSelection);
            }
        });
    }

    @FXML
    public void ajouterAdoptant() {
        // Validation des champs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String email = emailField.getText().trim();

        // Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Champs obligatoires manquants", 
                     "Tous les champs (Nom, Prénom, Téléphone, Email) sont obligatoires.");
            return;
        }

        // Validation des longueurs maximales
        if (nom.length() > 50 || prenom.length() > 50 || telephone.length() > 20 || email.length() > 100) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Données trop longues", 
                     "Les longueurs maximales sont :\n- Nom : 50 caractères\n- Prénom : 50 caractères\n- Téléphone : 20 caractères\n- Email : 100 caractères");
            return;
        }

        // Validation du format email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.matches(emailRegex, email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Email invalide", 
                     "Veuillez saisir une adresse email valide (ex: nom@domaine.com).");
            return;
        }

        // Validation du téléphone (format marocain)
        String phoneRegex = "^(\\+212|0)[6-7][0-9]{8}$";
        if (!Pattern.matches(phoneRegex, telephone)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Téléphone invalide", 
                     "Le numéro de téléphone doit être au format marocain valide :\n- Commencer par 0 ou +212\n- Suivi de 6 ou 7\n- Puis de 8 chiffres\nExemples : 0612345678 ou +212612345678");
            return;
        }

        // Vérification des doublons (email et téléphone)
        for (Adoptant a : adoptantService.listerAdoptants()) {
            if (a.getEmail().equalsIgnoreCase(email)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Email déjà utilisé", 
                         "Cette adresse email est déjà utilisée par un autre adoptant.");
                return;
            }
            if (a.getTelephone().equals(telephone)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Téléphone déjà utilisé", 
                         "Ce numéro de téléphone est déjà utilisé par un autre adoptant.");
                return;
            }
        }

        // Création et sauvegarde de l'adoptant
        try {
            Adoptant adoptant = new Adoptant(nom, prenom, telephone, email);
            adoptantService.ajouterAdoptant(adoptant);

            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     "Adoptant ajouté avec succès", 
                     String.format("L'adoptant %s %s a été enregistré avec succès.", nom, prenom));

            chargerAdoptants();
            viderChamps();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Échec de l'ajout", 
                     "Une erreur est survenue lors de l'ajout de l'adoptant : " + e.getMessage());
        }
    }

    @FXML
    public void modifierAdoptant() {
        Adoptant selected = adoptantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", 
                     "Aucun adoptant sélectionné", 
                     "Veuillez sélectionner un adoptant dans le tableau avant de modifier.");
            return;
        }

        // Demande de confirmation avant modification
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de modification");
        confirmAlert.setHeaderText("Modifier l'adoptant");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir modifier les informations de cet adoptant ?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // L'utilisateur a annulé
        }

        // Validation des champs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String email = emailField.getText().trim();

        // Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Champs obligatoires manquants", 
                     "Tous les champs (Nom, Prénom, Téléphone, Email) sont obligatoires.");
            return;
        }

        // Validation des longueurs maximales
        if (nom.length() > 50 || prenom.length() > 50 || telephone.length() > 20 || email.length() > 100) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Données trop longues", 
                     "Les longueurs maximales sont :\n- Nom : 50 caractères\n- Prénom : 50 caractères\n- Téléphone : 20 caractères\n- Email : 100 caractères");
            return;
        }

        // Validation du format email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.matches(emailRegex, email)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Email invalide", 
                     "Veuillez saisir une adresse email valide (ex: nom@domaine.com).");
            return;
        }

        // Validation du téléphone (format marocain)
        String phoneRegex = "^(\\+212|0)[6-7][0-9]{8}$";
        if (!Pattern.matches(phoneRegex, telephone)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", 
                     "Téléphone invalide", 
                     "Le numéro de téléphone doit être au format marocain valide :\n- Commencer par 0 ou +212\n- Suivi de 6 ou 7\n- Puis de 8 chiffres\nExemples : 0612345678 ou +212612345678");
            return;
        }

        // Vérification des doublons (email et téléphone)
        for (Adoptant a : adoptantService.listerAdoptants()) {
            if (a.getId() != selected.getId()) { // Ne pas vérifier l'adoptant actuel
                if (a.getEmail().equalsIgnoreCase(email)) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                             "Email déjà utilisé", 
                             "Cette adresse email est déjà utilisée par un autre adoptant.");
                    return;
                }
                if (a.getTelephone().equals(telephone)) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                             "Téléphone déjà utilisé", 
                             "Ce numéro de téléphone est déjà utilisé par un autre adoptant.");
                    return;
                }
            }
        }

        // Modification de l'adoptant
        try {
            selected.setNom(nom);
            selected.setPrenom(prenom);
            selected.setTelephone(telephone);
            selected.setEmail(email);
            
            adoptantService.modifierAdoptant(selected);

            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     "Modification réussie", 
                     String.format("Les informations de l'adoptant %s %s ont été mises à jour avec succès.", nom, prenom));

            chargerAdoptants();
            viderChamps();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Échec de la modification", 
                     "Une erreur est survenue lors de la modification de l'adoptant : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerAdoptant() {
        Adoptant selected = adoptantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", 
                     "Aucun adoptant sélectionné", 
                     "Veuillez sélectionner un adoptant dans le tableau avant de supprimer.");
            return;
        }

        try {
            // Vérification si l'adoptant a des animaux adoptés
            if (adoptantService.aDesAnimauxAdoptes(selected.getId())) {
                showAlert(Alert.AlertType.ERROR, "Suppression impossible", 
                         "Adoptant avec animaux", 
                         "Cet adoptant a des animaux à sa charge. Veuillez d'abord libérer ces animaux avant de pouvoir le supprimer.");
                return;
            }

            // Confirmation de suppression
            Alert confirmAlert = new Alert(
                Alert.AlertType.CONFIRMATION,
                String.format("Êtes-vous sûr de vouloir supprimer définitivement l'adoptant :\n\n" +
                             "• Nom : %s %s\n" +
                             "• Téléphone : %s\n" +
                             "• Email : %s\n\n" +
                             "⚠️ Cette action est irréversible !",
                             selected.getNom(), selected.getPrenom(), 
                             selected.getTelephone(), selected.getEmail()),
                ButtonType.YES, 
                ButtonType.NO
            );
            
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText("Supprimer définitivement cet adoptant ?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.YES) {
                return; // L'utilisateur a annulé
            }

            // Suppression de l'adoptant
            String nomComplet = selected.getNom() + " " + selected.getPrenom();
            adoptantService.supprimerAdoptant(selected.getId());

            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     "Adoptant supprimé", 
                     String.format("L'adoptant %s a été supprimé avec succès.", nomComplet));

            chargerAdoptants();
            viderChamps();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Échec de la suppression", 
                     "Une erreur est survenue lors de la suppression de l'adoptant : " + e.getMessage());
        }
    }

    private void chargerAdoptants() {
        adoptants.setAll(adoptantService.listerAdoptants());
        adoptantTable.setItems(adoptants);
    }

    private void remplirChamps(Adoptant adoptant) {
        if (adoptant != null) {
            // Mettre à jour les champs
            nomField.setText(adoptant.getNom());
            prenomField.setText(adoptant.getPrenom());
            telephoneField.setText(adoptant.getTelephone());
            emailField.setText(adoptant.getEmail());
            
            // Sélectionner l'élément dans le tableau
            adoptantTable.getSelectionModel().select(adoptant);
            adoptantTable.scrollTo(adoptant);
        }
    }

    private void viderChamps() {
        // Effacer les champs
        nomField.clear();
        prenomField.clear();
        telephoneField.clear();
        emailField.clear();
        
        // Désélectionner l'élément dans le tableau
        adoptantTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte
     * @param type Type d'alerte (ERROR, WARNING, INFORMATION, etc.)
     * @param title Titre de la fenêtre
     * @param header En-tête du message
     * @param content Contenu détaillé du message
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
