package com.videocassette.dao;

import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Singleton de connexion à la base de données SQLite.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:videocassette.db";

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            // Activer les clés étrangères
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Initialiser toutes les tables de la base de données à partir du fichier SQL
     * externe à la racine du projet.
     */
    public void initialiserBase() {
        // Chercher schema.sql à la racine du projet ou dans les ressources
        InputStream is = null;
        File schemaFile = new File("schema.sql");

        try {
            if (schemaFile.exists()) {
                is = new FileInputStream(schemaFile);
                System.out.println("Chargement de schema.sql depuis le fichier local.");
            } else {
                is = getClass().getResourceAsStream("/schema.sql");
                if (is == null) {
                    System.err.println("Erreur : schema.sql introuvable.");
                    return;
                }
                System.out.println("Chargement de schema.sql depuis les ressources.");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder script = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                // Ignorer les commentaires et les lignes vides
                if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("--")) {
                    script.append(line).append(" "); // Espace pour éviter les collisions à la jointure
                }
            }
            is.close();

            // Découpage intelligent par point-virgule (basique mais suffisant ici)
            String[] statements = script.toString().split(";");
            try (Statement stmt = getConnection().createStatement()) {
                int count = 0;
                for (String sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql.trim());
                        count++;
                    }
                }
                System.out.println("Base de données initialisée (" + count + " instructions exécutées).");
            }
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
