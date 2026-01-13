package ma.refuge;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import ma.refuge.controller.NavbarController;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le layout principal avec la navbar
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-layout.fxml"));
        BorderPane root = loader.load();
        
        // Initialiser le contrôleur de la navbar avec la racine
        NavbarController navbarController = loader.getController();
        NavbarController.setStaticRoot(root);
        
        // Charger le dashboard par défaut
        navbarController.goDashboard();
        
        // Configurer la scène
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
        
        // Configurer la fenêtre
        primaryStage.setTitle("Refuge Management System");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
