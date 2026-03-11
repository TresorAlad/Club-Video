package com.videocassette.dao;

import com.videocassette.model.Location;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe LocationDAO gère les contrats de location.
 * C'est ici qu'on enregistre qui a pris quel film et quand il le rend.
 */
public class LocationDAO {

    /**
     * Accès à la base de données.
     */
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Transforme une ligne de la base de données en objet Location.
     */
    private Location mapRow(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.setIdLocation(rs.getInt("id_location"));
        l.setIdCassette(rs.getInt("id_cassette"));
        l.setIdAbonne(rs.getInt("id_abonne"));

        // On traduit les dates (qui sont du texte dans la base)
        String dateAll = rs.getString("date_allocation");
        if (dateAll != null && !dateAll.isEmpty()) {
            l.setDateAllocation(LocalDate.parse(dateAll));
        }

        String dateRet = rs.getString("date_retour");
        if (dateRet != null && !dateRet.isEmpty()) {
            l.setDateRetour(LocalDate.parse(dateRet));
        }

        // Si on a fait une jointure (JOIN), on récupère aussi le titre du film et le
        // nom de l'abonné
        try {
            l.setCassetteTitre(rs.getString("titre"));
            l.setAbonneNom(rs.getString("nom_abonne"));
        } catch (SQLException ignored) {
            // Pas grave si ces infos ne sont pas là
        }

        return l;
    }

    /**
     * Crée un nouveau contrat de location (quand quelqu'un part avec un film).
     */
    public boolean create(Location location) {
        String sql = "INSERT INTO location_cassette (id_cassette, id_abonne, date_allocation, date_retour) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, location.getIdCassette());
            ps.setInt(2, location.getIdAbonne());
            // Si aucune date n'est précisée, on prend la date d'aujourd'hui
            ps.setString(3, location.getDateAllocation() != null ? location.getDateAllocation().toString()
                    : LocalDate.now().toString());
            ps.setString(4, location.getDateRetour() != null ? location.getDateRetour().toString() : null);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    location.setIdLocation(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Liste toutes les locations (historique complet).
     * Les plus récentes apparaissent en premier (ORDER BY ... DESC).
     */
    public List<Location> getAll() {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT l.*, c.titre, a.nom_abonne FROM location_cassette l " +
                "LEFT JOIN cassette c ON l.id_cassette = c.id_cassette " +
                "LEFT JOIN abonne a ON l.id_abonne = a.id_abonne " +
                "ORDER BY l.date_allocation DESC";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Trouve les locations d'un abonné qui n'a pas encore rendu ses films.
     */
    public List<Location> getActiveByAbonne(int idAbonne) {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT l.*, c.titre FROM location_cassette l " +
                "LEFT JOIN cassette c ON l.id_cassette = c.id_cassette " +
                "WHERE l.id_abonne = ? AND (l.date_retour IS NULL OR l.date_retour = '')";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idAbonne);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Historique des locations pour un abonné précis.
     */
    public List<Location> getByAbonne(int idAbonne) {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT l.*, c.titre FROM location_cassette l " +
                "LEFT JOIN cassette c ON l.id_cassette = c.id_cassette " +
                "WHERE l.id_abonne = ? ORDER BY l.date_allocation DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idAbonne);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère toutes les locations (actives ou non) pour une cassette donnée.
     * Utilisé pour vérifier si la cassette est disponible.
     */
    public List<Location> getByCassette(int idCassette) {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT l.*, a.nom_abonne FROM location_cassette l " +
                "LEFT JOIN abonne a ON l.id_abonne = a.id_abonne " +
                "WHERE l.id_cassette = ? ORDER BY l.date_allocation DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCassette);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Termine une location (quand l'abonné ramène le film).
     * On met la date d'aujourd'hui comme date de retour.
     */
    public boolean cloturerLocation(int idLocation) {
        String sql = "UPDATE location_cassette SET date_retour = ? WHERE id_location = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, LocalDate.now().toString());
            ps.setInt(2, idLocation);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Compte combien de films sont actuellement "dehors" (en cours de location).
     */
    public int countActives() {
        String sql = "SELECT COUNT(*) FROM location_cassette WHERE date_retour IS NULL OR date_retour = ''";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Supprime une ligne de location par erreur.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM location_cassette WHERE id_location = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
