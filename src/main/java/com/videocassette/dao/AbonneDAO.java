package com.videocassette.dao;

import com.videocassette.model.Abonne;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbonneDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Abonne mapRow(ResultSet rs) throws SQLException {
        Abonne ab = new Abonne();
        ab.setIdAbonne(rs.getInt("id_abonne"));
        ab.setCodeAbonne(rs.getString("code_abonne"));
        ab.setNomAbonne(rs.getString("nom_abonne"));
        ab.setAdresseAbonne(rs.getString("adresse_abonne"));

        String dateAb = rs.getString("date_abonnement");
        if (dateAb != null && !dateAb.isEmpty())
            ab.setDateAbonement(LocalDate.parse(dateAb));

        String dateEnt = rs.getString("date_entree");
        if (dateEnt != null && !dateEnt.isEmpty())
            ab.setDateEntree(LocalDate.parse(dateEnt));

        ab.setIdUtilisateur(rs.getInt("id_utilisateur"));

        // Récupérer le nombre de locations si présent dans la requête
        try {
            ab.setNombreLocations(rs.getInt("nb_locations"));
        } catch (SQLException e) {
            // nb_locations n'est pas dans le ResultSet, on ignore
        }

        return ab;
    }

    public boolean create(Abonne abonne) {
        String sql = "INSERT INTO abonne (code_abonne, nom_abonne, adresse_abonne, date_abonnement, date_entree, id_utilisateur) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, abonne.getCodeAbonne());
            ps.setString(2, abonne.getNomAbonne());
            ps.setString(3, abonne.getAdresseAbonne());
            ps.setString(4, abonne.getDateAbonement() != null ? abonne.getDateAbonement().toString()
                    : LocalDate.now().toString());
            ps.setString(5,
                    abonne.getDateEntree() != null ? abonne.getDateEntree().toString() : LocalDate.now().toString());
            ps.setInt(6, abonne.getIdUtilisateur());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    abonne.setIdAbonne(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Abonne> getAll() {
        List<Abonne> list = new ArrayList<>();
        String sql = "SELECT a.*, (SELECT COUNT(*) FROM location_cassette lc WHERE lc.id_abonne = a.id_abonne) as nb_locations "
                + "FROM abonne a ORDER BY a.nom_abonne";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Abonne getById(int id) {
        String sql = "SELECT * FROM abonne WHERE id_abonne = ?";
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

    public Abonne getByUtilisateurId(int utilId) {
        String sql = "SELECT * FROM abonne WHERE id_utilisateur = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, utilId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Abonne abonne) {
        String sql = "UPDATE abonne SET code_abonne = ?, nom_abonne = ?, adresse_abonne = ?, date_abonnement = ?, date_entree = ? WHERE id_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, abonne.getCodeAbonne());
            ps.setString(2, abonne.getNomAbonne());
            ps.setString(3, abonne.getAdresseAbonne());
            ps.setString(4, abonne.getDateAbonement() != null ? abonne.getDateAbonement().toString() : null);
            ps.setString(5, abonne.getDateEntree() != null ? abonne.getDateEntree().toString() : null);
            ps.setInt(6, abonne.getIdAbonne());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Abonne getByCode(String code) {
        String sql = "SELECT * FROM abonne WHERE code_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM abonne WHERE id_abonne = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM abonne";
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
