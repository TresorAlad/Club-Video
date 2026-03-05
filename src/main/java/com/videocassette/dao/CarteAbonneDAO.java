package com.videocassette.dao;

import com.videocassette.model.CarteAbonne;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarteAbonneDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(CarteAbonne carte) {
        String sql = "INSERT INTO carte_abonne (id_abonne) VALUES (?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, carte.getIdAbonne());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    carte.setIdCarteAbonne(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CarteAbonne getByAbonne(int idAbonne) {
        String sql = "SELECT ca.*, a.nom_abonne FROM carte_abonne ca LEFT JOIN abonne a ON ca.id_abonne = a.id_abonne WHERE ca.id_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idAbonne);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CarteAbonne carte = new CarteAbonne(rs.getInt("id_carte_abonne"), rs.getInt("id_abonne"));
                try {
                    carte.setNomAbonne(rs.getString("nom_abonne"));
                } catch (SQLException ignored) {
                }
                return carte;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CarteAbonne> getAll() {
        List<CarteAbonne> list = new ArrayList<>();
        String sql = "SELECT ca.*, a.nom_abonne FROM carte_abonne ca LEFT JOIN abonne a ON ca.id_abonne = a.id_abonne";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CarteAbonne carte = new CarteAbonne(rs.getInt("id_carte_abonne"), rs.getInt("id_abonne"));
                try {
                    carte.setNomAbonne(rs.getString("nom_abonne"));
                } catch (SQLException ignored) {
                }
                list.add(carte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM carte_abonne WHERE id_carte_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
