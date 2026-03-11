package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.CategorieDAO;
import com.videocassette.model.Categorie;
import com.videocassette.util.DataTransfer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Le CategoryFormController gère le formulaire pour créer ou modifier une catégorie (ex: Action, Comédie).
 */
public class CategoryFormController {

    @FXML private Label pageTitle; // Le titre de la page
    @FXML private TextField libelleField; // Le champ pour le nom de la catégorie

    private final CategorieDAO categorieDAO = new CategorieDAO();
    private Categorie existingCategory; // L'objet catégorie si on est en train de modifier

    /**
     * Initialisation : On vérifie si on a reçu une catégorie à modifier.
     */
    @FXML
    public void initialize() {
        if (DataTransfer.has("editCategory")) {
            existingCategory = (Categorie) DataTransfer.remove("editCategory");
            pageTitle.setText("Modifier la catégorie");
            libelleField.setText(existingCategory.getLibelleCategorie());
        } else {
            pageTitle.setText("Ajouter une catégorie");
        }
    }

    /**
     * Action déclenchée par le bouton "Enregistrer".
     */
    @FXML
    private void handleSave() throws IOException {
        String libelle = libelleField.getText().trim();

        // 1. On vérifie que le nom n'est pas vide
        if (libelle.isEmpty()) {
            alerte("Veuillez saisir un libellé.");
            return;
        }

        // 2. On crée ou on met à jour dans la base de données
        if (existingCategory == null) {
            categorieDAO.create(new Categorie(libelle));
        } else {
            existingCategory.setLibelleCategorie(libelle);
            categorieDAO.update(existingCategory);
        }

        // 3. On retourne au tableau de bord
        App.changerVue("/com/videocassette/views/dashboard.fxml");
    }

    /**
     * Action déclenchée par le bouton "Annuler".
     */
    @FXML
    private void handleCancel() throws IOException {
        App.changerVue("/com/videocassette/views/dashboard.fxml");
    }

    private void alerte(String m) {
        new Alert(Alert.AlertType.WARNING, m).showAndWait();
    }
}
