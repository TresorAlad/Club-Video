package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.AbonneDAO;
import com.videocassette.dao.CarteAbonneDAO;
import com.videocassette.dao.UtilisateurDAO;
import com.videocassette.model.Abonne;
import com.videocassette.model.CarteAbonne;
import com.videocassette.model.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Contrôleur pour la page d'inscription.
 */
public class RegisterController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final AbonneDAO abonneDAO = new AbonneDAO();
    private final CarteAbonneDAO carteAbonneDAO = new CarteAbonneDAO();

    @FXML
    public void handleRegister() {
        String nom = nomField.getText().trim();
        String adresse = adresseField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || adresse.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        if (password.length() < 4) {
            errorLabel.setText("Le mot de passe est trop court.");
            return;
        }

        // 1. Créer l'utilisateur
        Utilisateur user = new Utilisateur(nom, email, password);
        if (utilisateurDAO.create(user)) {
            // 2. Créer l'abonné lié
            Abonne abonne = new Abonne(nom, adresse, LocalDate.now(), LocalDate.now());
            abonne.setIdUtilisateur(user.getIdUtilisateur());

            if (abonneDAO.create(abonne)) {
                // 3. Créer la carte d'abonné
                CarteAbonne carte = new CarteAbonne();
                carte.setIdAbonne(abonne.getIdAbonne());
                carteAbonneDAO.create(carte);

                errorLabel.setStyle("-fx-text-fill: green;");
                errorLabel.setText("Inscription réussie ! Redirection");

                // Rediriger vers la connexion après 1.5s
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(this::goToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                errorLabel.setText("Erreur lors de la création du profil abonné.");
            }
        } else {
            errorLabel.setText("Cet email est déjà utilisé.");
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
}
