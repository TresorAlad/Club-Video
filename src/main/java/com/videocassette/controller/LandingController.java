package com.videocassette.controller;

import com.videocassette.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LandingController {

    @FXML
    private VBox contactBox;

    @FXML
    public void initialize() {
        if (contactBox != null) {
            contactBox.setVisible(false);
            contactBox.setManaged(false);
        }
    }

    @FXML
    public void goToLogin() {
        try {
            App.changerVue("/com/videocassette/views/login-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToRegister() {
        try {
            App.changerVue("/com/videocassette/views/register-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showContactInfo() {
        if (contactBox != null) {
            boolean isVisible = contactBox.isVisible();
            contactBox.setVisible(!isVisible);
            contactBox.setManaged(!isVisible);
        } else {
            // Fallback if the FXML doesn't have the contactBox yet
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contact du Video Club");
            alert.setHeaderText("Nos coordonnées");
            alert.setContentText("📧 Email : contact@videoclub.com\n" +
                    "📞 Téléphone : +33 1 23 45 67 89\n" +
                    "📍 Adresse : 123 Rue du Cinéma, 75000 Paris");
            alert.showAndWait();
        }
    }
}
