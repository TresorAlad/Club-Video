package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.AbonneDAO;
import com.videocassette.dao.CarteAbonneDAO;
import com.videocassette.model.Abonne;
import com.videocassette.model.CarteAbonne;
import com.videocassette.util.DataTransfer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

/*
 Le AbonneFormController gère le formulaire pour ajouter ou modifier un abonné.
 Il permet aussi de générer une carte de membre en PDF.
 */
public class AbonneFormController {

    @FXML private Label pageTitle;
    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField codeField; // Le code unique (ex: CLUB123)
    @FXML private DatePicker dateAbonnementPicker;
    @FXML private DatePicker dateEntreePicker;

    private final AbonneDAO abonneDAO = new AbonneDAO();
    private final CarteAbonneDAO carteAbonneDAO = new CarteAbonneDAO();
    private Abonne existingAbonne; // L'abonné qu'on modifie (si c'est le cas)
    private final Random random = new Random(); // Pour générer un code aléatoire

    /*
     Initialisation : On pré-remplit les champs si c'est une modification.
     */
    @FXML
    public void initialize() {
        if (DataTransfer.has("editAbonne")) {
            existingAbonne = (Abonne) DataTransfer.remove("editAbonne");
            pageTitle.setText("Modifier l'abonné");
            nomField.setText(existingAbonne.getNomAbonne());
            adresseField.setText(existingAbonne.getAdresseAbonne());
            codeField.setText(existingAbonne.getCodeAbonne());
            dateAbonnementPicker.setValue(existingAbonne.getDateAbonement());
            dateEntreePicker.setValue(existingAbonne.getDateEntree());
        } else {
            // Mode Ajout : On invente un code et on met les dates du jour
            pageTitle.setText("Ajouter un abonné");
            codeField.setText("CLUB" + (100 + random.nextInt(900)));
            dateAbonnementPicker.setValue(LocalDate.now());
            dateEntreePicker.setValue(LocalDate.now());
        }
    }

    /*
     Action déclenchée par le bouton "Enregistrer".
     */
    @FXML
    private void handleSave() throws IOException {
        String nom = nomField.getText().trim();
        String adresse = adresseField.getText().trim();
        String code = codeField.getText().trim();
        LocalDate dateAb = dateAbonnementPicker.getValue();
        LocalDate dateEnt = dateEntreePicker.getValue();

        // 1. Vérification des champs
        if (nom.isEmpty() || adresse.isEmpty() || code.isEmpty() || dateAb == null || dateEnt == null) {
            alerte("Veuillez remplir tous les champs.");
            return;
        }

        if (existingAbonne == null) {
            // Création d'un nouvel abonné
            Abonne newAbonne = new Abonne(code, nom, adresse, dateAb, dateEnt);
            if (abonneDAO.create(newAbonne)) {
                // On lui crée aussi sa carte automatique dans la base
                CarteAbonne c = new CarteAbonne();
                c.setIdAbonne(newAbonne.getIdAbonne());
                carteAbonneDAO.create(c);

                // On demande s'il veut la carte en PDF
                confirmPdfAndReturn(newAbonne);
            }
        } else {
            // Mise à jour de l'abonné existant
            existingAbonne.setNomAbonne(nom);
            existingAbonne.setAdresseAbonne(adresse);
            existingAbonne.setCodeAbonne(code);
            existingAbonne.setDateAbonement(dateAb);
            existingAbonne.setDateEntree(dateEnt);
            abonneDAO.update(existingAbonne);
            App.changerVue("/com/videocassette/views/dashboard.fxml");
        }
    }

    /*
     Propose de générer le fichier PDF de la carte de membre.
     */
    private void confirmPdfAndReturn(Abonne a) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
            "L'abonné a été ajouté. Voulez-vous générer sa carte de membre PDF ?", 
            ButtonType.YES, ButtonType.NO);
        alert.setTitle("Génération PDF");
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                // On génère le PDF dans une tâche de fond pour ne pas bloquer le logiciel
                new Thread(() -> {
                    com.videocassette.util.PDFUtils.generateMemberCard(a);
                    Platform.runLater(() -> alerteSuccess("Carte PDF générée dans le dossier 'cards'."));
                }).start();
            }
        });
        // On retourne au tableau de bord pendant que le PDF se génère
        App.changerVue("/com/videocassette/views/dashboard.fxml");
    }

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
