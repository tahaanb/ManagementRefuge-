package ma.refuge.controller;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import ma.refuge.model.Animal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageController {

    private static final String IMAGE_DIRECTORY = "resources/images/animals/";
    private static final String DEFAULT_IMAGE = "/images/default-animal.png";

    static {
        // Créer le répertoire des images s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(IMAGE_DIRECTORY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvre un sélecteur de fichier pour choisir une image
     */
    public static File selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo de l'animal");

        // Filtres pour les images
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Images (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"
        );
        fileChooser.getExtensionFilters().add(imageFilter);

        return fileChooser.showOpenDialog(null);
    }

    /**
     * Sauvegarde l'image sélectionnée dans le répertoire du projet
     */
    public static String saveImage(File sourceFile, int animalId) {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }

        try {
            // Créer un nom de fichier unique
            String extension = getFileExtension(sourceFile.getName());
            String fileName = "animal_" + animalId + "_" + System.currentTimeMillis() + extension;
            Path destination = Paths.get(IMAGE_DIRECTORY + fileName);

            // Copier le fichier
            Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            showError("Erreur lors de la sauvegarde de l'image", e.getMessage());
            return null;
        }
    }

    /**
     * Charge une image pour l'affichage
     */
    public static Image loadImage(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) {
            return new Image(ImageController.class.getResourceAsStream(DEFAULT_IMAGE));
        }

        try {
            File imageFile = new File(IMAGE_DIRECTORY + photoPath);
            if (imageFile.exists()) {
                return new Image(imageFile.toURI().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Image(ImageController.class.getResourceAsStream(DEFAULT_IMAGE));
    }

    /**
     * Crée un ImageView configuré pour afficher une image d'animal
     */
    public static ImageView createImageView(String photoPath, double width, double height) {
        ImageView imageView = new ImageView(loadImage(photoPath));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    /**
     * Supprime une image du système de fichiers
     */
    public static boolean deleteImage(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(IMAGE_DIRECTORY + photoPath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot > 0) ? fileName.substring(lastDot) : ".jpg";
    }

    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}