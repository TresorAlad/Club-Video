package com.videocassette.dao;

import com.videocassette.model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (email, mot_de_passe, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, utilisateur.getEmail());
            ps.setString(2, utilisateur.getMotDePasse());
            ps.setString(3, utilisateur.getRole() != null ? utilisateur.getRole() : "abonne");

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

    public Utilisateur login(String email, String password) {
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setIdUtilisateur(rs.getInt("id_utilisateur"));
                u.setEmail(rs.getString("email"));
                u.setMotDePasse(rs.getString("mot_de_passe"));
                u.setRole(rs.getString("role"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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
