package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.AbonneDAO;
import com.videocassette.model.Abonne;
import com.videocassette.model.Cassette;
import com.videocassette.util.DataTransfer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;

/*
 Ce contrôleur gère la location d'un film spécifiquement choisi depuis le
 catalogue (bouton "Louer").
 Il demande de saisir le code de l'abonné et la date de retour prévue pour
 finaliser la location.
 */
public class RentCassetteController {

    @FXML
    private Label cassetteTitleLabel; // Affiche le titre du film sélectionné
    @FXML
    private TextField codeAbonneField; // Zone pour taper le code (ex: CLUB123)
    @FXML
    private DatePicker dateRetourPicker; // Date de retour prévue

    private Cassette cassette; // Le film qu'on veut louer
    private final AbonneDAO abonneDAO = new AbonneDAO();

    /*
     Initialisation : on récupère le film envoyé par le Dashboard via
     DataTransfer.
     */
    @FXML
    public void initialize() {
        if (DataTransfer.has("rentCassette")) {
            cassette = (Cassette) DataTransfer.remove("rentCassette");
            cassetteTitleLabel.setText("Louer : " + cassette.getTitre());
        }
        // Par défaut, retour prévu dans 2 jours
        dateRetourPicker.setValue(LocalDate.now().plusDays(2));
    }

    /*Tente de valider la location avec le code abonné saisi. */
    @FXML
    private void handleRent() throws IOException {
        String code = codeAbonneField.getText().trim();
        if (code.isEmpty()) {
            alerte("Veuillez saisir un code abonné.");
            return;
        }

        LocalDate dateRetour = dateRetourPicker.getValue();
        if (dateRetour == null) {
            alerte("Veuillez sélectionner une date de retour prévue.");
            return;
        }
        if (dateRetour.isBefore(LocalDate.now())) {
            alerte("La date de retour prévue ne peut pas être dans le passé.");
            return;
        }

        // 1. Chercher l'abonné dans la base via son code unique
        Abonne abonne = abonneDAO.getByCode(code);
        if (abonne == null) {
            alerte("Code abonné inconnu. Veuillez vérifier (ex: CLUB456).");
            return;
        }

        // 2. Tenter de louer le film avec la date de retour prévue
        if (abonne.louerCassette(cassette.getIdCassette(), dateRetour)) {
            alerteSuccess("Location enregistrée avec succès !");
            // On retourne au menu principal après la réussite
            App.changerVue("/com/videocassette/views/dashboard.fxml");
        } else {
            // Échec si l'abonné a déjà 3 films ou si le film est déjà loué
            alerte("Impossible de louer (max 3 locations actives ou cassette indisponible).");
        }
    }

    /*Annuler et revenir au menu principal. */
    @FXML
    private void handleCancel() throws IOException {
        App.changerVue("/com/videocassette/views/dashboard.fxml");
    }

    private void alerte(String m) {
        new Alert(Alert.AlertType.WARNING, m).showAndWait();
    }

    private void alerteSuccess(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }
}
