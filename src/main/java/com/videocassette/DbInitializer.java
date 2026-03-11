package com.videocassette;

import com.videocassette.dao.DatabaseConnection;

/*
 Utilitaire pour initialiser manuellement la base de données.
 Utile pour créer les tables sans lancer toute l'interface graphique.
 */
public class DbInitializer {
    public static void main(String[] args) {
        System.out.println("--- Initialisation de la base de données ---");
        DatabaseConnection.getInstance().initialiserBase();
        System.out.println("--- Terminé ---");
    }
}
