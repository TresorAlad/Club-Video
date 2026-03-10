
package com.videocassette.controller;

import com.videocassette.App;
import com.videocassette.dao.*;
import com.videocassette.model.*;
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

public class DashboardController {

    @FXML
    private Button btnAccueil, btnCassettes, btnAbonnes, btnCategories, btnLocations, btnStats, btnProfil;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox accueilPane, cassettesPane, abonnesPane, categoriesPane, locationsPane, statistiquesPane, profilPane;
    @FXML
    private Label statCassettes, statAbonnes, statLocationsActives, statCategories;

    // ---- Accueil : catalogue (cartes)
    @FXML
    private FlowPane cassettesFlowPane;
    @FXML
    private HBox categoryFilterBox;
    @FXML
    private TextField searchCassette;

    // ---- Cassettes : TableView (CRUD)
    @FXML
    private TableView<Cassette> cassetteTable;
    @FXML
    private TableColumn<Cassette, Integer> cassIdCol;
    @FXML
    private TableColumn<Cassette, String> cassTitreCol, cassDureeCol, cassCatCol, cassPrixCol, cassDateCol,
            cassDispoCol;
    @FXML
    private TextField searchCassetteTable;

    // ---- Abonnés
    @FXML
    private TableView<Abonne> abonneTable;
    @FXML
    private TableColumn<Abonne, String> abCodeCol, abNomCol, abAdresseCol, abDateAbCol, abDateEntCol;
    @FXML
    private TableColumn<Abonne, Integer> abNbLocCol;
    @FXML
    private TableColumn<Abonne, Abonne> abActionsCol;

    private final java.util.Random random = new java.util.Random();
    @FXML
    private TextField searchAbonne;

    // ---- Catégories
    @FXML
    private TableView<Categorie> categorieTable;
    @FXML
    private TableColumn<Categorie, Integer> catIdCol;
    @FXML
    private TableColumn<Categorie, String> catLibelleCol;

    // ---- Locations
    @FXML
    private TableView<Location> locationTable;
    @FXML
    private TableColumn<Location, Integer> locIdCol;
    @FXML
    private TableColumn<Location, String> locCassetteCol, locAbonneCol, locDateAllocCol, locDateRetCol, locStatutCol;

    // ---- Stats + Profil
    @FXML
    private Label statTotalCassettes2, statTotalAbonnes2, statLocActives2, statsResume;
    @FXML
    private Label profilEmail, profilRole, profilMessage;
    @FXML
    private PasswordField newPasswordField, confirmPasswordField;

    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final AbonneDAO abonneDAO = new AbonneDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private final LocationDAO locationDAO = new LocationDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final CarteAbonneDAO carteAbonneDAO = new CarteAbonneDAO();

