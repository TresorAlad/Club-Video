package com.videocassette.dao;

import com.videocassette.model.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(Categorie categorie) {
        String sql = "INSERT INTO categorie (libelle_categorie) VALUES (?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categorie.getLibelleCategorie());
            int rows = ps.executeUpdate();
            if (rows > 0) {
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
