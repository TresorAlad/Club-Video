package com.videocassette.dao;

import com.videocassette.model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe UtilisateurDAO gère les employés (ou admins) qui se connectent au logiciel.
 */
public class UtilisateurDAO {

    /**
     * Accès à la base de données.
     */
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Crée un nouveau compte pour un employé.
     */
    public boolean create(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (nom_complet, email, mot_de_passe) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, utilisateur.getNomComplet());
            ps.setString(2, utilisateur.getEmail());
            ps.setString(3, utilisateur.getMotDePasse());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    utilisateur.setIdUtilisateur(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tente de connecter un utilisateur.
     * On utilise LOWER(?) pour que l'email marche même s'il y a des majuscules.
     */
    public Utilisateur login(String email, String password) {
        String sql = "SELECT * FROM utilisateur WHERE LOWER(email) = LOWER(?) AND mot_de_passe = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si on trouve quelqu'un, on crée son objet
                    Utilisateur u = new Utilisateur();
                    u.setIdUtilisateur(rs.getInt("id_utilisateur"));
                    u.setNomComplet(rs.getString("nom_complet"));
                    u.setEmail(rs.getString("email"));
                    u.setMotDePasse(rs.getString("mot_de_passe"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Permet de changer de mot de passe.
     */
    public boolean updatePassword(int id, String newPassword) {
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id_utilisateur = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Supprime un utilisateur.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM utilisateur WHERE id_utilisateur = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
