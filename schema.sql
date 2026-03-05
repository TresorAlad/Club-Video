-- Structure de la base de données pour Video Club

-- Table des utilisateurs (pour l'authentification)
CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    mot_de_passe TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'abonne'
);

-- Table des catégories
CREATE TABLE IF NOT EXISTS categorie (
    id_categorie INTEGER PRIMARY KEY AUTOINCREMENT,
    libelle_categorie TEXT NOT NULL
);

-- Table des cassettes
CREATE TABLE IF NOT EXISTS cassette (
    id_cassette INTEGER PRIMARY KEY AUTOINCREMENT,
    titre TEXT NOT NULL,
    duree TEXT,
    id_categorie INTEGER,
    prix TEXT,
    date_achat TEXT,
    FOREIGN KEY (id_categorie) REFERENCES categorie(id_categorie) ON DELETE SET NULL
);

-- Table des abonnés
CREATE TABLE IF NOT EXISTS abonne (
    id_abonne INTEGER PRIMARY KEY AUTOINCREMENT,
    nom_abonne TEXT NOT NULL,
    adresse_abonne TEXT,
    date_abonnement TEXT,
    date_entree TEXT,
    id_utilisateur INTEGER,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE
);

-- Table des locations
CREATE TABLE IF NOT EXISTS location_cassette (
    id_location INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cassette INTEGER NOT NULL,
    id_abonne INTEGER NOT NULL,
    date_allocation TEXT NOT NULL,
    date_retour TEXT,
    FOREIGN KEY (id_cassette) REFERENCES cassette(id_cassette) ON DELETE CASCADE,
    FOREIGN KEY (id_abonne) REFERENCES abonne(id_abonne) ON DELETE CASCADE
);

-- Table des cartes d'abonné
CREATE TABLE IF NOT EXISTS carte_abonne (
    id_carte_abonne INTEGER PRIMARY KEY AUTOINCREMENT,
    id_abonne INTEGER NOT NULL UNIQUE,
    FOREIGN KEY (id_abonne) REFERENCES abonne(id_abonne) ON DELETE CASCADE
);

-- Données initiales
INSERT OR IGNORE INTO utilisateur (id_utilisateur, email, mot_de_passe, role) VALUES (1, 'admin@videoclub.com', 'admin123', 'admin');
INSERT OR IGNORE INTO utilisateur (id_utilisateur, email, mot_de_passe, role) VALUES (2, 'abonne@videoclub.com', 'abonne123', 'abonne');

INSERT OR IGNORE INTO abonne (id_abonne, nom_abonne, adresse_abonne, date_abonnement, date_entree, id_utilisateur) 
VALUES (1, 'Abonné Test', '123 Rue de la Liberté, Paris', '2024-01-01', '2024-01-01', 2);

INSERT OR IGNORE INTO categorie (id_categorie, libelle_categorie) VALUES (1, 'Action');
INSERT OR IGNORE INTO categorie (id_categorie, libelle_categorie) VALUES (2, 'Comédie');
INSERT OR IGNORE INTO categorie (id_categorie, libelle_categorie) VALUES (3, 'Drame');
INSERT OR IGNORE INTO categorie (id_categorie, libelle_categorie) VALUES (4, 'Horreur');
INSERT OR IGNORE INTO categorie (id_categorie, libelle_categorie) VALUES (5, 'Science-Fiction');

INSERT OR IGNORE INTO cassette (id_cassette, titre, duree, id_categorie, prix, date_achat) VALUES (1, 'Inception', '148 min', 5, '15€', '2023-10-10');
INSERT OR IGNORE INTO cassette (id_cassette, titre, duree, id_categorie, prix, date_achat) VALUES (2, 'The Dark Knight', '152 min', 1, '12€', '2023-05-12');
INSERT OR IGNORE INTO cassette (id_cassette, titre, duree, id_categorie, prix, date_achat) VALUES (3, 'Superbad', '113 min', 2, '10€', '2023-08-20');
