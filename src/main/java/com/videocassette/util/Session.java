package com.videocassette.util;

import com.videocassette.model.Abonne;
import com.videocassette.model.Utilisateur;

/**
 * Classe utilitaire pour gérer la session utilisateur courante.
 */
public class Session {

    private static Utilisateur currentUser;
    private static Abonne currentAbonne;

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public static Abonne getCurrentAbonne() {
        return currentAbonne;
    }

    public static void setCurrentAbonne(Abonne abonne) {
        currentAbonne = abonne;
    }

    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    public static void clear() {
        currentUser = null;
        currentAbonne = null;
    }
}
