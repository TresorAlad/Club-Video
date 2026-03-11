package com.videocassette.model;

import com.videocassette.dao.CategorieDAO;
import com.videocassette.dao.LocationDAO;

import java.time.LocalDate;
import java.util.List;

/**
 * La classe Cassette représente un film ou une vidéo disponible à la location.
 * Elle contient les informations techniques du film (titre, durée) et son prix.
 */
public class Cassette {

    // --- Les "Champs" ---

    // Identifiant unique (numéro automatique)
    private int idCassette;
    
    // Le titre du film
    private String titre;
    
    // La durée du film en minutes
    private Integer duree;
    
    // L'identifiant de la catégorie (ex: Action, Comédie)
    private int idCategorie;
    
    // Le nom de la catégorie (pour ne pas avoir juste un numéro)
    private String categorieNom;
    
    // Le prix de la location
    private Double prix;
    
    // La date à laquelle le club a acheté cette cassette
    private LocalDate dateAchat;
    
    // La date de la dernière fois qu'elle a été louée
    private String derniereDateLocation;

    // --- Les Constructeurs ---

    public Cassette() {
    }

    // Utilisé quand on connaît déjà l'ID (ex: chargement depuis la base)
    public Cassette(int idCassette, String titre, Integer duree, int idCategorie, Double prix, LocalDate dateAchat) {
        this.idCassette = idCassette;
        this.titre = titre;
        this.duree = duree;
        this.idCategorie = idCategorie;
        this.prix = prix;
        this.dateAchat = dateAchat;
    }

    // Utilisé pour créer une nouvelle cassette (l'ID sera généré par la base)
    public Cassette(String titre, Integer duree, int idCategorie, Double prix, LocalDate dateAchat) {
        this.titre = titre;
        this.duree = duree;
        this.idCategorie = idCategorie;
        this.prix = prix;
        this.dateAchat = dateAchat;
    }

    // --- Les Getters et Setters ---
    // (Permettent d'accéder aux données privées en toute sécurité)

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

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
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

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public String getDerniereDateLocation() {
        return derniereDateLocation;
    }

    public void setDerniereDateLocation(String derniereDateLocation) {
        this.derniereDateLocation = derniereDateLocation;
    }

    // --- Les Méthodes Métier ---

    /**
     * Vérifie si la cassette est actuellement sur une étagère ou chez un client.
     * @return true si elle est disponible, false si elle est en location.
     */
    public boolean estDisponible() {
        LocationDAO locationDAO = new LocationDAO();
        // On récupère toutes les fois où cette cassette a été louée
        List<Location> locationsActives = locationDAO.getByCassette(this.idCassette);
        
        // On regarde si l'une de ces locations est encore "ouverte" (pas de date de retour)
        for (Location loc : locationsActives) {
            if (loc.estActive()) {
                return false; // Trouvé une location en cours !
            }
        }
        return true; // Aucune location active trouvée
    }

    /**
     * Récupère l'objet Catégorie complet lié à cette cassette.
     */
    public Categorie getCategorie() {
        CategorieDAO categorieDAO = new CategorieDAO();
        return categorieDAO.getById(this.idCategorie);
    }

    /**
     * Comment afficher la cassette dans une liste.
     */
    @Override
    public String toString() {
        return titre + " (" + duree + " min)";
    }
}
