package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.AbonneDAO;
import com.videocassette.dao.UtilisateurDAO;
import com.videocassette.model.Abonne;
import com.videocassette.model.Utilisateur;
import com.videocassette.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/*
 Le LoginController gère l'écran de connexion.
 Il vérifie si l'email et le mot de passe sont corrects pour laisser entrer l'utilisateur.
 */
public class LoginController {

    @FXML
    private TextField emailField; // Le champ où on tape l'email

    @FXML
    private PasswordField passwordField; // Le champ où on tape le mot de passe (caché par des points)

    @FXML
    private TextField passwordTextField; // Un champ de texte normal pour voir le mot de passe en clair

    @FXML
    private javafx.scene.control.Button togglePasswordBtn; // Le bouton "Voir/Masquer"

    @FXML
    private Label errorLabel; // Le texte rouge qui s'affiche en cas d'erreur

    private boolean isPasswordVisible = false;

    /*
     Initialisation : On lie les deux champs de mot de passe (caché et visible).
     Comme ça, ce qu'on tape dans l'un s'écrit automatiquement dans l'autre.
     */
    @FXML
    public void initialize() {
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    /*
     Action déclenchée par le bouton "Voir/Masquer".
     */
    @FXML
    public void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
            togglePasswordBtn.setText("Masquer");
        } else {
            passwordTextField.setVisible(false);
            passwordField.setVisible(true);
            togglePasswordBtn.setText("Voir");
        }
    }

    // On prépare les outils pour discuter avec la base de données
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final AbonneDAO abonneDAO = new AbonneDAO();

    /*
     Action déclenchée quand on clique sur "Se Connecter".
     */
    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // 1. On vérifie que les champs ne sont pas vides
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // 2. On demande au DAO de vérifier dans la base de données
        Utilisateur user = utilisateurDAO.login(email, password);

        if (user != null) {
            // 3. Si c'est bon, on enregistre l'utilisateur dans la Session
            Session.setCurrentUser(user);

            // 4. On récupère aussi son profil d'abonné s'il existe
            Abonne abonne = abonneDAO.getByUtilisateurId(user.getIdUtilisateur());
            if (abonne != null) {
                Session.setCurrentAbonne(abonne);
            }

            // 5. On passe à la page principale (Dashboard)
            try {
                App.changerVue("/com/videocassette/views/dashboard.fxml");
            } catch (IOException e) {
                errorLabel.setText("Erreur lors du chargement de la page.");
                e.printStackTrace();
            }
        } else {
            // 6. Sinon, on affiche un message d'erreur
            errorLabel.setText("Email ou mot de passe incorrect.");
        }
    }

    /*
     Redirection vers la page d'inscription.
     */
    @FXML
    public void goToRegister() {
        try {
            App.changerVue("/com/videocassette/views/register-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
