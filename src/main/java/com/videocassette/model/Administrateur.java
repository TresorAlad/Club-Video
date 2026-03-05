package com.videocassette.model;

import com.videocassette.dao.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe Administrateur - Gère les opérations administratives.
 * Correspond à la classe 'Administrateur' du diagramme de classes.
 */
public class Administrateur {

    private String a; // nom de l'administrateur

    private final AbonneDAO abonneDAO = new AbonneDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final LocationDAO locationDAO = new LocationDAO();

    public Administrateur() {
    }

    public Administrateur(String a) {
        this.a = a;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    // ======================== Méthodes du diagramme ========================

    /** Ajouter un agent (abonné) */
    public boolean ajoutAgent(Abonne abonne) {
        return abonneDAO.create(abonne);
    }

    /** Supprimer un agent (abonné) */
    public boolean supprimerAgent(int idAbonne) {
        return abonneDAO.delete(idAbonne);
    }

    /** Supprimer une catégorie */
    public boolean supprimerCategorie(int idCategorie) {
        return categorieDAO.delete(idCategorie);
    }

    /** Ajouter une catégorie */
    public boolean ajouterCategorie(Categorie categorie) {
        return categorieDAO.create(categorie);
    }

    /** Ajouter une cassette */
    public boolean ajouteCassette(Cassette cassette) {
        return cassetteDAO.create(cassette);
    }

    /** Supprimer une cassette */
    public boolean supprimerCassette(int idCassette) {
        return cassetteDAO.delete(idCassette);
    }

    /** Modifier une cassette */
    public boolean modifierCassette(Cassette cassette) {
        return cassetteDAO.update(cassette);
    }

    /** Afficher les statistiques */
    public Map<String, Integer> afficherStatistique() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalCassettes", cassetteDAO.getAll().size());
        stats.put("totalAbonnes", abonneDAO.getAll().size());
        stats.put("totalCategories", categorieDAO.getAll().size());

        List<Location> allLocations = locationDAO.getAll();
        int actives = 0;
        for (Location loc : allLocations) {
            if (loc.estActive())
                actives++;
        }
        stats.put("locationsActives", actives);
        stats.put("totalLocations", allLocations.size());
        return stats;
    }
}
