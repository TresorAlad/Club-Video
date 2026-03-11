package com.videocassette.model;

/*
 La classe Utilisateur représente une personne (souvent un employé) 
 qui a le droit d'utiliser ce logiciel.
 Elle permet de gérer la connexion (email et mot de passe).
 */
public class Utilisateur {

    // Les "Champs"
    // Identifiant unique du compte utilisateur
    private int idUtilisateur;
    
    // Prénom et Nom de l'employé
    private String nomComplet;
    
    // Adresse email (sert d'identifiant de connexion)
    private String email;
    
    // Mot de passe secret
    private String motDePasse;

    // Les Constructeurs
    public Utilisateur() {
    }

    public Utilisateur(int idUtilisateur, String nomComplet, String email, String motDePasse) {
        this.idUtilisateur = idUtilisateur;
        this.nomComplet = nomComplet;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public Utilisateur(String nomComplet, String email, String motDePasse) {
        this.nomComplet = nomComplet;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Les Getters et Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    /*
     Comment afficher l'utilisateur dans le système.
     */
    @Override
    public String toString() {
        return nomComplet + " (" + email + ")";
    }
}
