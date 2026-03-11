
package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.*;
import com.videocassette.model.*;
import com.videocassette.util.DataTransfer;
import com.videocassette.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

/*
  Le DashboardController est le "Cerveau" de l'application une fois connecté.
  Il gère tout : l'accueil, la liste des films, les abonnés, les catégories,
  les locations et les stats.
  C'est un gros fichier car il centralise l'affichage de tous les tableaux
  (TableView).
 */
public class DashboardController {

    // Boutons du menu latéral
    @FXML
    private Button btnAccueil, btnCassettes, btnAbonnes, btnCategories, btnLocations, btnStats, btnProfil;

    // Zone centrale où le contenu change
    @FXML
    private StackPane contentArea;

    // Les différents panneaux (pages) qu'on peut afficher
    @FXML
    private VBox accueilPane, cassettesPane, abonnesPane, categoriesPane, locationsPane, statistiquesPane, profilPane;

    // Labels pour les petites stats rapides en haut
    @FXML
    private Label statCassettes, statAbonnes, statLocationsActives, statCategories;

    // Page Accueil : Le catalogue avec des "cartes" (FlowPane)
    @FXML
    private FlowPane cassettesFlowPane; // Zone où les films s'affichent sous forme de jolies cartes
    @FXML
    private HBox categoryFilterBox; // Barre de boutons pour filtrer par catégorie
    @FXML
    private TextField searchCassette; // Barre de recherche pour les films (accueil)

    // Page Cassettes : Un tableau classique (CRUD)
    @FXML
    private TableView<Cassette> cassetteTable;
    @FXML
    private TableColumn<Cassette, Integer> cassIdCol;
    @FXML
    private TableColumn<Cassette, String> cassTitreCol, cassDureeCol, cassCatCol, cassPrixCol, cassDateCol,
            cassDispoCol, cassLastLocCol;
    @FXML
    private TextField searchCassetteTable;

    // Page Abonnés : Liste des clients
    @FXML
    private TableView<Abonne> abonneTable;
    @FXML
    private TableColumn<Abonne, String> abCodeCol, abNomCol;
    @FXML
    private TableColumn<Abonne, Integer> abNbLocCol;
    @FXML
    private TableColumn<Abonne, Abonne> abActionsCol; // Colonne spéciale pour les boutons (ex: détails)

    private final java.util.Random random = new java.util.Random();
    @FXML
    private TextField searchAbonne;

    // Page Catégories : Types de films
    @FXML
    private TableView<Categorie> categorieTable;
    @FXML
    private TableColumn<Categorie, Integer> catIdCol;
    @FXML
    private TableColumn<Categorie, String> catLibelleCol;

    // Page Locations : Historique des prêts
    @FXML
    private TableView<Location> locationTable;
    @FXML
    private TableColumn<Location, Integer> locIdCol;
    @FXML
    private TableColumn<Location, String> locCassetteCol, locAbonneCol, locDateAllocCol, locDateRetourPrevueCol,
            locDateRetCol, locStatutCol;

    // Elements divers (Stats détaillées, Profil)
    @FXML
    private Label statTotalCassettes2, statTotalAbonnes2, statLocActives2, statsResume;
    @FXML
    private Label profilEmail, profilRole, profilMessage;
    @FXML
    private PasswordField newPasswordField, confirmPasswordField;

    // Accès aux données (DAOs)
    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final AbonneDAO abonneDAO = new AbonneDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private final LocationDAO locationDAO = new LocationDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final CarteAbonneDAO carteAbonneDAO = new CarteAbonneDAO();

    // Listes d'objets pour remplir les tableaux
    private ObservableList<Cassette> cassettesData = FXCollections.observableArrayList();
    private ObservableList<Cassette> cassettesTableData = FXCollections.observableArrayList();
    private ObservableList<Abonne> abonnesData = FXCollections.observableArrayList();
    private ObservableList<Categorie> categoriesData = FXCollections.observableArrayList();
    private ObservableList<Location> locationsData = FXCollections.observableArrayList();

    /*
     * Cette méthode se lance dès que la page est chargée.
     */
    @FXML
    public void initialize() {
        setupTables(); // 1. On prépare les colonnes des tableaux
        setupSearch(); // 2. On prépare les barres de recherche
        showAccueil(); // 3. On affiche la page d'accueil par défaut
    }

