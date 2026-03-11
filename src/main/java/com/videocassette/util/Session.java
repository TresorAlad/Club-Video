package com.videocassette.util;

import com.videocassette.model.Abonne;
import com.videocassette.model.Utilisateur;

/**
 * La classe Session est comme un petit carnet de notes qui se souvient
 * de qui est connecté au logiciel en ce moment.
 */
public class Session {

    // L'employé (Utilisateur) actuellement connecté
    private static Utilisateur currentUser;
    
    // L'abonné sur lequel on travaille éventuellement
    private static Abonne currentAbonne;

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    /**
     * Enregistre l'utilisateur qui vient de se connecter.
     */
    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public static Abonne getCurrentAbonne() {
        return currentAbonne;
    }

    /**
     * Garde en mémoire l'abonné sélectionné.
     */
    public static void setCurrentAbonne(Abonne abonne) {
        currentAbonne = abonne;
    }

    /**
     * Vérifie si c'est un administrateur (ici tout le monde l'est).
     */
    public static boolean isAdmin() {
        return true; 
    }

    /**
     * Vide la session (quand on se déconnecte).
     */
    public static void clear() {
        currentUser = null;
        currentAbonne = null;
    }
}
