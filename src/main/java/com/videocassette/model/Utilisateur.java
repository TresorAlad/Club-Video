package com.videocassette.model;

/**
 * Classe Utilisateur - Représente un utilisateur du système.
 * Correspond à la classe 'Utilisateur' du diagramme de classes.
 */
public class Utilisateur {

    private int idUtilisateur;
    private String nomComplet;
    private String email;
    private String motDePasse;

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

    // ======================== Getters et Setters ========================

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

    @Override
    public String toString() {
        return nomComplet + " (" + email + ")";
    }
}
