package com.videocassette.controller;

import com.videocassette.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Le LandingController gère la toute première page que l'on voit (la page d'accueil).
 * C'est ici que l'utilisateur choisit s'il veut se connecter ou s'inscrire.
 */
public class LandingController {

    @FXML
    private VBox contactBox; // Une boîte qui contient les infos de contact

    /**
     * Cette méthode se lance automatiquement quand la page s'affiche.
     */
    @FXML
    public void initialize() {
        if (contactBox != null) {
            // Au début, on cache les infos de contact pour ne pas encombrer l'écran
            contactBox.setVisible(false);
            contactBox.setManaged(false);
        }
    }

    /**
     * Action déclenchée par le bouton "Se Connecter".
     */
    @FXML
    public void goToLogin() {
        try {
            // On demande à l'App de changer la fenêtre pour montrer la page de login
            App.changerVue("/com/videocassette/views/login-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Action déclenchée par le bouton "Créer un compte".
     */
    @FXML
    public void goToRegister() {
        try {
            // On change vers la page d'inscription
            App.changerVue("/com/videocassette/views/register-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Action déclenchée par le bouton "Contact".
     */
    @FXML
    public void showContactInfo() {
        try {
            // On essaie d'ouvrir la page contact dédiée
            App.changerVue("/com/videocassette/views/contact-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
            
            // Si la page ne marche pas, on affiche une petite boîte de secours (alerte)
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contact du Video Club");
            alert.setHeaderText("Nos coordonnées");
            alert.setContentText("📧 Email : bester@videoclub.org\n" +
                    "📞 Téléphone : +228 99 70 70 99\n" +
                    "📍 Adresse : 123 Rue du Cinéma, Bd 30 Août ADIDOGOME");
            alert.showAndWait();
        }
    }
}
