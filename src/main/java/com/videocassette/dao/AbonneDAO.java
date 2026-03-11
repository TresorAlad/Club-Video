package com.videocassette.dao;

import com.videocassette.model.Abonne;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe AbonneDAO est comme un "majordome" pour les Abonnés.
 * Son seul travail est de faire le lien entre les objets Java (les Abonnés)
 * et les lignes de texte dans la base de données (SQL).
 * 
 * DAO signifie "Data Access Object" (Objet d'Accès aux Données).
 */
public class AbonneDAO {

    /**
     * Récupère la connexion active vers la base de données.
     */
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Cette méthode est un "traducteur".
     * Elle prend une ligne de résultat SQL (ResultSet) et la transforme en un bel objet Abonne.
     */
    private Abonne mapRow(ResultSet rs) throws SQLException {
        Abonne ab = new Abonne();
        ab.setIdAbonne(rs.getInt("id_abonne"));
        ab.setCodeAbonne(rs.getString("code_abonne"));
        ab.setNomAbonne(rs.getString("nom_abonne"));
        ab.setAdresseAbonne(rs.getString("adresse_abonne"));

        // On transforme les textes de la base en vraies dates Java
        String dateAb = rs.getString("date_abonnement");
        if (dateAb != null && !dateAb.isEmpty())
            ab.setDateAbonement(LocalDate.parse(dateAb));

        String dateEnt = rs.getString("date_entree");
        if (dateEnt != null && !dateEnt.isEmpty())
            ab.setDateEntree(LocalDate.parse(dateEnt));

        ab.setIdUtilisateur(rs.getInt("id_utilisateur"));

        // On récupère aussi les infos calculées par SQL (comme le nombre de films loués)
        try {
            ab.setNombreLocations(rs.getInt("nb_locations"));
        } catch (SQLException e) {}
        
        try {
            ab.setDerniereDateLocation(rs.getString("derniere_location"));
        } catch (SQLException e) {}

        return ab;
    }

    /**
     * Enregistre un nouvel abonné dans la base de données.
     * C'est la commande SQL "INSERT INTO".
     */
    public boolean create(Abonne abonne) {
        String sql = "INSERT INTO abonne (code_abonne, nom_abonne, adresse_abonne, date_abonnement, date_entree, id_utilisateur) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // On remplace les "?" par les vraies valeurs
            ps.setString(1, abonne.getCodeAbonne());
            ps.setString(2, abonne.getNomAbonne());
            ps.setString(3, abonne.getAdresseAbonne());
            ps.setString(4, abonne.getDateAbonement() != null ? abonne.getDateAbonement().toString()
                    : LocalDate.now().toString());
            ps.setString(5,
                    abonne.getDateEntree() != null ? abonne.getDateEntree().toString() : LocalDate.now().toString());
            ps.setInt(6, abonne.getIdUtilisateur());

            int rows = ps.executeUpdate(); // On lance la commande
            if (rows > 0) {
                // On récupère le numéro (ID) que la base a donné automatiquement
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    abonne.setIdAbonne(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Récupère la liste de TOUS les abonnés.
     * C'est la commande SQL "SELECT * FROM".
     */
    public List<Abonne> getAll() {
        List<Abonne> list = new ArrayList<>();
        // On demande aussi de compter les locations et de trouver la date de la dernière
        String sql = "SELECT a.*, " +
                     "(SELECT COUNT(*) FROM location_cassette lc WHERE lc.id_abonne = a.id_abonne) as nb_locations, " +
                     "(SELECT MAX(date_allocation) FROM location_cassette lc WHERE lc.id_abonne = a.id_abonne) as derniere_location " +
                     "FROM abonne a ORDER BY a.nom_abonne";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs)); // Pour chaque ligne, on crée un objet Abonne
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Trouve un abonné en connaissant son numéro unique (ID).
     */
    public Abonne getById(int id) {
        String sql = "SELECT a.*, " +
                     "(SELECT COUNT(*) FROM location_cassette lc WHERE lc.id_abonne = a.id_abonne) as nb_locations, " +
                     "(SELECT MAX(date_allocation) FROM location_cassette lc WHERE lc.id_abonne = a.id_abonne) as derniere_location " +
                     "FROM abonne a WHERE a.id_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Met à jour les informations d'un abonné qui existe déjà.
     * C'est la commande SQL "UPDATE".
     */
    public boolean update(Abonne abonne) {
        String sql = "UPDATE abonne SET code_abonne = ?, nom_abonne = ?, adresse_abonne = ?, date_abonnement = ?, date_entree = ? WHERE id_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, abonne.getCodeAbonne());
            ps.setString(2, abonne.getNomAbonne());
            ps.setString(3, abonne.getAdresseAbonne());
            ps.setString(4, abonne.getDateAbonement() != null ? abonne.getDateAbonement().toString() : null);
            ps.setString(5, abonne.getDateEntree() != null ? abonne.getDateEntree().toString() : null);
            ps.setInt(6, abonne.getIdAbonne());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Supprime un abonné définitivement.
     * C'est la commande SQL "DELETE".
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM abonne WHERE id_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Compte combien il y a d'abonnés au total dans le club.
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM abonne";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
