package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.CassetteDAO;
import com.videocassette.dao.CategorieDAO;
import com.videocassette.model.Cassette;
import com.videocassette.model.Categorie;
import com.videocassette.util.DataTransfer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Le CassetteFormController gère le formulaire pour ajouter ou modifier un film.
 */
public class CassetteFormController {

    @FXML private Label pageTitle; // Titre qui change (Ajouter ou Modifier)
    @FXML private TextField titreField; // Champ pour le titre du film
    @FXML private TextField dureeField; // Champ pour la durée (en minutes)
    @FXML private TextField prixField; // Champ pour le prix de location
    @FXML private ComboBox<Categorie> categorieCombo; // Liste déroulante des catégories
    @FXML private DatePicker dateAchatPicker; // Calendrier pour la date d'achat

    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private Cassette existingCassette; // Si on modifie un film, on le stocke ici

    /**
     * Initialisation : On remplit la liste des catégories et on vérifie si on est en mode "édition".
     */
    @FXML
    public void initialize() {
        // On récupère toutes les catégories de la base pour les mettre dans la liste déroulante
        categorieCombo.setItems(FXCollections.observableArrayList(categorieDAO.getAll()));

        // Si on a reçu une cassette à modifier via DataTransfer
        if (DataTransfer.has("editCassette")) {
            existingCassette = (Cassette) DataTransfer.remove("editCassette");
            pageTitle.setText("Modifier la cassette");
            
            // On remplit les champs avec les infos actuelles du film
            titreField.setText(existingCassette.getTitre());
            dureeField.setText(String.valueOf(existingCassette.getDuree()));
            prixField.setText(String.valueOf(existingCassette.getPrix()));
            dateAchatPicker.setValue(existingCassette.getDateAchat());
            
            // On sélectionne la bonne catégorie dans la liste déroulante
            categorieCombo.getItems().stream()
                    .filter(c -> c.getIdCategorie() == existingCassette.getIdCategorie())
                    .findFirst()
                    .ifPresent(categorieCombo::setValue);
        } else {
            // Sinon on est en mode "Ajout"
            pageTitle.setText("Ajouter une cassette");
            dateAchatPicker.setValue(LocalDate.now()); // Date d'aujourd'hui par défaut
        }
    }

    /**
     * Action déclenchée quand on clique sur "Enregistrer".
     */
    @FXML
    private void handleSave() throws IOException {
        String titre = titreField.getText().trim();
        String dureeStr = dureeField.getText().trim();
        String prixStr = prixField.getText().trim();
        Categorie cat = categorieCombo.getValue();
        LocalDate date = dateAchatPicker.getValue();

        // 1. On vérifie que tout est rempli
        if (titre.isEmpty() || dureeStr.isEmpty() || prixStr.isEmpty() || cat == null || date == null) {
            alerte("Veuillez remplir tous les champs.");
            return;
        }

        try {
            // 2. On transforme les textes en nombres
            int duree = Integer.parseInt(dureeStr);
            double prix = Double.parseDouble(prixStr);

            if (existingCassette == null) {
                // Créer une nouvelle cassette
                Cassette newCassette = new Cassette(titre, duree, cat.getIdCategorie(), prix, date);
                cassetteDAO.create(newCassette);
            } else {
                // Mettre à jour la cassette existante
                existingCassette.setTitre(titre);
                existingCassette.setDuree(duree);
                existingCassette.setIdCategorie(cat.getIdCategorie());
                existingCassette.setPrix(prix);
                existingCassette.setDateAchat(date);
                cassetteDAO.update(existingCassette);
            }

            // Retour au tableau de bord
            App.changerVue("/com/videocassette/views/dashboard.fxml");
        } catch (NumberFormatException e) {
            // Si l'utilisateur a tapé des lettres au lieu de chiffres
            alerte("La durée et le prix doivent être des nombres.");
        }
    }

    /**
     * Action déclenchée par le bouton "Annuler".
     */
    @FXML
    private void handleCancel() throws IOException {
        App.changerVue("/com/videocassette/views/dashboard.fxml");
    }

    /**
     * Petite méthode pratique pour afficher une boîte d'alerte.
     */
    private void alerte(String m) {
        new Alert(Alert.AlertType.WARNING, m).showAndWait();
    }
}
