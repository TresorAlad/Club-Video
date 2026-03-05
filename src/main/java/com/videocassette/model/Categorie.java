package com.videocassette.model;

import com.videocassette.dao.CassetteDAO;

import java.util.List;

/**
 * Classe Categorie - Représente une catégorie de cassettes vidéo.
 * Correspond à la classe 'categorie' du diagramme de classes.
 */
public class Categorie {

    private int idCategorie;
    private String libelleCategorie;

    // ======================== Constructeurs ========================

    public Categorie() {
    }

    public Categorie(int idCategorie, String libelleCategorie) {
        this.idCategorie = idCategorie;
        this.libelleCategorie = libelleCategorie;
    }

    public Categorie(String libelleCategorie) {
        this.libelleCategorie = libelleCategorie;
    }

    // ======================== Getters et Setters ========================

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getLibelleCategorie() {
        return libelleCategorie;
    }

    public void setLibelleCategorie(String libelleCategorie) {
        this.libelleCategorie = libelleCategorie;
    }

    // ======================== Méthodes du diagramme ========================

    /**
     * Retourne la liste des cassettes appartenant à cette catégorie.
     * Méthode getCassettes() du diagramme de classes.
     */
    public List<Cassette> getCassettes() {
        CassetteDAO cassetteDAO = new CassetteDAO();
        return cassetteDAO.getByCategorie(this.idCategorie);
    }

    @Override
    public String toString() {
        return libelleCategorie;
    }
}
