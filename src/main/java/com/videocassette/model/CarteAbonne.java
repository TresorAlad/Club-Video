package com.videocassette.model;

/**
 * Classe CarteAbonné - Représente la carte d'un abonné.
 * Correspond à la classe 'carte d'abonné' du diagramme de classes.
 */
public class CarteAbonne {

    private int idCarteAbonne;
    private int idAbonne;
    private String nomAbonne;

    public CarteAbonne() {
    }

    public CarteAbonne(int idCarteAbonne, int idAbonne) {
        this.idCarteAbonne = idCarteAbonne;
        this.idAbonne = idAbonne;
    }

    // ======================== Getters et Setters ========================

    public int getIdCarteAbonne() {
        return idCarteAbonne;
    }

    public void setIdCarteAbonne(int idCarteAbonne) {
        this.idCarteAbonne = idCarteAbonne;
    }

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

    // ======================== Méthodes du diagramme ========================

    /**
     * Générer le numéro de carte de l'abonné.
     */
    public String genererCarte() {
        return "VC-" + String.format("%04d", idAbonne) + "-" + String.format("%06d", idCarteAbonne);
    }

    @Override
    public String toString() {
        return genererCarte();
    }
}
