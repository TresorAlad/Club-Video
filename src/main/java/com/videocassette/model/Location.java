package com.videocassette.model;

import java.time.LocalDate;

/**
 * Classe Location - Représente une location de cassette.
 */
public class Location {

    private int idLocation;
    private int idCassette;
    private int idAbonne;
    private LocalDate dateAllocation;
    private LocalDate dateRetour; // Changé de String à LocalDate

    // Champs d'affichage pour les tables
    private String cassetteTitre;
    private String abonneNom;

    public Location() {
    }

    public Location(int idLocation, int idCassette, int idAbonne, LocalDate dateAllocation, LocalDate dateRetour) {
        this.idLocation = idLocation;
        this.idCassette = idCassette;
        this.idAbonne = idAbonne;
        this.dateAllocation = dateAllocation;
        this.dateRetour = dateRetour;
    }

    // Getters et Setters
    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public int getIdCassette() {
        return idCassette;
    }

    public void setIdCassette(int idCassette) {
        this.idCassette = idCassette;
    }

    public int getIdAbonne() {
        return idAbonne;
    }

    public void setIdAbonne(int idAbonne) {
        this.idAbonne = idAbonne;
    }

    public LocalDate getDateAllocation() {
        return dateAllocation;
    }

    public void setDateAllocation(LocalDate dateAllocation) {
        this.dateAllocation = dateAllocation;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public String getCassetteTitre() {
        return cassetteTitre;
    }

    public void setCassetteTitre(String cassetteTitre) {
        this.cassetteTitre = cassetteTitre;
    }

    public String getAbonneNom() {
        return abonneNom;
    }

    public void setAbonneNom(String abonneNom) {
        this.abonneNom = abonneNom;
    }

    // Méthodes du diagramme
    public void creerLocation() {
        this.dateAllocation = LocalDate.now();
        this.dateRetour = null;
    }

    public void cloturerLocation() {
        this.dateRetour = LocalDate.now();
    }

    public boolean estActive() {
        return dateRetour == null;
    }

    @Override
    public String toString() {
        return "Location #" + idLocation + " (Cassette ID: " + idCassette + ")";
    }
}
