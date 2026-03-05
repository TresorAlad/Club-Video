package com.videocassette.model;

import com.videocassette.dao.LocationDAO;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe Abonné - Représente un abonné du club vidéo.
 */
public class Abonne {

    private int idAbonne;
    private String nomAbonne;
    private String adresseAbonne;
    private LocalDate dateAbonement;
    private LocalDate dateEntree;
    private int idUtilisateur;

    public Abonne() {
    }

    public Abonne(String nomAbonne, String adresseAbonne, LocalDate dateAbonement, LocalDate dateEntree) {
        this.nomAbonne = nomAbonne;
        this.adresseAbonne = adresseAbonne;
        this.dateAbonement = dateAbonement;
        this.dateEntree = dateEntree;
    }

    // Getters et Setters
    public int getIdAbonne() {
        return idAbonne;
    }

    public void setIdAbonne(int idAbonne) {
        this.idAbonne = idAbonne;
    }

    public String getNomAbonne() {
        return nomAbonne;
    }

    public void setNomAbonne(String nomAbonne) {
        this.nomAbonne = nomAbonne;
    }

    public String getAdresseAbonne() {
        return adresseAbonne;
    }

    public void setAdresseAbonne(String adresseAbonne) {
        this.adresseAbonne = adresseAbonne;
    }

    public LocalDate getDateAbonement() {
        return dateAbonement;
    }

    public void setDateAbonement(LocalDate dateAbonement) {
        this.dateAbonement = dateAbonement;
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    // Méthodes du diagramme de classes

    /**
     * Louer une cassette. Maximum 3 par abonné.
     */
    public boolean louerCassette(int idCassette) {
        if (!peutLouer())
            return false;

        LocationDAO dao = new LocationDAO();
        Location loc = new Location();
        loc.setIdCassette(idCassette);
        loc.setIdAbonne(this.idAbonne);
        loc.creerLocation(); // Initialise dateAllocation à aujourd'hui

        return dao.create(loc);
    }

    /**
     * Retourner une cassette.
     */
    public void retournerCassette(int idLocation) {
        LocationDAO dao = new LocationDAO();
        dao.cloturerLocation(idLocation);
    }

    /**
     * Liste des locations actives (non retournées).
     */
    public List<Location> getLocationsEnCours() {
        LocationDAO dao = new LocationDAO();
        return dao.getActiveByAbonne(this.idAbonne);
    }

    /**
     * Vérifie si l'abonné peut louer (max 3 locations actives).
     */
    public boolean peutLouer() {
        return getLocationsEnCours().size() < 3;
    }

    @Override
    public String toString() {
        return nomAbonne;
    }
}
