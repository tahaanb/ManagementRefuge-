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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Champs obligatoires manquants");
            alert.setContentText("Tous les champs (Nom, Prénom, Téléphone, Email) sont obligatoires.");
            alert.showAndWait();
            return;
        }

        // Validation du format email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.matches(emailRegex, email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Email invalide");
            alert.setContentText("Veuillez saisir une adresse email valide (ex: nom@domaine.com).");
            alert.showAndWait();
            return;
        }

        // Validation du téléphone (format marocain simple)
        String phoneRegex = "^(\\+212|0)[6-7][0-9]{8}$";
        if (!Pattern.matches(phoneRegex, telephone)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Téléphone invalide");
            alert.setContentText("Le numéro de téléphone doit être au format marocain (ex: 0612345678 ou +212612345678).");
            alert.showAndWait();
            return;
        }

        // Validation de la longueur des champs
        if (nom.length() > 50 || prenom.length() > 50 || telephone.length() > 20 || email.length() > 100) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Données trop longues");
            alert.setContentText("Vérifiez la longueur des champs saisie.");
            alert.showAndWait();
            return;
        }

        // Vérification que l'adoptant n'existe pas déjà (même nom + prénom + téléphone)
        boolean adoptantExists = adoptantService.listerAdoptants().stream()
                .anyMatch(a -> a.getNom().equalsIgnoreCase(nom) &&
                              a.getPrenom().equalsIgnoreCase(prenom) &&
                              a.getTelephone().equals(telephone));
        if (adoptantExists) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Adoptant déjà existant");
            alert.setContentText("Un adoptant avec le même nom, prénom et numéro de téléphone existe déjà dans le système.");
            alert.showAndWait();
            return;
        }

        // Création et sauvegarde de l'adoptant
        try {
            Adoptant adoptant = new Adoptant(nom, prenom, telephone, email);
            adoptantService.ajouterAdoptant(adoptant);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Adoptant ajouté");
            successAlert.setContentText("L'adoptant \"" + nom + " " + prenom + "\" a été ajouté avec succès.");
            successAlert.showAndWait();

            chargerAdoptants();
            viderChamps();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout");
            alert.setContentText("Une erreur s'est produite lors de l'ajout de l'adoptant : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void modifierAdoptant() {
        Adoptant selected = adoptantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun adoptant sélectionné");
            alert.setContentText("Veuillez sélectionner un adoptant dans le tableau avant de modifier.");
            alert.showAndWait();
            return;
        }

        // Validation des champs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String email = emailField.getText().trim();

        // Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Champs obligatoires manquants");
            alert.setContentText("Tous les champs (Nom, Prénom, Téléphone, Email) sont obligatoires.");
            alert.showAndWait();
            return;
        }

        // Validation du format email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.matches(emailRegex, email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Email invalide");
            alert.setContentText("Veuillez saisir une adresse email valide (ex: nom@domaine.com).");
            alert.showAndWait();
            return;
        }

        // Validation du téléphone
        String phoneRegex = "^(\\+212|0)[6-7][0-9]{8}$";
        if (!Pattern.matches(phoneRegex, telephone)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Téléphone invalide");
            alert.setContentText("Le numéro de téléphone doit être au format marocain (ex: 0612345678 ou +212612345678).");
            alert.showAndWait();
            return;
        }

        // Validation de la longueur des champs
        if (nom.length() > 50 || prenom.length() > 50 || telephone.length() > 20 || email.length() > 100) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Données trop longues");
            alert.setContentText("Vérifiez la longueur des champs saisie.");
            alert.showAndWait();
            return;
        }

        // Vérification que l'email n'existe pas déjà (sauf pour l'adoptant actuel)
        boolean emailExists = adoptantService.listerAdoptants().stream()
                .anyMatch(a -> a.getEmail().equalsIgnoreCase(email) && a.getId() != selected.getId());
        if (emailExists) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Email déjà utilisé");
            alert.setContentText("Cette adresse email est déjà utilisée par un autre adoptant.");
            alert.showAndWait();
            return;
        }

        // Modification de l'adoptant
        try {
            selected.setNom(nom);
            selected.setPrenom(prenom);
            selected.setTelephone(telephone);
            selected.setEmail(email);
            adoptantService.modifierAdoptant(selected);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Adoptant modifié");
            successAlert.setContentText("L'adoptant \"" + nom + " " + prenom + "\" a été modifié avec succès.");
            successAlert.showAndWait();

            chargerAdoptants();
            viderChamps();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la modification");
            alert.setContentText("Une erreur s'est produite lors de la modification de l'adoptant : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void supprimerAdoptant() {
        Adoptant selected = adoptantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun adoptant sélectionné");
            alert.setContentText("Veuillez sélectionner un adoptant dans le tableau avant de supprimer.");
            alert.showAndWait();
            return;
        }

        // Vérification si l'adoptant a des animaux adoptés
        if (selected.getAnimauxAdoptes() != null && !selected.getAnimauxAdoptes().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suppression impossible");
            alert.setHeaderText("Adoptant avec animaux");
            alert.setContentText("Cet adoptant a " + selected.getAnimauxAdoptes().size() +
                " animal(aux) adopté(s). Vous ne pouvez pas le supprimer tant qu'il a des animaux à sa charge.");
            alert.showAndWait();
            return;
        }

        // Confirmation de suppression
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer l'adoptant");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'adoptant \"" +
            selected.getNom() + " " + selected.getPrenom() + "\" ?\n\nCette action est irréversible.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        // Suppression de l'adoptant
        try {
            adoptantService.supprimerAdoptant(selected.getId());

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText("Adoptant supprimé");
            successAlert.setContentText("L'adoptant \"" + selected.getNom() + " " + selected.getPrenom() + "\" a été supprimé avec succès.");
            successAlert.showAndWait();

            chargerAdoptants();
            viderChamps();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la suppression");
            alert.setContentText("Une erreur s'est produite lors de la suppression de l'adoptant : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void chargerAdoptants() {
        adoptants.setAll(adoptantService.listerAdoptants());
        adoptantTable.setItems(adoptants);
    }

    private void remplirChamps(Adoptant adoptant) {
        nomField.setText(adoptant.getNom());
        prenomField.setText(adoptant.getPrenom());
        telephoneField.setText(adoptant.getTelephone());
        emailField.setText(adoptant.getEmail());
    }

    private void viderChamps() {
        nomField.clear();
        prenomField.clear();
        telephoneField.clear();
        emailField.clear();
    }
}
