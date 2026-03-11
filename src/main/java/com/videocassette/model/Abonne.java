package com.videocassette.model;

import com.videocassette.dao.LocationDAO;
import java.time.LocalDate;
import java.util.List;

/*
 La classe Abonne est ce qu'on appelle un "Modèle" ou un "POJO" (Plain Old
 Java Object).
 C'est comme une fiche d'information qui représente un client (un abonné) dans
 notre système.
 Elle contient toutes les caractéristiques d'un abonné (nom, adresse, etc.) et
 quelques actions qu'il peut faire.
 */
public class Abonne {

    // Les "Champs" ou "Attributs"
    // Ce sont les informations que l'on stocke pour chaque abonné.

    // Identifiant unique dans la base de données (numéro automatique)
    private int idAbonne;

    // Code de l'abonné (ex: CLUB001) pour l'identifier facilement
    private String codeAbonne;

    // Nom complet de l'abonné
    private String nomAbonne;

    // Adresse physique de l'abonné
    private String adresseAbonne;

    // Date à laquelle il a payé son abonnement
    private LocalDate dateAbonement;

    // Date de sa première inscription
    private LocalDate dateEntree;

    // Lien avec l'utilisateur qui a créé cet abonné (pour la sécurité)
    private int idUtilisateur;

    // Nombre total de locations effectuées (pour les statistiques)
    private int nombreLocations;

    // Date de la toute dernière location (que nous avons ajoutée récemment)
    private String derniereDateLocation;

    // Les "Constructeurs"
    // Ce sont des méthodes spéciales utilisées pour créer un nouvel objet "Abonne".

    // Constructeur vide (nécessaire pour certains outils informatiques)
    public Abonne() {
    }

    // Constructeur pratique pour créer un abonné avec ses infos de base
    public Abonne(String nomAbonne, String adresseAbonne, LocalDate dateAbonement, LocalDate dateEntree) {
        this.nomAbonne = nomAbonne;
        this.adresseAbonne = adresseAbonne;
        this.dateAbonement = dateAbonement;
        this.dateEntree = dateEntree;
    }

    // Constructeur complet avec le code d'abonné
    public Abonne(String codeAbonne, String nomAbonne, String adresseAbonne, LocalDate dateAbonement,
            LocalDate dateEntree) {
        this.codeAbonne = codeAbonne;
        this.nomAbonne = nomAbonne;
        this.adresseAbonne = adresseAbonne;
        this.dateAbonement = dateAbonement;
        this.dateEntree = dateEntree;
    }

    // Les "Getters" et "Setters"
    // Java utilise le principe d'encapsulation : on ne touche pas directement aux
    // champs (private).
    // On utilise des méthodes "Get" pour lire la valeur et "Set" pour la modifier.

    public int getIdAbonne() {
        return idAbonne;
    }

    public void setIdAbonne(int idAbonne) {
        this.idAbonne = idAbonne;
    }

    public String getCodeAbonne() {
        return codeAbonne;
    }

    public void setCodeAbonne(String codeAbonne) {
        this.codeAbonne = codeAbonne;
    }

    public String getNomAbonne() {
        return nomAbonne;
    }

    public void setNomAbonne(String nomAbonne) {
        this.nomAbonne = nomAbonne;
    }

    public String getAdresseAbonne() {
        return adresseAbonne;
    }

    public void setAdresseAbonne(String adresseAbonne) {
        this.adresseAbonne = adresseAbonne;
    }

    public LocalDate getDateAbonement() {
        return dateAbonement;
    }

    public void setDateAbonement(LocalDate dateAbonement) {
        this.dateAbonement = dateAbonement;
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public int getNombreLocations() {
        return nombreLocations;
    }

    public void setNombreLocations(int nombreLocations) {
        this.nombreLocations = nombreLocations;
    }

    public String getDerniereDateLocation() {
        return derniereDateLocation;
    }

    public void setDerniereDateLocation(String derniereDateLocation) {
        this.derniereDateLocation = derniereDateLocation;
    }

    // Les "Méthodes Métier"
    // Ce sont les actions concrètes que l'abonné peut déclencher.

    /*
     Permet à l'abonné de louer une cassette.
     Règle : un abonné ne peut pas avoir plus de 3 cassettes à la fois.
     
     @param idCassette       L'identifiant de la cassette à louer.
     @param dateRetourPrevue La date prévue de retour (peut être null).
     */
    public boolean louerCassette(int idCassette, LocalDate dateRetourPrevue) {
        // 1. On vérifie s'il a encore le droit de louer
        if (!peutLouer())
            return false;

        // 2. On prépare l'enregistrement de la location
        LocationDAO dao = new LocationDAO();
        Location loc = new Location();
        loc.setIdCassette(idCassette);
        loc.setIdAbonne(this.idAbonne);
        loc.creerLocation(dateRetourPrevue); // Met la date d'aujourd'hui + la date de retour prévue

        // 3. On enregistre en base de données
        return dao.create(loc);
    }

    /*
     Permet à l'abonné de rendre une cassette.
     */
    public void retournerCassette(int idLocation) {
        LocationDAO dao = new LocationDAO();
        dao.cloturerLocation(idLocation); // On met une date de retour pour libérer la place
    }

    /*
     Récupère la liste des locations que l'abonné n'a pas encore rendues.
     */
    public List<Location> getLocationsEnCours() {
        LocationDAO dao = new LocationDAO();
        return dao.getActiveByAbonne(this.idAbonne);
    }

    /*
     Vérifie si l'abonné peut encore louer une cassette.
     Retourne 'true' si oui (moins de 3 en cours), 'false' sinon.
     */
    public boolean peutLouer() {
        return getLocationsEnCours().size() < 3;
    }

    /*
     Cette méthode spéciale permet d'afficher le nom de l'abonné
     quand on utilise l'objet dans une liste ou un menu déroulant.
     */
    @Override
    public String toString() {
        return nomAbonne;
    }
}
