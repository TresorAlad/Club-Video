package com.videocassette.dao;

import com.videocassette.model.Cassette;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CassetteDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Cassette mapRow(ResultSet rs) throws SQLException {
        Cassette c = new Cassette();
        c.setIdCassette(rs.getInt("id_cassette"));
        c.setTitre(rs.getString("titre"));
        c.setDuree(rs.getString("duree"));
        c.setIdCategorie(rs.getInt("id_categorie"));
        c.setPrix(rs.getString("prix"));
        String dateStr = rs.getString("date_achat");
        if (dateStr != null && !dateStr.isEmpty()) {
            c.setDateAchat(LocalDate.parse(dateStr));
        }
        // Charger le nom de la catégorie si joint
        try {
            c.setCategorieNom(rs.getString("libelle_categorie"));
        } catch (SQLException ignored) {
        }
        return c;
    }

    public boolean create(Cassette cassette) {
        String sql = "INSERT INTO cassette (titre, duree, id_categorie, prix, date_achat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cassette.getTitre());
            ps.setString(2, cassette.getDuree());
            ps.setInt(3, cassette.getIdCategorie());
            ps.setString(4, cassette.getPrix());
            ps.setString(5, cassette.getDateAchat() != null ? cassette.getDateAchat().toString() : null);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    cassette.setIdCassette(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Cassette> getAll() {
        List<Cassette> list = new ArrayList<>();
        String sql = "SELECT c.*, cat.libelle_categorie FROM cassette c LEFT JOIN categorie cat ON c.id_categorie = cat.id_categorie ORDER BY c.titre";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Cassette getById(int id) {
        String sql = "SELECT c.*, cat.libelle_categorie FROM cassette c LEFT JOIN categorie cat ON c.id_categorie = cat.id_categorie WHERE c.id_cassette = ?";
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

    public List<Cassette> getByCategorie(int idCategorie) {
        List<Cassette> list = new ArrayList<>();
        String sql = "SELECT c.*, cat.libelle_categorie FROM cassette c LEFT JOIN categorie cat ON c.id_categorie = cat.id_categorie WHERE c.id_categorie = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCategorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean update(Cassette cassette) {
        String sql = "UPDATE cassette SET titre = ?, duree = ?, id_categorie = ?, prix = ?, date_achat = ? WHERE id_cassette = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, cassette.getTitre());
            ps.setString(2, cassette.getDuree());
            ps.setInt(3, cassette.getIdCategorie());
            ps.setString(4, cassette.getPrix());
            ps.setString(5, cassette.getDateAchat() != null ? cassette.getDateAchat().toString() : null);
            ps.setInt(6, cassette.getIdCassette());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM cassette WHERE id_cassette = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM cassette";
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
