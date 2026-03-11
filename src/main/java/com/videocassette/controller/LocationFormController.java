package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.AbonneDAO;
import com.videocassette.dao.CassetteDAO;
import com.videocassette.dao.LocationDAO;
import com.videocassette.model.Abonne;
import com.videocassette.model.Cassette;
import com.videocassette.model.Location;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Ce contrôleur gère le formulaire pour créer une nouvelle location.
 * On y choisit le film, l'abonné, et les dates de début et de fin du prêt.
 */
public class LocationFormController {

    // --- Champs du formulaire ---
    @FXML private ComboBox<Cassette> cassetteCombo; // Liste déroulante des films dispo
    @FXML private ComboBox<Abonne> abonneCombo;    // Liste déroulante des abonnés
    @FXML private DatePicker dateLocationPicker;   // Date de début
    @FXML private DatePicker dateRetourPicker;     // Date prévue de retour (par défaut +2 jours)

    // --- Accès aux données ---
    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final AbonneDAO abonneDAO = new AbonneDAO();
    private final LocationDAO locationDAO = new LocationDAO();

    /** Initialisation : on remplit les listes déroulantes. */
    @FXML
    public void initialize() {
        // On ne propose que les cassettes qui ne sont pas déjà louées
        cassetteCombo.setItems(FXCollections.observableArrayList(cassetteDAO.getAllDisponibles()));
        abonneCombo.setItems(FXCollections.observableArrayList(abonneDAO.getAll()));
        
        dateLocationPicker.setValue(LocalDate.now()); // Aujourd'hui
        dateRetourPicker.setValue(LocalDate.now().plusDays(2)); // Prêt de 2 jours par défaut
    }

    /** Appuyer sur "Enregistrer" pour valider la location. */
    @FXML
    private void handleSave() throws IOException {
        Cassette cass = cassetteCombo.getValue();
        Abonne ab = abonneCombo.getValue();
        LocalDate dLoc = dateLocationPicker.getValue();
        LocalDate dRet = dateRetourPicker.getValue();

        // 1. Vérification que tout est rempli
        if (cass == null || ab == null || dLoc == null || dRet == null) {
            alerte("Veuillez remplir tous les champs.");
            return;
        }

        // 2. Vérification des dates
        if (dRet.isBefore(dLoc)) {
            alerte("La date de retour doit être après la date de location.");
            return;
        }

        // 3. Tentative d'enregistrement dans la base
        // La méthode louerCassette s'occupe de créer la location et de marquer le film comme "indisponible"
        if (ab.louerCassette(cass.getIdCassette())) {
            alerteSuccess("Location enregistrée !");
            App.changerVue("/com/videocassette/views/dashboard.fxml");
        } else {
            // Un abonné ne peut pas avoir plus de 3 locations actives
            alerte("Impossible de louer cette cassette à cet abonné (vérifiez les quotas : max 3 films).");
        }
    }

    /** Annuler et retourner au tableau de bord. */
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
