package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.AbonneDAO;
import com.videocassette.dao.CassetteDAO;
import com.videocassette.dao.CategorieDAO;
import com.videocassette.model.Abonne;
import com.videocassette.model.Cassette;
import com.videocassette.model.Categorie;
import com.videocassette.util.DataTransfer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/*
 Le ConfirmDeleteController gère la page qui demande "Êtes-vous sûr ?" avant de supprimer.
 Il s'adapte selon qu'on veuille supprimer un film, un abonné ou une catégorie.
 */
public class ConfirmDeleteController {

    @FXML private Label confirmTitle; // Le titre de la page
    @FXML private Label confirmMessage; // La question posée

    private Object target; // L'objet qu'on veut supprimer (Cassette, Abonne ou Categorie)
    private String type; // Le type d'objet (ex: "CASSETTE")

    /*
     Initialisation : On récupère l'objet à supprimer qui a été "transféré" par la page précédente.
     */
    @FXML
    public void initialize() {
        if (DataTransfer.has("deleteTarget") && DataTransfer.has("deleteType")) {
            target = DataTransfer.remove("deleteTarget");
            type = (String) DataTransfer.remove("deleteType");

            // On change le texte affiché selon ce qu'on supprime
            switch (type) {
                case "CASSETTE" -> {
                    Cassette c = (Cassette) target;
                    confirmTitle.setText("Supprimer la cassette");
                    confirmMessage.setText("Voulez-vous vraiment supprimer la cassette \"" + c.getTitre() + "\" ?");
                }
                case "ABONNE" -> {
                    Abonne a = (Abonne) target;
                    confirmTitle.setText("Supprimer l'abonné");
                    confirmMessage.setText("Voulez-vous vraiment supprimer l'abonné \"" + a.getNomAbonne() + "\" ?");
                }
                case "CATEGORIE" -> {
                    Categorie cat = (Categorie) target;
                    confirmTitle.setText("Supprimer la catégorie");
                    confirmMessage.setText("Voulez-vous vraiment supprimer la catégorie \"" + cat.getLibelleCategorie() + "\" ?");
                }
            }
        }
    }

    /*
     Action déclenchée quand on clique sur "Confirmer la suppression".
     */
    @FXML
    private void handleConfirm() throws IOException {
        if (target != null && type != null) {
            // On appelle le bon DAO pour faire le nettoyage dans la base de données
            switch (type) {
                case "CASSETTE" -> new CassetteDAO().delete(((Cassette) target).getIdCassette());
                case "ABONNE" -> new AbonneDAO().delete(((Abonne) target).getIdAbonne());
                case "CATEGORIE" -> new CategorieDAO().delete(((Categorie) target).getIdCategorie());
            }
        }
        // Une fois fini, on repart sur le tableau de bord
        App.changerVue("/com/videocassette/views/dashboard.fxml");
    }

    /*
     Action déclenchée quand on clique sur "Annuler".
     */
    @FXML
    private void handleCancel() throws IOException {
        // On ne fait rien et on revient au tableau de bord
        App.changerVue("/com/videocassette/views/dashboard.fxml");
    }
}
