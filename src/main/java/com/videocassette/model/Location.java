package com.videocassette.model;

import java.time.LocalDate;

/*
 La classe Location représente le contrat entre le club et un abonné.
 Elle enregistre quelle cassette est partie chez quel client, et quand.
 */
public class Location {

    // Les "Champs"
    // Numéro de la transaction de location
    private int idLocation;

    // Le numéro de la cassette qui est louée
    private int idCassette;

    // Le numéro de l'abonné qui loue la cassette
    private int idAbonne;

    // La date où la cassette a été emportée
    private LocalDate dateAllocation;

    // La date prévue pour le retour de la cassette
    private LocalDate dateRetourPrevue;

    // La date où la cassette a été effectivement rendue (null si elle est encore
    // chez le client)
    private LocalDate dateRetour;

    // Champs d'affichage pratique
    // (Utilisés pour afficher le titre et le nom au lieu de simples numéros dans
    // les tableaux)
    private String cassetteTitre;
    private String abonneNom;

    // Les Constructeurs
    public Location() {
    }

    public Location(int idLocation, int idCassette, int idAbonne, LocalDate dateAllocation, LocalDate dateRetourPrevue,
            LocalDate dateRetour) {
        this.idLocation = idLocation;
        this.idCassette = idCassette;
        this.idAbonne = idAbonne;
        this.dateAllocation = dateAllocation;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetour = dateRetour;
    }

    // Les Getters et Setters
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

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
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

    // Les Méthodes Métier
    /*
     Prépare une nouvelle location : met la date d'aujourd'hui.
     
     @param dateRetourPrevue La date à laquelle l'abonné prévoit de rendre la
                             cassette (peut être null).
     */
    public void creerLocation(LocalDate dateRetourPrevue) {
        this.dateAllocation = LocalDate.now();
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetour = null; // Pas encore rendue
    }

    /*
     Clôture une location : enregistre la date de retour (aujourd'hui).
     */
    public void cloturerLocation() {
        this.dateRetour = LocalDate.now();
    }

    /*
     Indique si la cassette est encore chez le client.
     
     @return true si elle n'est pas revenue, false si elle est rendue.
     */
    public boolean estActive() {
        return dateRetour == null;
    }

    @Override
    public String toString() {
        return "Location #" + idLocation + " (Cassette ID: " + idCassette + ")";
    }
}
