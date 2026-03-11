package com.videocassette.dao;

import com.videocassette.model.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe CategorieDAO gère les tiroirs de notre bibliothèque (les catégories).
 * Elle permet d'ajouter, de voir, de changer ou de supprimer des étiquettes (Horreur, Action, etc.).
 */
public class CategorieDAO {

    /**
     * Accès à la base de données.
     */
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Crée une nouvelle catégorie.
     */
    public boolean create(Categorie categorie) {
        String sql = "INSERT INTO categorie (libelle_categorie) VALUES (?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categorie.getLibelleCategorie());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                // On récupère l'ID donné par la base de données
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    categorie.setIdCategorie(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Trouve une catégorie précise par son numéro.
     */
    public Categorie getById(int id) {
        String sql = "SELECT * FROM categorie WHERE id_categorie = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Categorie(rs.getInt("id_categorie"), rs.getString("libelle_categorie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Donne la liste de toutes les catégories, triées par ordre alphabétique.
     */
    public List<Categorie> getAll() {
        List<Categorie> list = new ArrayList<>();
        String sql = "SELECT * FROM categorie ORDER BY libelle_categorie";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Categorie(rs.getInt("id_categorie"), rs.getString("libelle_categorie")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Modifie le nom d'une catégorie.
     */
    public boolean update(Categorie categorie) {
        String sql = "UPDATE categorie SET libelle_categorie = ? WHERE id_categorie = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, categorie.getLibelleCategorie());
            ps.setInt(2, categorie.getIdCategorie());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Supprime une catégorie.
     * Attention : SQLite empêchera la suppression si des films sont encore dans cette catégorie.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM categorie WHERE id_categorie = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