    /*
     * Configure comment les données sont affichées dans chaque colonne des
     * tableaux.
     */
    private void setupTables() {
        // Colonnes du tableau des Cassettes
        cassIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdCassette()).asObject());
        cassTitreCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitre()));
        cassDureeCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDuree() + " min"));
        cassCatCol.setCellValueFactory(d -> {
            // Pour afficher le nom de la catégorie (ex: Action) au lieu de son ID technique
            // (ex: 1)
            Categorie cat = categorieDAO.getById(d.getValue().getIdCategorie());
            return new javafx.beans.property.SimpleStringProperty(cat != null ? cat.getLibelleCategorie() : "—");
        });
        cassPrixCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getPrix())));
        cassDateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateAchat() != null ? d.getValue().getDateAchat().toString() : ""));
        cassDispoCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().estDisponible() ? " Oui" : " Non"));
        cassLastLocCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDerniereDateLocation() != null ? d.getValue().getDerniereDateLocation() : "—"));
        cassetteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Colonnes du tableau des Abonnés
        abCodeCol
                .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCodeAbonne()));
        abNomCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNomAbonne()));
        abNbLocCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getNombreLocations()).asObject());

        // Colonne spéciale pour le bouton "Détails" sur chaque ligne d'abonné
        abActionsCol.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        abActionsCol.setCellFactory(param -> new TableCell<Abonne, Abonne>() {
            private final Button btn = new Button(" Détails");
            {
                btn.getStyleClass().add("action-button-secondary");
                btn.setOnAction(event -> {
                    Abonne a = getItem();
                    if (a != null) {
                        showDetailsAbonne(a);
                    }
                });
            }

            @Override
            protected void updateItem(Abonne item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        abonneTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Colonnes du tableau des Catégories
        catIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdCategorie()).asObject());
        catLibelleCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getLibelleCategorie()));
        categorieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Colonnes du tableau des Locations
        locIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdLocation()).asObject());
        locCassetteCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCassetteTitre()));
        locAbonneCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAbonneNom()));
        locDateAllocCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateAllocation() != null ? d.getValue().getDateAllocation().toString() : ""));
        locDateRetourPrevueCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateRetourPrevue() != null ? d.getValue().getDateRetourPrevue().toString() : "—"));
        locDateRetCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateRetour() != null ? d.getValue().getDateRetour().toString() : "—"));
        locStatutCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().estActive() ? " Active" : " Terminée"));
        locationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /*
     * Configure le fonctionnement des barres de recherche.
     * Dès qu'on tape une lettre, le tableau se filtre tout seul.
     */
    private void setupSearch() {
        // Recherche dans le catalogue Accueil (les cartes de films)
        searchCassette.textProperty().addListener((obs, old, val) -> {
            applyCassettesFilter();
        });

        // Recherche dans le tableau des cassettes (page Cassettes)
        searchCassetteTable.textProperty().addListener((obs, old, val) -> {
            cassetteTable.setItems(new FilteredList<>(cassettesTableData,
                    c -> val == null || val.isEmpty() || c.getTitre().toLowerCase().contains(val.toLowerCase())));
        });

        // Recherche dans le tableau des abonnés
        searchAbonne.textProperty().addListener((obs, old, val) -> {
            abonneTable.setItems(new FilteredList<>(abonnesData,
                    a -> val == null || val.isEmpty()
                            || a.getNomAbonne().toLowerCase().contains(val.toLowerCase())
                            || (a.getCodeAbonne() != null
                                    && a.getCodeAbonne().toLowerCase().contains(val.toLowerCase()))));
        });
    }

    /*
     * Méthode générique pour afficher un panneau et cacher les autres.
     * Elle gère aussi la couleur du bouton actif dans le menu.
     */
    private void showPane(VBox pane) {
        VBox[] panes = { accueilPane, cassettesPane, abonnesPane, categoriesPane, locationsPane, statistiquesPane,
                profilPane };
        Button[] buttons = { btnAccueil, btnCassettes, btnAbonnes, btnCategories, btnLocations, btnStats, btnProfil };
        for (int i = 0; i < panes.length; i++) {
            boolean active = (panes[i] == pane);
            panes[i].setVisible(active);
            panes[i].setManaged(active);
            buttons[i].getStyleClass().remove("nav-button-active");
            if (active)
                buttons[i].getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    public void showAccueil() {
        showPane(accueilPane);
        refreshStats(); // Met à jour les petits compteurs en haut
        refreshAccueilCatalogue(); // Recharge les films
    }

    @FXML
    public void showCassettes() {
        showPane(cassettesPane);
        refreshCassettesTable();
    }

    @FXML
    public void showAbonnes() {
        showPane(abonnesPane);
        refreshAbonnes();
    }

    @FXML
    public void showCategories() {
        showPane(categoriesPane);
        refreshCategories();
    }

    @FXML
    public void showLocations() {
        showPane(locationsPane);
        refreshLocations();
    }

    @FXML
    public void showStatistiques() {
        showPane(statistiquesPane);
        refreshStatistiques();
    }

    @FXML
    public void showProfil() {
        showPane(profilPane);
        Utilisateur u = Session.getCurrentUser();
        if (u != null) {
            profilEmail.setText(u.getEmail());
            profilRole.setText("Nom : " + u.getNomComplet());
        }
    }

    /*
     * Quitter la session et revenir à la page de connexion.
     */
    @FXML
    public void deconnecter() {
        Session.clear();
        try {
            App.changerVue("/com/videocassette/views/login-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthodes de rafraîchissement des données (BDD -> UI)
    /* Met à jour les 4 badges de stats rapides en haut de l'écran. */
    private void refreshStats() {
        statCassettes.setText(String.valueOf(cassetteDAO.count()));
        statAbonnes.setText(String.valueOf(abonneDAO.count()));
        statLocationsActives.setText(String.valueOf(locationDAO.countActives()));
        statCategories.setText(String.valueOf(categorieDAO.getAll().size()));
    }

    /* Recharge les films pour le catalogue de l'accueil. */
    private void refreshAccueilCatalogue() {
        cassettesData.setAll(cassetteDAO.getAll());
        setupCategoryFilters();
        applyCassettesFilter();
    }

    /* Recharge les films pour le tableau de gestion. */
    private void refreshCassettesTable() {
        cassettesTableData.setAll(cassetteDAO.getAll());
        cassetteTable.setItems(cassettesTableData);
    }

    private ToggleGroup categoryToggleGroup = new ToggleGroup();

    /*
     * Crée dynamiquement les boutons de catégories (Action, Comédie, etc.) pour
     * filtrer.
     */
    private void setupCategoryFilters() {
        categoryFilterBox.getChildren().clear();
        categoryToggleGroup = new ToggleGroup();

        // Ajout du bouton "Toutes" par défaut
        ToggleButton allBtn = new ToggleButton("Toutes");
        allBtn.getStyleClass().add("category-toggle-all");
        allBtn.setToggleGroup(categoryToggleGroup);
        allBtn.setSelected(true);
        allBtn.setUserData(null);
        allBtn.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        categoryFilterBox.getChildren().add(allBtn);

        // On crée un bouton pour chaque catégorie existante dans la base
        for (Categorie cat : categorieDAO.getAll()) {
            ToggleButton btn = new ToggleButton(cat.getLibelleCategorie());
            btn.getStyleClass().add("category-toggle");
            btn.setToggleGroup(categoryToggleGroup);
            btn.setUserData(cat.getIdCategorie());
            btn.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            categoryFilterBox.getChildren().add(btn);
        }

        // Si on clique sur un bouton de catégorie, on filtre la liste
        categoryToggleGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val == null) {
                allBtn.setSelected(true);
            } else {
                applyCassettesFilter();
            }
        });
    }

    /* Applique le filtre (texte + catégorie) sur les cartes de l'accueil. */
    private void applyCassettesFilter() {
        cassettesFlowPane.getChildren().clear();
        String searchText = searchCassette.getText() != null ? searchCassette.getText().toLowerCase() : "";

        Integer selectedCatId = null;
        if (categoryToggleGroup.getSelectedToggle() != null) {
            selectedCatId = (Integer) categoryToggleGroup.getSelectedToggle().getUserData();
        }

        for (Cassette c : cassettesData) {
            boolean matchesSearch = searchText.isEmpty() || c.getTitre().toLowerCase().contains(searchText);
            boolean matchesCat = selectedCatId == null || c.getIdCategorie() == selectedCatId;
            if (matchesSearch && matchesCat) {
                // Si le film correspond aux critères, on crée sacarte et on l'ajoute à l'écran
                cassettesFlowPane.getChildren().add(createAccueilCard(c));
            }
        }
    }

    /*
     * Crée une jolie carte visuelle pour un film (avec icône, titre, prix et bouton
     * Louer).
     */
    private VBox createAccueilCard(Cassette c) {
        VBox card = new VBox();
        card.getStyleClass().add("cassette-card");

        // Zone de l'image (icône 🎬)
        StackPane imageBox = new StackPane();
        imageBox.getStyleClass().add("cassette-image-box");

        Label icon = new Label("🎬");
        icon.setStyle("-fx-font-size: 48px;");

        // Petit badge pour dire si c'est dispo
        Label badge = new Label(c.estDisponible() ? "Disponible" : "Indisponible");
        badge.getStyleClass().add(c.estDisponible() ? "badge-dispo" : "badge-indispo");
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);

        imageBox.getChildren().addAll(icon, badge);

        // Infos du film
        Label lblTitre = new Label(c.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        lblTitre.getStyleClass().add("cassette-info");

        Label lblDuree = new Label(c.getDuree() + " min");
        lblDuree.getStyleClass().add("cassette-info");

        Categorie cat = categorieDAO.getById(c.getIdCategorie());
        Label lblCat = new Label(cat != null ? cat.getLibelleCategorie() : "—");
        lblCat.getStyleClass().add("cassette-info");

        Label lblPrix = new Label(c.getPrix() + " CFA");
        lblPrix.getStyleClass().add("cassette-info");

        // Bouton Louer
        Button btnLouer = new Button("Louer");
        btnLouer.getStyleClass().add("cassette-btn-loue");
        btnLouer.setMaxWidth(Double.MAX_VALUE);
        btnLouer.setDisable(!c.estDisponible()); // On ne peut pas louer si c'est déjà pris
        btnLouer.setOnAction(e -> ouvrirDialogLouer(c));

        card.getChildren().addAll(imageBox, lblTitre, lblDuree, lblCat, lblPrix, btnLouer);
        return card;
    }

    /*
     * Action déclenchée quand on veut louer un film depuis la carte.
     */
    private void ouvrirDialogLouer(Cassette c) {
        DataTransfer.put("rentCassette", c); // On "transfère" le film à la page de location
        try {
            App.changerVue("/com/videocassette/views/rent-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CRUD Cassettes (page Cassettes - TableView)
    //
    // CRUD Cassettes (Gestion via le tableau)
    // /*Ouvre le formulaire pour ajouter un nouveau film. */
    @FXML
    private void ajouterCassette() {
        try {
            App.changerVue("/com/videocassette/views/cassette-form-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Ouvre le formulaire pour modifier le film sélectionné dans le tableau. */
    @FXML
    private void modifierCassetteSelecTable() {
        Cassette sel = cassetteTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner une cassette à modifier.");
            return;
        }
        DataTransfer.put("editCassette", sel); // On passe l'objet à la page suivante
        try {
            App.changerVue("/com/videocassette/views/cassette-form-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Demande confirmation avant de supprimer le film sélectionné. */
    @FXML
    private void supprimerCassetteSelecTable() {
        Cassette sel = cassetteTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner une cassette à supprimer.");
            return;
        }
        DataTransfer.put("deleteTarget", sel);
        DataTransfer.put("deleteType", "CASSETTE");
        try {
            App.changerVue("/com/videocassette/views/confirm-delete-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CRUD Abonnés
    /* Recharge la liste des abonnés depuis la base. */
    private void refreshAbonnes() {
        abonnesData.setAll(abonneDAO.getAll());
        abonneTable.setItems(abonnesData);
    }

    /* Ouvre le formulaire d'ajout d'abonné. */
    @FXML
    private void ajouterAbonne() {
        try {
            App.changerVue("/com/videocassette/views/abonne-form-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Ouvre le formulaire pour modifier l'abonné choisi. */
    @FXML
    private void modifierAbonne() {
        Abonne sel = abonneTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner un abonné à modifier.");
            return;
        }
        DataTransfer.put("editAbonne", sel);
        try {
            App.changerVue("/com/videocassette/views/abonne-form-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Demande confirmation pour supprimer un abonné. */
    @FXML
    private void supprimerAbonne() {
        Abonne sel = abonneTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner un abonné à supprimer.");
            return;
        }
        DataTransfer.put("deleteTarget", sel);
        DataTransfer.put("deleteType", "ABONNE");
        try {
            App.changerVue("/com/videocassette/views/confirm-delete-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Affiche une boîte de dialogue avec tous les détails de l'abonné.
     */
    private void showDetailsAbonne(Abonne a) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setTitle("Détails de l'abonné");
        dialog.setHeaderText("Fiche de l'abonné : " + a.getNomAbonne());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Code :"), 0, 0);
        grid.add(new Label(a.getCodeAbonne()), 1, 0);

        grid.add(new Label("Nom :"), 0, 1);
        grid.add(new Label(a.getNomAbonne()), 1, 1);

        grid.add(new Label("Adresse :"), 0, 2);
        grid.add(new Label(a.getAdresseAbonne()), 1, 2);

        grid.add(new Label("Date Abonnement :"), 0, 3);
        grid.add(new Label(a.getDateAbonement() != null ? a.getDateAbonement().toString() : "—"), 1, 3);

        grid.add(new Label("Date Entrée :"), 0, 4);
        grid.add(new Label(a.getDateEntree() != null ? a.getDateEntree().toString() : "—"), 1, 4);

        grid.add(new Label("Nombre Locations :"), 0, 5);
        grid.add(new Label(String.valueOf(a.getNombreLocations())), 1, 5);

        grid.add(new Label("Dernière location :"), 0, 6);
        grid.add(new Label(a.getDerniereDateLocation() != null ? a.getDerniereDateLocation() : "—"), 1, 6);

        Button btnImprimer = new Button("️ Imprimer");
        btnImprimer.getStyleClass().add("action-button-secondary");
        btnImprimer.setOnAction(e -> {
            imprimerCarteAbonne(a);
        });

        content.getChildren().addAll(grid, new Separator(), btnImprimer);
        dialog.getDialogPane().setContent(content);
        dialog.getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /*
     * Gère la génération et l'affichage du PDF de la carte d'abonné.
     */
    private void imprimerCarteAbonne(Abonne a) {
        new Thread(() -> {
            String path = com.videocassette.util.PDFUtils.generateMemberCard(a);
            javafx.application.Platform.runLater(() -> {
                if (path != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Impression réussie");
                    alert.setHeaderText("Carte générée");
                    alert.setContentText("Le PDF a été créé dans :\n" + path);
                    alert.showAndWait();
                } else {
                    alerte("Erreur lors de la génération du PDF.");
                }
            });
        }).start();
    }

    // CRUD Catégories
    private void refreshCategories() {
        categoriesData.setAll(categorieDAO.getAll());
        categorieTable.setItems(categoriesData);
    }

    @FXML
    private void ajouterCategorie() {
        try {
            App.changerVue("/com/videocassette/views/category-form-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierCategorie() {
        Categorie sel = categorieTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner une catégorie à modifier.");
            return;
        }
        DataTransfer.put("editCategory", sel);
        try {
            App.changerVue("/com/videocassette/views/category-form-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerCategorie() {
        Categorie sel = categorieTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner une catégorie à supprimer.");
            return;
        }
        DataTransfer.put("deleteTarget", sel);
        DataTransfer.put("deleteType", "CATEGORIE");
        try {
            App.changerVue("/com/videocassette/views/confirm-delete-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Locations (Historique et fin de prêt)
    private void refreshLocations() {
        locationsData.setAll(locationDAO.getAll());
        locationTable.setItems(locationsData);
    }

    /* Créer une nouvelle location (souvent via le bouton "Louer" de l'accueil). */
    @FXML
    private void nouvelleLocation() {
        try {
            App.changerVue("/com/videocassette/views/location-form-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Quand un client ramène le film, on clôture sa location. */
    @FXML
    private void cloturerLocation() {
        Location sel = locationTable.getSelectionModel().getSelectedItem();
        // On vérifie que la location est encore "Active" (pas déjà terminée)
        if (sel != null && sel.estActive() && confirmer("Clôturer cette location ?")) {
            locationDAO.cloturerLocation(sel.getIdLocation());
            refreshLocations(); // On recharge le tableau pour voir le changement
        }
    }

    // Statistiques Détaillées
    private void refreshStatistiques() {
        int tc = cassetteDAO.count();
        int ta = abonneDAO.count();
        int la = locationDAO.countActives();
        statTotalCassettes2.setText(String.valueOf(tc));
        statTotalAbonnes2.setText(String.valueOf(ta));
        statLocActives2.setText(String.valueOf(la));
        statsResume.setText("Résumé : " + tc + " cassettes, " + ta + " abonnés, " + la + " locations en cours.");
    }

    // Page Profil (Changement de mot de passe)
    @FXML
    private void changerMotDePasse() {
        String p1 = newPasswordField.getText();
        // On vérifie que les deux mots de passe tapés sont identiques
        if (p1 != null && p1.equals(confirmPasswordField.getText()) && !p1.isEmpty()) {
            utilisateurDAO.updatePassword(Session.getCurrentUser().getIdUtilisateur(), p1);
            profilMessage.setText("Mot de passe modifié avec succès.");
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            profilMessage.setText("Erreur : les mots de passe ne correspondent pas.");
        }
    }

    // Utilitaires (Alertes et Confirmations)
    /* Affiche un simple message d'information. */
    private void alerte(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    /* Demande à l'utilisateur de cliquer sur OUI ou NON. */
    private boolean confirmer(String m) {
        return new Alert(Alert.AlertType.CONFIRMATION, m, ButtonType.YES, ButtonType.NO).showAndWait()
                .get() == ButtonType.YES;
    }
}
