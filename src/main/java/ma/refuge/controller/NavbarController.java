package ma.refuge.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
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
            Node view = loader.load();
            
            // Si c'est un ScrollPane, on l'ajoute directement
            if (view instanceof ScrollPane) {
                staticRoot.setCenter(view);
            } 
            // Sinon, on vérifie si c'est un Pane
            else if (view instanceof Pane) {
                staticRoot.setCenter(view);
            } 
            // Si ce n'est ni l'un ni l'autre, on l'encapsule dans un Pane
            else {
                Pane container = new Pane(view);
                staticRoot.setCenter(container);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goDashboard() { loadView("dashboard-simple.fxml"); }

    @FXML
    public void goAnimals() { loadView("animals.fxml"); }

    @FXML
    public void goAdoptants() { loadView("adoptants.fxml"); }
    
    @FXML
    public void goFicheSante() { loadView("fiche-sante.fxml"); }

    @FXML
    public void goHistorique() { loadView("historique.fxml"); }
    
    @FXML
    public void handleQuit() {
        if (staticRoot != null && staticRoot.getScene() != null && staticRoot.getScene().getWindow() != null) {
            ((Stage) staticRoot.getScene().getWindow()).close();
        }
    }
}
