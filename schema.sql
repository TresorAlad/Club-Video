-- ==========================================================
-- SCHEMA DE LA BASE DE DONNEES (SQLite)
-- Ce fichier définit la structure de la base de données (les tables)
-- et contient quelques données de départ pour tester l'application.
-- ==========================================================

-- 0. Supprimer les tables existantes (ordre inverse des dépendances pour éviter les erreurs)
DROP TABLE IF EXISTS location_cassette;
DROP TABLE IF EXISTS carte_abonne;
DROP TABLE IF EXISTS abonne;
DROP TABLE IF EXISTS cassette;
DROP TABLE IF EXISTS categorie;
DROP TABLE IF EXISTS utilisateur;


-- 1. Table Utilisateur (Stocke les comptes pour se connecter)
CREATE TABLE  utilisateur (
    id_utilisateur INTEGER PRIMARY KEY AUTOINCREMENT, -- Identifiant unique automatique
    nom_complet TEXT NOT NULL,                         -- Nom et prénom
    email TEXT NOT NULL UNIQUE,                        -- Adresse email unique (clé de connexion)
    mot_de_passe TEXT NOT NULL                         -- Mot de passe (en texte clair ici pour simplifier)
);

-- 2. Table des catégories
CREATE TABLE  categorie (
    id_categorie INTEGER PRIMARY KEY AUTOINCREMENT,
    libelle_categorie TEXT NOT NULL
);

-- 3. Table des cassettes (Les films disponibles à la location)
CREATE TABLE  cassette (
    id_cassette INTEGER PRIMARY KEY AUTOINCREMENT,
    titre TEXT NOT NULL,
    duree INTEGER,        -- Durée en minutes (ex: 120)
    id_categorie INTEGER, -- Lien vers la table catégorie
    prix REAL,            -- Prix de location (ex: 15.5)
    date_achat TEXT,      -- Date au format YYYY-MM-DD
    FOREIGN KEY (id_categorie) REFERENCES categorie(id_categorie) ON DELETE SET NULL
);

-- 4. Table des abonnés (Le profil client lié à un utilisateur)
CREATE TABLE  abonne (
    id_abonne INTEGER PRIMARY KEY AUTOINCREMENT,
    code_abonne TEXT UNIQUE,       -- Code unique type CLUB001
    nom_abonne TEXT NOT NULL,
    adresse_abonne TEXT,
    date_abonnement TEXT,
    date_entree TEXT,
    id_utilisateur INTEGER UNIQUE, -- Un abonné est lié à UN SEUL compte utilisateur
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
);

-- 5. Table des cartes d'abonné
CREATE TABLE  carte_abonne (
    id_carte_abonne INTEGER PRIMARY KEY AUTOINCREMENT,
    id_abonne INTEGER NOT NULL UNIQUE,
    FOREIGN KEY (id_abonne) REFERENCES abonne(id_abonne) ON DELETE CASCADE
);

-- 6. Table des locations (Enregistre qui a loué quoi et quand)
CREATE TABLE  location_cassette (
    id_location INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cassette INTEGER NOT NULL, -- La cassette louée
    id_abonne INTEGER NOT NULL,   -- L'abonné qui loue
    date_allocation TEXT NOT NULL,
    date_retour_prevue TEXT,      -- Date à laquelle l'abonné doit rendre la cassette
    date_retour TEXT,             -- Rempli quand la cassette est effectivement rendue
    FOREIGN KEY (id_cassette) REFERENCES cassette(id_cassette) ON DELETE CASCADE,
    FOREIGN KEY (id_abonne) REFERENCES abonne(id_abonne) ON DELETE CASCADE
);

-- ==========================================
-- Données initiales (Jeux d'essai mis à jour)
-- ==========================================
INSERT INTO utilisateur (id_utilisateur, nom_complet, email, mot_de_passe)
VALUES (1, 'Admin VideoClub', 'admin@videoclub.com', 'admin123');

INSERT INTO utilisateur (id_utilisateur, nom_complet, email, mot_de_passe)
VALUES (2, 'Jean Dupont', 'abonne@videoclub.com', 'abonne123');

-- L'abonné est lié à l'utilisateur #2 (Jean Dupont)
INSERT INTO abonne (id_abonne, code_abonne, nom_abonne, adresse_abonne, date_abonnement, date_entree, id_utilisateur)
VALUES (1, 'CLUB001', 'Jean Dupont', '123 Rue de la Liberté, Paris', '2024-01-01', '2024-01-01', 2);

-- On lui crée sa carte !
INSERT INTO carte_abonne (id_carte_abonne, id_abonne)
VALUES (1, 1);

INSERT INTO categorie (id_categorie, libelle_categorie) VALUES (1, 'Action');
INSERT INTO categorie (id_categorie, libelle_categorie) VALUES (2, 'Comédie');
INSERT INTO categorie (id_categorie, libelle_categorie) VALUES (5, 'Science-Fiction');

-- Remarque : Les prix et la durée sont maintenant des VRAIS nombres sans texte
INSERT INTO cassette (id_cassette, titre, duree, id_categorie, prix, date_achat) 
VALUES (1, 'Inception', 148, 5, 15.00, '2023-10-10');
INSERT INTO cassette (id_cassette, titre, duree, id_categorie, prix, date_achat) 
VALUES (2, 'The Dark Knight', 152, 1, 12.00, '2023-05-12');
