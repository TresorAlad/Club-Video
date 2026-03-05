package com.videocassette.dao;

import com.videocassette.model.Location;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Location mapRow(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.setIdLocation(rs.getInt("id_location"));
        l.setIdCassette(rs.getInt("id_cassette"));
        l.setIdAbonne(rs.getInt("id_abonne"));

        String dateAll = rs.getString("date_allocation");
        if (dateAll != null && !dateAll.isEmpty()) {
            l.setDateAllocation(LocalDate.parse(dateAll));
        }

        String dateRet = rs.getString("date_retour");
        if (dateRet != null && !dateRet.isEmpty()) {
            l.setDateRetour(LocalDate.parse(dateRet));
        }

        // Tentative de récupération des colonnes jointes (titre, nom)
        try {
            l.setCassetteTitre(rs.getString("titre"));
            l.setAbonneNom(rs.getString("nom_abonne"));
        } catch (SQLException ignored) {
            // Ces colonnes ne sont pas présentes dans toutes les requêtes
        }

        return l;
    }

    public boolean create(Location location) {
        String sql = "INSERT INTO location_cassette (id_cassette, id_abonne, date_allocation, date_retour) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, location.getIdCassette());
            ps.setInt(2, location.getIdAbonne());
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

    public List<Location> getByCassette(int idCassette) {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT * FROM location_cassette WHERE id_cassette = ?";
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
