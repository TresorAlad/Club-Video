package com.videocassette.dao;

import com.videocassette.model.Cassette;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 La classe CassetteDAO gère le catalogue des films (cassettes).
 */
public class CassetteDAO {

    /*
     Accès à la base de données.
     */
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /*
     Traduit une ligne SQL en un objet Cassette.
     */
    private Cassette mapRow(ResultSet rs) throws SQLException {
        Cassette c = new Cassette();
        c.setIdCassette(rs.getInt("id_cassette"));
        c.setTitre(rs.getString("titre"));
        c.setDuree(rs.getInt("duree"));
        c.setIdCategorie(rs.getInt("id_categorie"));
        c.setPrix(rs.getDouble("prix"));
        
        String dateStr = rs.getString("date_achat");
        if (dateStr != null && !dateStr.isEmpty()) {
            c.setDateAchat(LocalDate.parse(dateStr));
        }

        // On récupère le nom de la catégorie (ex: Action, Comédie) si on a fait la jointure
        try {
            c.setCategorieNom(rs.getString("libelle_categorie"));
        } catch (SQLException ignored) {}
        
        // On récupère la toute dernière fois où ce film a été loué
        try {
            c.setDerniereDateLocation(rs.getString("derniere_location"));
        } catch (SQLException ignored) {}
        
        return c;
    }

    /*
     Ajoute un nouveau film au catalogue.
     */
    public boolean create(Cassette cassette) {
        String sql = "INSERT INTO cassette (titre, duree, id_categorie, prix, date_achat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cassette.getTitre());
            
            // Si on ne connaît pas la durée, on dit à la base de données que c'est vide (NULL)
            if (cassette.getDuree() != null)
                ps.setInt(2, cassette.getDuree());
            else
                ps.setNull(2, java.sql.Types.INTEGER);

            ps.setInt(3, cassette.getIdCategorie());

            if (cassette.getPrix() != null)
                ps.setDouble(4, cassette.getPrix());
            else
                ps.setNull(4, java.sql.Types.REAL);

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

    /*
     Liste toutes les cassettes du club.
     */
    public List<Cassette> getAll() {
        List<Cassette> list = new ArrayList<>();
        // On demande le titre du film, le nom de sa catégorie, et la date de sa dernière location
        String sql = "SELECT c.*, cat.libelle_categorie, " +
                     "(SELECT MAX(date_allocation) FROM location_cassette WHERE id_cassette = c.id_cassette) as derniere_location " +
                     "FROM cassette c LEFT JOIN categorie cat ON c.id_categorie = cat.id_categorie ORDER BY c.titre";
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

    /*
     Trouve un film précis.
     */
    public Cassette getById(int id) {
        String sql = "SELECT c.*, cat.libelle_categorie, " +
                     "(SELECT MAX(date_allocation) FROM location_cassette WHERE id_cassette = c.id_cassette) as derniere_location " +
                     "FROM cassette c LEFT JOIN categorie cat ON c.id_categorie = cat.id_categorie WHERE c.id_cassette = ?";
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

    /*
     Trouve tous les films d'une catégorie (ex: tous les films d'Horreur).
     */
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

    /*
     Modifie les infos d'un film.
     */
    public boolean update(Cassette cassette) {
        String sql = "UPDATE cassette SET titre = ?, duree = ?, id_categorie = ?, prix = ?, date_achat = ? WHERE id_cassette = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, cassette.getTitre());
            if (cassette.getDuree() != null)
                ps.setInt(2, cassette.getDuree());
            else
                ps.setNull(2, java.sql.Types.INTEGER);
            ps.setInt(3, cassette.getIdCategorie());
            if (cassette.getPrix() != null)
                ps.setDouble(4, cassette.getPrix());
            else
                ps.setNull(4, java.sql.Types.REAL);
            ps.setString(5, cassette.getDateAchat() != null ? cassette.getDateAchat().toString() : null);
            ps.setInt(6, cassette.getIdCassette());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     Supprime un film de la base.
     */
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

    /*
     Compte combien de cassettes on possède en tout.
     */
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

    /*
     Donne la liste des films qui ne sont pas loués en ce moment.
     */
    public List<Cassette> getAllDisponibles() {
        return getAll().stream().filter(Cassette::estDisponible).toList();
    }
}
