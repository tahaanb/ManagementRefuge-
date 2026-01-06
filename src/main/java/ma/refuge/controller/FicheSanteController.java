package ma.refuge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.refuge.model.FicheSante;
import ma.refuge.service.FicheSanteService;

import java.time.LocalDate;

public class FicheSanteController {

    @FXML private DatePicker datePicker;
    @FXML private TextArea descriptionArea;
    @FXML private TextField documentField;

    private final FicheSanteService ficheSanteService = new FicheSanteService();

    @FXML
    public void ajouterFicheSante(int animalId) {
        FicheSante fiche = new FicheSante();
        fiche.setDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
        fiche.setDescription(descriptionArea.getText());
        fiche.setDocumentPath(documentField.getText());

        ficheSanteService.ajouterFicheSante(animalId, fiche);
        viderChamps();
    }

    private void viderChamps() {
        descriptionArea.clear();
        documentField.clear();
        datePicker.setValue(null);
    }
}
