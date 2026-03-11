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
 * Le RegisterController s'occupe de la création des nouveaux comptes.
 * Quand on s'inscrit, il crée trois choses : un Utilisateur, un Abonné et une Carte.
 */
public class RegisterController {

    // On récupère les informations tapées dans le formulaire via @FXML
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

    // Les "ponts" vers la base de données
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final AbonneDAO abonneDAO = new AbonneDAO();
    private final CarteAbonneDAO carteAbonneDAO = new CarteAbonneDAO();

    /**
     * Action déclenchée quand on clique sur "S'inscrire".
     */
    @FXML
    public void handleRegister() {
        String nom = nomField.getText().trim();
        String adresse = adresseField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        // 1. Vérification : Est-ce que tout est rempli ?
        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || adresse.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // 2. Vérification : Est-ce que les mots de passe sont identiques ?
        if (!password.equals(confirm)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        // 3. Sécurité : Mot de passe pas trop court
        if (password.length() < 4) {
            errorLabel.setText("Le mot de passe est trop court.");
            return;
        }

        // --- ÉTape 1 : Créer le compte Utilisateur (Email / MDP) ---
        Utilisateur user = new Utilisateur(nom, email, password);
        if (utilisateurDAO.create(user)) {
            
            // --- Étape 2 : Créer la fiche Abonné liée à cet utilisateur ---
            Abonne abonne = new Abonne(nom, adresse, LocalDate.now(), LocalDate.now());
            abonne.setIdUtilisateur(user.getIdUtilisateur());

            if (abonneDAO.create(abonne)) {
                
                // --- Étape 3 : Créer sa Carte de membre automatique ---
                CarteAbonne carte = new CarteAbonne();
                carte.setIdAbonne(abonne.getIdAbonne());
                carteAbonneDAO.create(carte);

                errorLabel.setStyle("-fx-text-fill: green;");
                errorLabel.setText("Inscription réussie ! Redirection...");

                // On attend 1.5 seconde avant d'aller à la page de connexion
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

    /**
     * Retourner à la page de connexion.
     */
    @FXML
    public void goToLogin() {
        try {
            App.changerVue("/com/videocassette/views/login-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
