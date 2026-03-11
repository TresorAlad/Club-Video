package com.videocassette.model;

/*
 La classe CarteAbonne représente physiquement la carte de membre que l'on donne à l'abonné.
 C'est une extension de l'abonné dans le système.
 */
public class CarteAbonne {

    // Les "Champs"
    // Le numéro unique de la carte dans la base de données
    private int idCarteAbonne;
    
    // Le numéro de l'abonné à qui appartient cette carte
    private int idAbonne;
    
    // Le nom de l'abonné (pour l'afficher facilement sur la carte)
    private String nomAbonne;

    // Les Constructeurs
    public CarteAbonne() {
    }

    public CarteAbonne(int idCarteAbonne, int idAbonne) {
        this.idCarteAbonne = idCarteAbonne;
        this.idAbonne = idAbonne;
    }

    // Les Getters et Setters
    // (Voir les explications dans Abonne.java sur l'encapsulation)

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

    // Les Méthodes Métier
    /*
     Cette méthode crée le "Code Barres" ou le numéro visuel de la carte.
     Exemple : VC-0005-000012
     VC : Video Club
     0005 : ID de l'abonné
     000012 : ID de la carte
     */
    public String genererCarte() {
        // String.format permet de rajouter des '0' devant les chiffres pour faire joli
        return "VC-" + String.format("%04d", idAbonne) + "-" + String.format("%06d", idCarteAbonne);
    }

    /*
     Par défaut, si on veut afficher la carte, on affiche son numéro généré.
     */
    @Override
    public String toString() {
        return genererCarte();
    }
}
