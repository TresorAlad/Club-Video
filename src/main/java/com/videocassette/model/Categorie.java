package com.videocassette.model;

import com.videocassette.dao.CassetteDAO;

import java.util.List;

/**
 * La classe Categorie permet de classer les films par genre.
 * Exemples : "Action", "Comédie", "Drame", "Horreur".
 * Cela aide les clients à trouver le type de film qu'ils aiment.
 */
public class Categorie {

    // --- Les "Champs" ---

    // Identifiant unique de la catégorie
    private int idCategorie;
    
    // Le nom de la catégorie (ex: "Science-Fiction")
    private String libelleCategorie;

    // --- Les Constructeurs ---
    // (Voir Abonne.java pour le rôle des constructeurs)

    public Categorie() {
    }

    public Categorie(int idCategorie, String libelleCategorie) {
        this.idCategorie = idCategorie;
        this.libelleCategorie = libelleCategorie;
    }

    public Categorie(String libelleCategorie) {
        this.libelleCategorie = libelleCategorie;
    }

    // --- Les Getters et Setters ---

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

    // --- Les Méthodes Métier ---

    /**
     * Permet de savoir quels films font partie de cette catégorie.
     * @return Une liste de toutes les cassettes classées ici.
     */
    public List<Cassette> getCassettes() {
        CassetteDAO cassetteDAO = new CassetteDAO();
        // On demande au "pont" (le DAO) de nous donner les films de cette catégorie
        return cassetteDAO.getByCategorie(this.idCategorie);
    }

    /**
     * Affiche simplement le nom de la catégorie (ex: "Comédie")
     */
    @Override
    public String toString() {
        return libelleCategorie;
    }
}
