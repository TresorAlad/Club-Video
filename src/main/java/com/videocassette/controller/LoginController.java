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

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final AbonneDAO abonneDAO = new AbonneDAO();

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur user = utilisateurDAO.login(email, password);

        if (user != null) {
            Session.setCurrentUser(user);

            // Toujours essayer de charger le profil abonne associé
            Abonne abonne = abonneDAO.getByUtilisateurId(user.getIdUtilisateur());
            if (abonne != null) {
                Session.setCurrentAbonne(abonne);
            }

            try {
                App.changerVue("/com/videocassette/views/dashboard.fxml");
            } catch (IOException e) {
                errorLabel.setText("Erreur lors du chargement de la page.");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Email ou mot de passe incorrect.");
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
}
