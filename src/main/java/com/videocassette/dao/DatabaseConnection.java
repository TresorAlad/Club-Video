package com.videocassette.dao;

import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * La classe DatabaseConnection est le "cerveau" de la communication.
 * Elle s'occupe de brancher le logiciel à la base de données SQLite (le fichier videocassette.db).
 * 
 * Elle utilise le motif "Singleton" : cela signifie qu'il n'y a qu'une seule 
 * prise (connexion) partagée par tout le programme pour éviter de s'emmêler les pinceaux.
 */
public class DatabaseConnection {

    // L'unique exemplaire de cette classe
    private static DatabaseConnection instance;
    
    // L'objet qui représente la connexion réelle
    private Connection connection;
    
    // L'adresse du fichier de la base de données sur l'ordinateur
    private static final String DB_URL = "jdbc:sqlite:videocassette.db";

    /**
     * Le constructeur est "private" pour empêcher de créer plusieurs connexions.
     * On doit obligatoirement passer par getInstance().
     */
    private DatabaseConnection() {
        try {
            // On tente d'ouvrir la porte vers la base de données
            connection = DriverManager.getConnection(DB_URL);
            
            // On active une sécurité (clés étrangères) pour éviter de supprimer 
            // une catégorie si des films l'utilisent encore.
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        } catch (SQLException e) {
            // Si la porte refuse de s'ouvrir, on affiche l'erreur
            e.printStackTrace();
        }
    }

    /**
     * C'est ici qu'on demande la connexion.
     * Si elle n'existe pas encore, on la crée. Sinon, on donne celle qui existe déjà.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Donne l'objet "Connection" qui permet de lancer des ordres SQL.
     */
    public Connection getConnection() {
        try {
            // Si la porte a été fermée par erreur, on la rouvre
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Cette méthode est magique : elle lit le fichier "schema.sql" et crée 
     * toutes les tables (Abonnés, Cassettes, etc.) si elles n'existent pas encore.
     * C'est comme construire les étagères avant de ranger les livres.
     */
    public void initialiserBase() {
        InputStream is = null;
        File schemaFile = new File("schema.sql");

        try {
            // 1. On cherche le plan de construction (schema.sql)
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

            // 2. On lit le fichier ligne par ligne
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder currentStatement = new StringBuilder();
            String line;
            int count = 0;

            // 3. On prépare un "lanceur de commandes" SQL
            try (Statement stmt = getConnection().createStatement()) {
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    // On saute les lignes vides ou les commentaires
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                        continue;
                    }

                    // On prépare la commande complète (qui peut faire plusieurs lignes)
                    String lineWithoutComments = line;
                    int commentIndex = line.indexOf("--");
                    if (commentIndex >= 0) {
                        lineWithoutComments = line.substring(0, commentIndex);
                    }
                    String checkLine = lineWithoutComments.trim();

                    currentStatement.append(line);

                    // Si on voit un ";" c'est que la commande est finie, on l'exécute !
                    if (checkLine.endsWith(";")) {
                        String sql = currentStatement.toString().trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                            count++;
                        }
                        currentStatement.setLength(0); // On vide pour la commande suivante
                    } else {
                        currentStatement.append("\n");
                    }
                }
                System.out.println("Base de données initialisée (" + count + " instructions SQL exécutées).");

                // 4. Petite vérification finale : est-ce que la table des utilisateurs est bien là ?
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
