# 🎬 Gestion de Location de Cassettes Vidéo (Video Club)

Une application de bureau moderne développée en **JavaFX** pour la gestion d'un vidéo-club. Cette application permet de gérer les adhérents, le catalogue de cassettes, les catégories et le cycle de vie complet des locations (emprunt et retour), tout en respectant une architecture MVC robuste.

---

## 📸 Aperçu des Fonctionnalités

### 👤 Tableau de Bord Unique
- **Inscription & Connexion** : Création de compte autonome avec génération automatique de carte d'abonné.
- **Catalogue & Catégories** : Gérer (ajouter, modifier, supprimer) et consulter les cassettes et catégories disponibles.
- **Locations & Abonnés** : Enregistrer de nouvelles locations, gérer les retours, et suivre la liste des abonnés.
- **Statistiques** : Vue globale avec statistiques en temps réel sur le parc de cassettes et les locations.
- **Profil** : Modification du mot de passe et visualisation des informations personnelles.

---

## 🛠️ Technologies Utilisées

- **Langage** : Java 21
- **Interface Graphique** : JavaFX (FXML + CSS personnalisé)
- **Base de Données** : SQLite (via JDBC)
- **Gestionnaire de Projet** : Maven
- **Design** : Styles CSS inspirés de Figma (Palette : #415A77, #778DA9, #E0E1DD)

---

## 📂 Structure du Projet

L'architecture suit les principes de séparation des préoccupations :

```text
src/main/
├── java/com/videocassette/
│   ├── controller/   # Logique des vues (Contrôleurs FXML)
│   ├── dao/          # Accès aux données (Data Access Objects)
│   ├── model/        # Classes métiers (POJO)
│   ├── util/         # Utilitaires (Session, etc.)
│   └── App.java      # Point d'entrée de l'application
├── resources/com/videocassette/
│   ├── sql/          # Script d'initialisation (schema.sql)
│   ├── styles/       # Fichiers CSS
│   └── views/        # Fichiers FXML (Interface)
└── module-info.java  # Configuration du module Java
```

---

## 🚀 Guide d'Installation et Lancement

### 1. Pré-requis
- **JDK 21** ou supérieur installé.
- **Maven 3.8+** installé.
- Un terminal (Bash, PowerShell, etc.).

### 2. Clonage / Récupération
Placez-vous dans le répertoire du projet :
```bash
cd "Documents/Projet Java"
```

### 3. Compilation
Utilisez Maven pour compiler les sources et télécharger les dépendances :
```bash
mvn clean compile
```

### 4. Lancement de l'application
Exécutez la commande suivante pour démarrer l'interface graphique :
```bash
mvn javafx:run
```

---

## 🔐 Création de Compte

L'application ne fait plus de distinction entre les utilisateurs ("admin" ou "abonné"). Chaque compte créé donne un accès complet à la plateforme.
Utilisez la page **Inscription** pour créer votre propre compte. Tous les membres ont accès à toutes les fonctionnalités du club vidéo !

---

## 🗃️ Base de Données (SQLite)

Le fichier de base de données `videocassette.db` est créé automatiquement à la racine du projet lors du premier lancement.  
Pour réinitialiser complètement la base :
1. Fermez l'application.
2. Supprimez le fichier `videocassette.db`.
3. Relancez l'application. Elle se reconstruira à partir du fichier `src/main/resources/com/videocassette/sql/schema.sql`.

---

## 👨‍💻 Développé par
*Équipe MindSet Projet Java* 🚀
