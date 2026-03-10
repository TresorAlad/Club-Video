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
            StringBuilder currentStatement = new StringBuilder();
            String line;
            int count = 0;

            try (Statement stmt = getConnection().createStatement()) {
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                        continue;
                    }

                    // On enlève les commentaires en fin de ligne pour la détection du ";"
                    String lineWithoutComments = line;
                    int commentIndex = line.indexOf("--");
                    if (commentIndex >= 0) {
                        lineWithoutComments = line.substring(0, commentIndex);
                    }
                    String checkLine = lineWithoutComments.trim();

                    currentStatement.append(line);

                    if (checkLine.endsWith(";")) {
                        String sql = currentStatement.toString().trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                            count++;
                        }
                        currentStatement.setLength(0);
                    } else {
                        currentStatement.append("\n");
                    }
                }
                System.out.println("Base de données initialisée (" + count + " instructions SQL exécutées).");

                // Vérification robuste de l'existence de la table utilisateur
                try (ResultSet rs = getConnection().getMetaData().getTables(null, null, "utilisateur", null)) {
                    boolean exists = false;
                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");
                        if ("utilisateur".equalsIgnoreCase(tableName)) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        System.out.println("Vérification : Table 'utilisateur' trouvée.");
                    } else {
                        System.err
                                .println("ERREUR CRITIQUE : La table 'utilisateur' est absente après initialisation !");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
