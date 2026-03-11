package com.videocassette.controller;

import com.videocassette.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

/*
 Le ContactController gère la page où l'utilisateur peut envoyer un message au club.
 */
public class ContactController {

    @FXML
    private TextField nameField; // Le champ pour le nom

    @FXML
    private TextField emailField; // Le champ pour l'email

    @FXML
    private TextArea messageArea; // La grande zone pour le message

    /*
     Action déclenchée par le bouton "Envoyer".
     */
    @FXML
    public void envoyerMessage() {
        // 1. On vérifie que rien n'est vide
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || messageArea.getText().trim().isEmpty()) {
            // Si un champ manque, on affiche un avertissement
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs avant d'envoyer votre message.");
            alert.showAndWait();
            return;
        }

        // 2. Si tout est bon, on affiche une confirmation (simulée)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Envoyé");
        alert.setHeaderText("Merci pour votre message, " + nameField.getText() + " !");
        alert.setContentText("Nous avons bien reçu votre demande et nous vous contacterons sous peu à l'adresse " + emailField.getText() + ".");
        alert.showAndWait();

        // 3. On vide le formulaire pour le prochain message
        nameField.clear();
        emailField.clear();
        messageArea.clear();
    }

    /*
     Retour à la page d'accueil.
     */
    @FXML
    public void goToLanding() {
        try {
            App.changerVue("/com/videocassette/views/landing-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
