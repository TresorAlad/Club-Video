package com.videocassette;

import com.videocassette.dao.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/*
 La classe App est le point de départ de tout le programme.
 C'est elle qui "allume" le logiciel et affiche la première fenêtre.
 */
public class App extends Application {

    // On garde une trace de la fenêtre principale pour pouvoir changer ce qu'il y a
    // dedans
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // 1. Charger la toute première page (la page d'accueil)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/videocassette/views/landing-view.fxml"));
        Parent root = loader.load();

        // 3. Créer une "Scène" (le contenu de la fenêtre) avec une taille par défaut
        Scene scene = new Scene(root, 900, 600);

        // 4. Ajouter le fichier de style (CSS) pour que ce soit joli (couleurs,
        // boutons, etc.)
        scene.getStylesheets().add(getClass().getResource("/com/videocassette/styles/style.css").toExternalForm());

        // 5. Configurer et afficher la fenêtre
        stage.setTitle("Gestion de Location de Cassettes Vidéo");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    /*
     Permet d'accéder à la fenêtre de n'importe où dans le code.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /*
     Cette méthode magique permet de changer de page.
     
     @param fxmlPath Le chemin vers le fichier de la nouvelle page (ex:
                     "/com/videocassette/views/login-view.fxml")
     */
    public static void changerVue(String fxmlPath) throws IOException {
        URL resource = App.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("Fichier FXML introuvable : " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        // On crée une nouvelle scène plus grande pour le tableau de bord
        Scene scene = new Scene(root, 1100, 700);

        // On n'oublie pas de remettre le style CSS
        URL styleResource = App.class.getResource("/com/videocassette/styles/style.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen(); // On recentre la fenêtre sur l'écran
    }

    /*
     Même chose que changerVue, mais on peut choisir la taille de la fenêtre.
     */
    public static void changerVue(String fxmlPath, double width, double height) throws IOException {
        URL resource = App.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("Fichier FXML introuvable : " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);

        URL styleResource = App.class.getResource("/com/videocassette/styles/style.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    /*
     La méthode main est celle que l'ordinateur appelle en premier.
     */
    public static void main(String[] args) {
        launch(args); // Lance l'application JavaFX
    }
}
