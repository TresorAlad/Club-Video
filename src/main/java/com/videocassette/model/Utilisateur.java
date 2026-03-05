package com.videocassette.model;

/**
 * Classe Utilisateur - Représente un utilisateur du système.
 * Correspond à la classe 'Utilisateur' du diagramme de classes.
 */
public class Utilisateur {

    private int idUtilisateur;
    private String email;
    private String motDePasse;
    private String role; // "admin" ou "abonne"

    public Utilisateur() {
    }

    public Utilisateur(int idUtilisateur, String email, String motDePasse, String role) {
        this.idUtilisateur = idUtilisateur;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public Utilisateur(String email, String motDePasse, String role) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    // ======================== Getters et Setters ========================

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return email + " (" + role + ")";
    }
}
