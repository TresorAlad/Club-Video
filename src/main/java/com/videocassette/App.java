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

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initialiser la base de données
        DatabaseConnection.getInstance().initialiserBase();

        // Charger la vue d'accueil (landing page)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/videocassette/views/landing-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/com/videocassette/styles/style.css").toExternalForm());

        stage.setTitle("Gestion de Location de Cassettes Vidéo");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void changerVue(String fxmlPath) throws IOException {
        URL resource = App.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("Fichier FXML introuvable : " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 700);

        URL styleResource = App.class.getResource("/com/videocassette/styles/style.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

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

    public static void main(String[] args) {
        launch(args);
    }
}
