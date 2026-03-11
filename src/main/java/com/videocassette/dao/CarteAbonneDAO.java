package com.videocassette.dao;

import com.videocassette.model.CarteAbonne;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 La classe CarteAbonneDAO gère la création et la lecture des cartes de membres.
 */
public class CarteAbonneDAO {

    /*
     Accès à la base de données.
     */
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /*
     Crée une nouvelle carte pour un abonné.
     */
    public boolean create(CarteAbonne carte) {
        String sql = "INSERT INTO carte_abonne (id_abonne) VALUES (?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, carte.getIdAbonne());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                // On récupère le numéro de carte généré
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

    /*
     Trouve la carte d'un abonné.
     On utilise "LEFT JOIN" pour récupérer aussi le nom de l'abonné en une seule fois.
     */
    public CarteAbonne getByAbonne(int idAbonne) {
        String sql = "SELECT ca.*, a.nom_abonne FROM carte_abonne ca LEFT JOIN abonne a ON ca.id_abonne = a.id_abonne WHERE ca.id_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idAbonne);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CarteAbonne carte = new CarteAbonne(rs.getInt("id_carte_abonne"), rs.getInt("id_abonne"));
                try {
                    // On remplit le nom de l'abonné dans l'objet carte
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

    /*
     Liste toutes les cartes existantes.
     */
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

    /*
     Supprime une carte (par exemple si l'abonné quitte le club).
     */
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
