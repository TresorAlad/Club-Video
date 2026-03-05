package com.videocassette.model;

import com.videocassette.dao.CategorieDAO;
import com.videocassette.dao.LocationDAO;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe Cassette - Représente une cassette vidéo.
 * Correspond à la classe 'cassette' du diagramme de classes.
 */
public class Cassette {

    private int idCassette;
    private String titre;
    private String duree;
    private int idCategorie;
    private String categorieNom;
    private String prix;
    private LocalDate dateAchat;

    // ======================== Constructeurs ========================

    public Cassette() {
    }

    public Cassette(int idCassette, String titre, String duree, int idCategorie, String prix, LocalDate dateAchat) {
        this.idCassette = idCassette;
        this.titre = titre;
        this.duree = duree;
        this.idCategorie = idCategorie;
        this.prix = prix;
        this.dateAchat = dateAchat;
    }

    public Cassette(String titre, String duree, int idCategorie, String prix, LocalDate dateAchat) {
        this.titre = titre;
        this.duree = duree;
        this.idCategorie = idCategorie;
        this.prix = prix;
        this.dateAchat = dateAchat;
    }

    // ======================== Getters et Setters ========================

    public int getIdCassette() {
        return idCassette;
    }

    public void setIdCassette(int idCassette) {
        this.idCassette = idCassette;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getCategorieNom() {
        return categorieNom;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    // ======================== Méthodes du diagramme ========================

    /**
     * Vérifie si la cassette est disponible (pas en location active).
     * Méthode estDisponible() du diagramme de classes.
     */
    public boolean estDisponible() {
        LocationDAO locationDAO = new LocationDAO();
        List<Location> locationsActives = locationDAO.getByCassette(this.idCassette);
        for (Location loc : locationsActives) {
            if (loc.estActive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retourne la catégorie de cette cassette.
     * Méthode getCategorie() du diagramme de classes.
     */
    public Categorie getCategorie() {
        CategorieDAO categorieDAO = new CategorieDAO();
        return categorieDAO.getById(this.idCategorie);
    }

    @Override
    public String toString() {
        return titre + " (" + duree + ")";
    }
}
