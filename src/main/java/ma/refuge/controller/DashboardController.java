package ma.refuge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ma.refuge.service.AnimalService;
import ma.refuge.service.AdoptantService;

public class DashboardController {

    @FXML private Label animalCount;
    @FXML private Label adoptantCount;

    private final AnimalService animalService = new AnimalService();
    private final AdoptantService adoptantService = new AdoptantService();

    @FXML
    public void initialize() {
        animalCount.setText(String.valueOf(animalService.listerAnimaux().size()));
        adoptantCount.setText(String.valueOf(adoptantService.listerAdoptants().size()));
    }
}
