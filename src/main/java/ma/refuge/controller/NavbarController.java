package ma.refuge.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class NavbarController {

    private static BorderPane staticRoot;

    public static void setStaticRoot(BorderPane root) {
        staticRoot = root;
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxml));
            Pane view = loader.load();
            staticRoot.setCenter(view); // remplace seulement le contenu central
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goDashboard() { loadView("dashboard-modern.fxml"); }

    @FXML
    public void goAnimals() { loadView("animals-modern.fxml"); }

    @FXML
    public void goAdoptants() { loadView("adoptants.fxml"); }

    @FXML
    public void goHistorique() { loadView("historique.fxml"); }
}
