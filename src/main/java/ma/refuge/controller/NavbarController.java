package ma.refuge.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class NavbarController {

    @FXML
    private BorderPane root; // le BorderPane parent défini dans Dashboard.fxml, Animal.fxml, etc.

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxml));
            Pane view = loader.load();
            root.setCenter(view); // remplace seulement le contenu central
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goDashboard() { loadView("dashboard.fxml"); }

    @FXML
    public void goAnimals() { loadView("animals.fxml"); }

    @FXML
    public void goAdoptants() { loadView("adoptants.fxml"); }

    @FXML
    public void goHistorique() { loadView("historique.fxml"); }
}