    private ObservableList<Cassette> cassettesData = FXCollections.observableArrayList();
    private ObservableList<Cassette> cassettesTableData = FXCollections.observableArrayList();
    private ObservableList<Abonne> abonnesData = FXCollections.observableArrayList();
    private ObservableList<Categorie> categoriesData = FXCollections.observableArrayList();
    private ObservableList<Location> locationsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTables();
        setupSearch();
        showAccueil();
    }

    // =========================================================
    // Configuration des colonnes de toutes les TableView
    // =========================================================
    private void setupTables() {
        // --- Cassettes TableView (page Cassettes)
        cassIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdCassette()).asObject());
        cassTitreCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitre()));
        cassDureeCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDuree() + " min"));
        cassCatCol.setCellValueFactory(d -> {
            Categorie cat = categorieDAO.getById(d.getValue().getIdCategorie());
            return new javafx.beans.property.SimpleStringProperty(cat != null ? cat.getLibelleCategorie() : "—");
        });
        cassPrixCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getPrix())));
        cassDateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateAchat() != null ? d.getValue().getDateAchat().toString() : ""));
        cassDispoCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().estDisponible() ? "✅ Oui" : "❌ Non"));
        cassetteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Abonnés
        abCodeCol
                .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCodeAbonne()));
        abNomCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNomAbonne()));
        abAdresseCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAdresseAbonne()));
        abDateAbCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateAbonement() != null ? d.getValue().getDateAbonement().toString() : ""));
        abDateEntCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateEntree() != null ? d.getValue().getDateEntree().toString() : ""));
        abNbLocCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getNombreLocations()).asObject());

        // --- Cellule d'Action (Imprimer Carte)
        abActionsCol.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        abActionsCol.setCellFactory(param -> new TableCell<Abonne, Abonne>() {
            private final Button btn = new Button("🖨️ Imprimer");
            {
                btn.getStyleClass().add("action-button-secondary");
                btn.setOnAction(event -> {
                    Abonne a = getItem();
                    if (a != null) {
                        btn.setDisable(true); // Eviter les clics multiples
                        new Thread(() -> {
                            String path = com.videocassette.util.PDFUtils.generateMemberCard(a);
                            javafx.application.Platform.runLater(() -> {
                                btn.setDisable(false);
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

        // --- Catégories
        catIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdCategorie()).asObject());
        catLibelleCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getLibelleCategorie()));
        categorieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Locations
        locIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdLocation()).asObject());
        locCassetteCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCassetteTitre()));
        locAbonneCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAbonneNom()));
        locDateAllocCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateAllocation() != null ? d.getValue().getDateAllocation().toString() : ""));
        locDateRetCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateRetour() != null ? d.getValue().getDateRetour().toString() : "—"));
        locStatutCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().estActive() ? "🟢 Active" : "⚪ Terminée"));
        locationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // =========================================================
    // Barre de recherche
    // =========================================================
    private void setupSearch() {
        // Recherche dans le catalogue Accueil (cartes)
        searchCassette.textProperty().addListener((obs, old, val) -> {
            applyCassettesFilter();
        });

        // Recherche dans TableView cassettes (page Cassettes)
        searchCassetteTable.textProperty().addListener((obs, old, val) -> {
            cassetteTable.setItems(new FilteredList<>(cassettesTableData,
                    c -> val == null || val.isEmpty() || c.getTitre().toLowerCase().contains(val.toLowerCase())));
        });

        // Recherche abonnés
        searchAbonne.textProperty().addListener((obs, old, val) -> {
            abonneTable.setItems(new FilteredList<>(abonnesData,
                    a -> val == null || val.isEmpty()
                            || a.getNomAbonne().toLowerCase().contains(val.toLowerCase())
                            || (a.getCodeAbonne() != null
                                    && a.getCodeAbonne().toLowerCase().contains(val.toLowerCase()))));
        });
    }

    // =========================================================
    // Navigation entre panneaux
    // =========================================================
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
        refreshStats();
        refreshAccueilCatalogue();
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

    @FXML
    public void deconnecter() {
        Session.clear();
        try {
            App.changerVue("/com/videocassette/views/login-view.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // Refresh des données
    // =========================================================
    private void refreshStats() {
        statCassettes.setText(String.valueOf(cassetteDAO.count()));
        statAbonnes.setText(String.valueOf(abonneDAO.count()));
        statLocationsActives.setText(String.valueOf(locationDAO.countActives()));
        statCategories.setText(String.valueOf(categorieDAO.getAll().size()));
    }

    /** Catalogue cassettes sur la page Accueil (cartes + louer) */
    private void refreshAccueilCatalogue() {
        cassettesData.setAll(cassetteDAO.getAll());
        setupCategoryFilters();
        applyCassettesFilter();
    }

    /** TableView cassettes sur la page Cassettes (CRUD) */
    private void refreshCassettesTable() {
        cassettesTableData.setAll(cassetteDAO.getAll());
        cassetteTable.setItems(cassettesTableData);
    }

    private ToggleGroup categoryToggleGroup = new ToggleGroup();

    private void setupCategoryFilters() {
        categoryFilterBox.getChildren().clear();
        categoryToggleGroup = new ToggleGroup();

        ToggleButton allBtn = new ToggleButton("Toutes");
        allBtn.getStyleClass().add("category-toggle-all");
        allBtn.setToggleGroup(categoryToggleGroup);
        allBtn.setSelected(true);
        allBtn.setUserData(null);
        allBtn.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        categoryFilterBox.getChildren().add(allBtn);

        for (Categorie cat : categorieDAO.getAll()) {
            ToggleButton btn = new ToggleButton(cat.getLibelleCategorie());
            btn.getStyleClass().add("category-toggle");
            btn.setToggleGroup(categoryToggleGroup);
            btn.setUserData(cat.getIdCategorie());
            btn.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            categoryFilterBox.getChildren().add(btn);
        }

        categoryToggleGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val == null) {
                allBtn.setSelected(true);
            } else {
                applyCassettesFilter();
            }
        });
    }

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
                cassettesFlowPane.getChildren().add(createAccueilCard(c));
            }
        }
    }

    /** Carte pour l'Accueil : affichage + bouton Louer uniquement */
    private VBox createAccueilCard(Cassette c) {
        VBox card = new VBox();
        card.getStyleClass().add("cassette-card");

        StackPane imageBox = new StackPane();
        imageBox.getStyleClass().add("cassette-image-box");

        Label icon = new Label("🎬");
        icon.setStyle("-fx-font-size: 48px;");

        Label badge = new Label(c.estDisponible() ? "Disponible" : "Indisponible");
        badge.getStyleClass().add(c.estDisponible() ? "badge-dispo" : "badge-indispo");
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);

        imageBox.getChildren().addAll(icon, badge);

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

        Button btnLouer = new Button("Louer");
        btnLouer.getStyleClass().add("cassette-btn-loue");
        btnLouer.setMaxWidth(Double.MAX_VALUE);
        btnLouer.setDisable(!c.estDisponible());
        btnLouer.setOnAction(e -> ouvrirDialogLouer(c));

        card.getChildren().addAll(imageBox, lblTitre, lblDuree, lblCat, lblPrix, btnLouer);
        return card;
    }

    /** Ouvre le dialog pour louer depuis l'accueil */
    private void ouvrirDialogLouer(Cassette c) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Louer : " + c.getTitre());
        dialog.setHeaderText("Sélectionnez l'abonné qui souhaite louer cette cassette.");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtCodeAbonne = new TextField();
        txtCodeAbonne.setPromptText("Saisir le code (ex: CLUB452)");
        txtCodeAbonne.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Code Abonné :"), 0, 0);
        grid.add(txtCodeAbonne, 1, 0);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String code = txtCodeAbonne.getText().trim();
                Abonne sel = abonneDAO.getByCode(code);
                if (sel == null) {
                    alerte("Code abonné inconnu. Veuillez vérifier.");
                    return false;
                }
                if (sel.louerCassette(c.getIdCassette())) {
                    alerte("Location enregistrée avec succès !");
                    return true;
                } else {
                    alerte("Impossible (max 3 locations actives ou cassette indisponible).");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait().ifPresent(ok -> {
            if (ok) {
                refreshStats();
                refreshAccueilCatalogue();
            }
        });
    }

    // =========================================================
    // CRUD Cassettes (page Cassettes - TableView)
    // =========================================================
    @FXML
    private void ajouterCassette() {
        Optional<Cassette> res = creerCassetteDialog(null).showAndWait();
        res.ifPresent(c -> {
            cassetteDAO.create(c);
            refreshCassettesTable();
        });
    }

    @FXML
    private void modifierCassetteSelecTable() {
        Cassette sel = cassetteTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner une cassette à modifier.");
            return;
        }
        Optional<Cassette> res = creerCassetteDialog(sel).showAndWait();
        res.ifPresent(c -> {
            c.setIdCassette(sel.getIdCassette());
            cassetteDAO.update(c);
            refreshCassettesTable();
        });
    }

    @FXML
    private void supprimerCassetteSelecTable() {
        Cassette sel = cassetteTable.getSelectionModel().getSelectedItem();
        if (sel != null && confirmer("Supprimer \"" + sel.getTitre() + "\" ?")) {
            cassetteDAO.delete(sel.getIdCassette());
            refreshCassettesTable();
        }
    }

    @FXML
    private void modifierCassette() {
    }

    @FXML
    private void supprimerCassette() {
    }

    private Dialog<Cassette> creerCassetteDialog(Cassette ex) {
        Dialog<Cassette> d = new Dialog<>();
        d.setTitle(ex == null ? "Ajouter une cassette" : "Modifier la cassette");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(20));
        TextField t = new TextField(ex != null ? ex.getTitre() : "");
        TextField du = new TextField(ex != null && ex.getDuree() != null ? String.valueOf(ex.getDuree()) : "");
        TextField p = new TextField(ex != null && ex.getPrix() != null ? String.valueOf(ex.getPrix()) : "");
        DatePicker da = new DatePicker(ex != null ? ex.getDateAchat() : LocalDate.now());
        ComboBox<Categorie> cb = new ComboBox<>(FXCollections.observableArrayList(categorieDAO.getAll()));
        if (ex != null)
            cb.getItems().stream().filter(c -> c.getIdCategorie() == ex.getIdCategorie()).findFirst()
                    .ifPresent(cb::setValue);
        g.add(new Label("Titre :"), 0, 0);
        g.add(t, 1, 0);
        g.add(new Label("Durée :"), 0, 1);
        g.add(du, 1, 1);
        g.add(new Label("Catégorie :"), 0, 2);
        g.add(cb, 1, 2);
        g.add(new Label("Prix :"), 0, 3);
        g.add(p, 1, 3);
        g.add(new Label("Date achat :"), 0, 4);
        g.add(da, 1, 4);
        d.getDialogPane().setContent(g);
        d.setResultConverter(b -> {
            if (b == ButtonType.OK && !t.getText().isEmpty() && cb.getValue() != null) {
                try {
                    return new Cassette(t.getText(), Integer.valueOf(du.getText()), cb.getValue().getIdCategorie(),
                            Double.valueOf(p.getText()), da.getValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        return d;
    }

    // =========================================================
    // CRUD Abonnés
    // =========================================================
    private void refreshAbonnes() {
        abonnesData.setAll(abonneDAO.getAll());
        abonneTable.setItems(abonnesData);
    }

    @FXML
    private void ajouterAbonne() {
        Optional<Abonne> res = creerAbonneDialog(null).showAndWait();
        res.ifPresent(a -> {
            if (abonneDAO.create(a)) {
                // L'ID est maintenant renseigné dans l'objet 'a' grâce à getGeneratedKeys
                CarteAbonne c = new CarteAbonne();
                c.setIdAbonne(a.getIdAbonne());
                carteAbonneDAO.create(c);

                // Générer la carte PDF automatiquement
                com.videocassette.util.PDFUtils.generateMemberCard(a);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Abonné ajouté avec succès");
                alert.setContentText(
                        "La carte de membre a été générée et enregistrée dans le dossier 'cards' du projet.");
                alert.showAndWait();

                refreshAbonnes();
            }
        });

    }

    @FXML
    private void modifierAbonne() {
        Abonne sel = abonneTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerte("Veuillez sélectionner un abonné à modifier.");
            return;
        }
        Optional<Abonne> res = creerAbonneDialog(sel).showAndWait();
        res.ifPresent(a -> {
            a.setIdAbonne(sel.getIdAbonne());
            abonneDAO.update(a);
            refreshAbonnes();
        });
    }

    @FXML
    private void supprimerAbonne() {
        Abonne sel = abonneTable.getSelectionModel().getSelectedItem();
        if (sel != null && confirmer("Supprimer " + sel.getNomAbonne() + " ?")) {
            abonneDAO.delete(sel.getIdAbonne());
            refreshAbonnes();
        }
    }

    private Dialog<Abonne> creerAbonneDialog(Abonne ex) {
        Dialog<Abonne> d = new Dialog<>();
        d.setTitle(ex == null ? "Ajouter un abonné" : "Modifier l'abonné");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(20));

        // Code random si nouvel abonné
        String generatedCode = "CLUB" + (100 + random.nextInt(900));
        TextField c = new TextField(ex != null ? ex.getCodeAbonne() : generatedCode);
        TextField n = new TextField(ex != null ? ex.getNomAbonne() : "");
        TextField ad = new TextField(ex != null ? ex.getAdresseAbonne() : "");
        DatePicker da = new DatePicker(ex != null ? ex.getDateAbonement() : LocalDate.now());
        DatePicker de = new DatePicker(ex != null ? ex.getDateEntree() : LocalDate.now());

        g.add(new Label("Code :"), 0, 0);
        g.add(c, 1, 0);
        g.add(new Label("Nom :"), 0, 1);
        g.add(n, 1, 1);
        g.add(new Label("Adresse :"), 0, 2);
        g.add(ad, 1, 2);
        g.add(new Label("Date Ab. :"), 0, 3);
        g.add(da, 1, 3);
        g.add(new Label("Date Entrée :"), 0, 4);
        g.add(de, 1, 4);

        d.getDialogPane().setContent(g);
        d.setResultConverter(b -> (b == ButtonType.OK)
                ? new Abonne(c.getText(), n.getText(), ad.getText(), da.getValue(), de.getValue())
                : null);
        return d;
    }

    // =========================================================
    // CRUD Catégories
    // =========================================================
    private void refreshCategories() {
        categoriesData.setAll(categorieDAO.getAll());
        categorieTable.setItems(categoriesData);
    }

    @FXML
    private void ajouterCategorie() {
        TextInputDialog id = new TextInputDialog();
        id.setTitle("Ajouter une catégorie");
        id.setContentText("Nom :");
        id.showAndWait().ifPresent(v -> {
            if (v != null && !v.isEmpty())
                categorieDAO.create(new Categorie(v));
            refreshCategories();
        });
    }

    @FXML
    private void modifierCategorie() {
        Categorie sel = categorieTable.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        TextInputDialog id = new TextInputDialog(sel.getLibelleCategorie());
        id.setTitle("Modifier la catégorie");
        id.showAndWait().ifPresent(v -> {
            if (v != null && !v.isEmpty()) {
                sel.setLibelleCategorie(v);
                categorieDAO.update(sel);
                refreshCategories();
            }
        });
    }

    @FXML
    private void supprimerCategorie() {
        Categorie sel = categorieTable.getSelectionModel().getSelectedItem();
        if (sel != null && confirmer("Supprimer \"" + sel.getLibelleCategorie() + "\" ?")) {
            categorieDAO.delete(sel.getIdCategorie());
            refreshCategories();
        }
    }

    // =========================================================
    // Locations
    // =========================================================
    private void refreshLocations() {
        locationsData.setAll(locationDAO.getAll());
        locationTable.setItems(locationsData);
    }

    @FXML
    private void nouvelleLocation() {
        List<Cassette> cassettesDispo = cassetteDAO.getAll().stream().filter(Cassette::estDisponible).toList();
        if (cassettesDispo.isEmpty()) {
            alerte("Il n'y a aucune cassette disponible.");
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Location");
        dialog.setHeaderText("Enregistrer une location");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<Cassette> cbCassette = new ComboBox<>(FXCollections.observableArrayList(cassettesDispo));
        cbCassette.setPromptText("Sélectionnez une cassette");
        grid.add(new Label("Cassette :"), 0, 0);
        grid.add(cbCassette, 1, 0);

        TextField txtCodeAbonne = new TextField();
        txtCodeAbonne.setPromptText("Ex: CLUB123");
        grid.add(new Label("Code Abonné :"), 0, 1);
        grid.add(txtCodeAbonne, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                Cassette selectedCassette = cbCassette.getValue();
                String code = txtCodeAbonne.getText().trim();
                Abonne selectedAbonne = abonneDAO.getByCode(code);

                if (selectedCassette == null) {
                    alerte("Veuillez sélectionner une cassette.");
                    return false;
                }
                if (selectedAbonne == null) {
                    alerte("Code abonné invalide.");
                    return false;
                }
                if (selectedAbonne.louerCassette(selectedCassette.getIdCassette())) {
                    alerte("Location enregistrée avec succès !");
                    return true;
                } else {
                    alerte("Impossible de créer la location (max 3 cassettes ou indisponible).");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                refreshLocations();
                refreshStats();
            }
        });
    }

    @FXML
    private void cloturerLocation() {
        Location sel = locationTable.getSelectionModel().getSelectedItem();
        if (sel != null && sel.estActive() && confirmer("Clôturer cette location ?")) {
            locationDAO.cloturerLocation(sel.getIdLocation());
            refreshLocations();
        }
    }

    // =========================================================
    // Statistiques
    // =========================================================
    private void refreshStatistiques() {
        int tc = cassetteDAO.count();
        int ta = abonneDAO.count();
        int la = locationDAO.countActives();
        statTotalCassettes2.setText(String.valueOf(tc));
        statTotalAbonnes2.setText(String.valueOf(ta));
        statLocActives2.setText(String.valueOf(la));
        statsResume.setText("Résumé : " + tc + " cassettes, " + ta + " abonnés, " + la + " locations en cours.");
    }

    // =========================================================
    // Profil
    // =========================================================
    @FXML
    private void changerMotDePasse() {
        String p1 = newPasswordField.getText();
        if (p1 != null && p1.equals(confirmPasswordField.getText()) && !p1.isEmpty()) {
            utilisateurDAO.updatePassword(Session.getCurrentUser().getIdUtilisateur(), p1);
            profilMessage.setText("Mot de passe modifié avec succès.");
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            profilMessage.setText("Erreur : les mots de passe ne correspondent pas.");
        }
    }

    // =========================================================
    // Utilitaires
    // =========================================================
    private void alerte(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    private boolean confirmer(String m) {
        return new Alert(Alert.AlertType.CONFIRMATION, m, ButtonType.YES, ButtonType.NO).showAndWait()
                .get() == ButtonType.YES;
    }
}
