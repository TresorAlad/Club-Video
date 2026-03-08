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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import javafx.application.Platform;

public class DashboardController {

    @FXML
    private Button btnAccueil, btnCassettes, btnAbonnes, btnCategories, btnLocations, btnStats, btnProfil;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox accueilPane, cassettesPane, abonnesPane, categoriesPane, locationsPane, statistiquesPane, profilPane;
    @FXML
    private Label statCassettes, statAbonnes, statLocationsActives, statCategories;

    @FXML
    private FlowPane cassettesFlowPane;
    @FXML
    private HBox categoryFilterBox;
    @FXML
    private TextField searchCassette;

    @FXML
    private TableView<Abonne> abonneTable;
    @FXML
    private TableColumn<Abonne, Integer> abIdCol;
    @FXML
    private TableColumn<Abonne, String> abNomCol, abAdresseCol, abDateAbCol, abDateEntCol;
    @FXML
    private TextField searchAbonne;

    @FXML
    private TableView<Categorie> categorieTable;
    @FXML
    private TableColumn<Categorie, Integer> catIdCol;
    @FXML
    private TableColumn<Categorie, String> catLibelleCol;

    @FXML
    private TableView<Location> locationTable;
    @FXML
    private TableColumn<Location, Integer> locIdCol;
    @FXML
    private TableColumn<Location, String> locCassetteCol, locAbonneCol, locDateAllocCol, locDateRetCol, locStatutCol;

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
    private ObservableList<Abonne> abonnesData = FXCollections.observableArrayList();
    private ObservableList<Categorie> categoriesData = FXCollections.observableArrayList();
    private ObservableList<Location> locationsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTables();
        setupSearch();
        applySecurity();
        showAccueil();
    }

    private void applySecurity() {
        // Accès total pour tous les utilisateurs (aucun bouton masqué)
    }

    private void setupTables() {
        // cassetteTable has been replaced by cassettesFlowPane

        abIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdAbonne()).asObject());
        abNomCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNomAbonne()));
        abAdresseCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAdresseAbonne()));
        abDateAbCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateAbonement() != null ? d.getValue().getDateAbonement().toString() : ""));
        abDateEntCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateEntree() != null ? d.getValue().getDateEntree().toString() : ""));
        abonneTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        catIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdCategorie()).asObject());
        catLibelleCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getLibelleCategorie()));
        categorieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        locIdCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdLocation()).asObject());
        locCassetteCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCassetteTitre()));
        locAbonneCol
                .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAbonneNom()));
        locDateAllocCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateAllocation() != null ? d.getValue().getDateAllocation().toString() : ""));
        locDateRetCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateRetour() != null ? d.getValue().getDateRetour().toString() : "—"));
        locStatutCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().estActive() ? "🟢 Active" : "⚪ Terminée"));
        locationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupSearch() {
        searchCassette.textProperty().addListener((obs, old, val) -> {
            applyCassettesFilter();
        });
        searchAbonne.textProperty().addListener((obs, old, val) -> {
            abonneTable.setItems(new FilteredList<>(abonnesData,
                    a -> val == null || val.isEmpty() || a.getNomAbonne().toLowerCase().contains(val.toLowerCase())));
        });
    }

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
    }

    @FXML
    public void showCassettes() {
        showPane(cassettesPane);
        refreshCassettes();
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
            profilRole.setText("Rôle : " + u.getRole().toUpperCase());
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

    private void refreshStats() {
        statCassettes.setText(String.valueOf(cassetteDAO.count()));
        statAbonnes.setText(String.valueOf(abonneDAO.count()));
        statLocationsActives.setText(String.valueOf(locationDAO.countActives()));
        statCategories.setText(String.valueOf(categorieDAO.getAll().size()));
    }

    private void refreshCassettes() {
        cassettesData.setAll(cassetteDAO.getAll());
        setupCategoryFilters();
        applyCassettesFilter();
    }

    private ToggleGroup categoryToggleGroup = new ToggleGroup();

    private void setupCategoryFilters() {
        categoryFilterBox.getChildren().clear();

        ToggleButton allBtn = new ToggleButton("Toutes");
        allBtn.getStyleClass().add("category-toggle-all");
        allBtn.setToggleGroup(categoryToggleGroup);
        allBtn.setSelected(true);
        allBtn.setUserData(null);
        allBtn.setMinSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
        categoryFilterBox.getChildren().add(allBtn);

        for (Categorie cat : categorieDAO.getAll()) {
            ToggleButton btn = new ToggleButton(cat.getLibelleCategorie());
            btn.getStyleClass().add("category-toggle");
            btn.setToggleGroup(categoryToggleGroup);
            btn.setUserData(cat.getIdCategorie());
            btn.setMinSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
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
                cassettesFlowPane.getChildren().add(createCassetteCard(c));
            }
        }
    }

    private VBox createCassetteCard(Cassette c) {
        VBox card = new VBox();
        card.getStyleClass().add("cassette-card");

        StackPane imageBox = new StackPane();
        imageBox.getStyleClass().add("cassette-image-box");

        Label icon = new Label("🎬");
        icon.setStyle("-fx-font-size: 50px;");

        Label badge = new Label(c.estDisponible() ? "Disponible" : "Indisponible");
        badge.getStyleClass().add(c.estDisponible() ? "badge-dispo" : "badge-indispo");
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);

        Button btnDelete = new Button("X");
        btnDelete.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 5;");
        btnDelete.setOnAction(e -> supprimerCassetteCard(c));
        StackPane.setAlignment(btnDelete, javafx.geometry.Pos.TOP_LEFT);

        imageBox.getChildren().addAll(icon, btnDelete, badge);

        Label lblTitre = new Label("Titre : " + c.getTitre());
        lblTitre.setStyle("-fx-font-weight: bold;");
        lblTitre.getStyleClass().add("cassette-info");

        Label lblDuree = new Label("Durée : " + c.getDuree());
        lblDuree.getStyleClass().add("cassette-info");

        Label lblPrix = new Label("Prix : " + c.getPrix() + " CFA");
        lblPrix.getStyleClass().add("cassette-info");

        Button btnLoue = new Button("Loué");
        btnLoue.getStyleClass().add("cassette-btn-loue");
        btnLoue.setMaxWidth(Double.MAX_VALUE);
        if (!c.estDisponible()) {
            btnLoue.setDisable(true);
        }

        card.getChildren().addAll(imageBox, lblTitre, lblDuree, lblPrix, btnLoue);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("✏ Modifier");
        editItem.setOnAction(e -> modifierCassetteCard(c));
        MenuItem deleteItem = new MenuItem("🗑 Supprimer");
        deleteItem.setOnAction(e -> supprimerCassetteCard(c));
        contextMenu.getItems().addAll(editItem, deleteItem);

        card.setOnContextMenuRequested(e -> contextMenu.show(card, e.getScreenX(), e.getScreenY()));

        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                modifierCassetteCard(c);
            }
        });

        return card;
    }

    private void modifierCassetteCard(Cassette sel) {
        if (sel == null)
            return;
        Optional<Cassette> res = creerCassetteDialog(sel).showAndWait();
        res.ifPresent(c -> {
            c.setIdCassette(sel.getIdCassette());
            cassetteDAO.update(c);
            refreshCassettes();
        });
    }

    private void supprimerCassetteCard(Cassette sel) {
        if (sel != null && confirmer("Supprimer \"" + sel.getTitre() + "\" ?")) {
            cassetteDAO.delete(sel.getIdCassette());
            refreshCassettes();
        }
    }

    private void refreshAbonnes() {
        abonnesData.setAll(abonneDAO.getAll());
        abonneTable.setItems(abonnesData);
    }

    private void refreshCategories() {
        categoriesData.setAll(categorieDAO.getAll());
        categorieTable.setItems(categoriesData);
    }

    private void refreshLocations() {
        locationsData.setAll(locationDAO.getAll());
        locationTable.setItems(locationsData);
    }

    private void refreshStatistiques() {
        int tc = cassetteDAO.count();
        int ta = abonneDAO.count();
        int la = locationDAO.countActives();
        statTotalCassettes2.setText(String.valueOf(tc));
        statTotalAbonnes2.setText(String.valueOf(ta));
        statLocActives2.setText(String.valueOf(la));
        statsResume.setText("Résumé: " + tc + " cassettes, " + ta + " abonnés, " + la + " locations en cours.");
    }

    @FXML
    private void ajouterCassette() {
        Optional<Cassette> res = creerCassetteDialog(null).showAndWait();
        res.ifPresent(c -> {
            cassetteDAO.create(c);
            refreshCassettes();
        });
    }

    @FXML
    private void modifierCassette() {
        // Obsolete function, replaced by modifierCassetteCard through ContextMenu
    }

    @FXML
    private void supprimerCassette() {
        // Obsolete function, replaced by supprimerCassetteCard through ContextMenu
    }

    private Dialog<Cassette> creerCassetteDialog(Cassette ex) {
        Dialog<Cassette> d = new Dialog<>();
        d.setTitle(ex == null ? "Ajouter" : "Modifier");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(20));
        TextField t = new TextField(ex != null ? ex.getTitre() : "");
        TextField du = new TextField(ex != null ? ex.getDuree() : "");
        TextField p = new TextField(ex != null ? ex.getPrix() : "");
        DatePicker da = new DatePicker(ex != null ? ex.getDateAchat() : LocalDate.now());
        ComboBox<Categorie> cb = new ComboBox<>(FXCollections.observableArrayList(categorieDAO.getAll()));
        if (ex != null)
            cb.getItems().stream().filter(c -> c.getIdCategorie() == ex.getIdCategorie()).findFirst()
                    .ifPresent(cb::setValue);
        g.add(new Label("Titre:"), 0, 0);
        g.add(t, 1, 0);
        g.add(new Label("Durée:"), 0, 1);
        g.add(du, 1, 1);
        g.add(new Label("Catégorie:"), 0, 2);
        g.add(cb, 1, 2);
        g.add(new Label("Prix:"), 0, 3);
        g.add(p, 1, 3);
        g.add(new Label("Date:"), 0, 4);
        g.add(da, 1, 4);
        d.getDialogPane().setContent(g);
        d.setResultConverter(b -> (b == ButtonType.OK)
                ? new Cassette(t.getText(), du.getText(), cb.getValue().getIdCategorie(), p.getText(), da.getValue())
                : null);
        return d;
    }

    @FXML
    private void ajouterAbonne() {
        Optional<Abonne> res = creerAbonneDialog(null).showAndWait();
        res.ifPresent(a -> {
            if (abonneDAO.create(a)) {
                CarteAbonne c = new CarteAbonne();
                c.setIdAbonne(a.getIdAbonne());
                carteAbonneDAO.create(c);
                refreshAbonnes();
            }
        });
    }

    @FXML
    private void modifierAbonne() {
        Abonne sel = abonneTable.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
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
        d.setTitle(ex == null ? "Ajouter" : "Modifier");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(20));
        TextField n = new TextField(ex != null ? ex.getNomAbonne() : "");
        TextField ad = new TextField(ex != null ? ex.getAdresseAbonne() : "");
        DatePicker da = new DatePicker(ex != null ? ex.getDateAbonement() : LocalDate.now());
        DatePicker de = new DatePicker(ex != null ? ex.getDateEntree() : LocalDate.now());
        g.add(new Label("Nom:"), 0, 0);
        g.add(n, 1, 0);
        g.add(new Label("Adresse:"), 0, 1);
        g.add(ad, 1, 1);
        g.add(new Label("Date Ab:"), 0, 2);
        g.add(da, 1, 2);
        g.add(new Label("Date En:"), 0, 3);
        g.add(de, 1, 3);
        d.getDialogPane().setContent(g);
        d.setResultConverter(
                b -> (b == ButtonType.OK) ? new Abonne(n.getText(), ad.getText(), da.getValue(), de.getValue()) : null);
        return d;
    }

    @FXML
    private void ajouterCategorie() {
        TextInputDialog id = new TextInputDialog();
        id.setTitle("Ajouter Categorie");
        id.setContentText("Nom:");
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
        id.setTitle("Modifier Categorie");
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
        grid.add(new Label("Cassette:"), 0, 0);
        grid.add(cbCassette, 1, 0);

        ComboBox<Abonne> cbAbonne = new ComboBox<>(FXCollections.observableArrayList(abonneDAO.getAll()));
        cbAbonne.setPromptText("Sélectionnez un abonné");
        grid.add(new Label("Abonné:"), 0, 1);
        grid.add(cbAbonne, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ComboBox<Abonne> finalCbAbonne = cbAbonne;
        dialog.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                Cassette selectedCassette = cbCassette.getValue();
                Abonne selectedAbonne = finalCbAbonne.getValue();

                if (selectedCassette == null) {
                    alerte("Veuillez sélectionner une cassette.");
                    return false;
                }
                if (selectedAbonne == null) {
                    alerte("Veuillez sélectionner un abonné valide.");
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
                refreshCassettes();
                refreshLocations();
                refreshStatistiques();
            }
        });
    }

    @FXML
    private void cloturerLocation() {
        Location sel = locationTable.getSelectionModel().getSelectedItem();
        if (sel != null && sel.estActive() && confirmer("Clôturer ?")) {
            locationDAO.cloturerLocation(sel.getIdLocation());
            refreshLocations();
        }
    }

    @FXML
    private void changerMotDePasse() {
        String p1 = newPasswordField.getText();
        if (p1 != null && p1.equals(confirmPasswordField.getText()) && !p1.isEmpty()) {
            utilisateurDAO.updatePassword(Session.getCurrentUser().getIdUtilisateur(), p1);
            profilMessage.setText("Mot de passe modifié.");
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else
            profilMessage.setText("Erreur de confirmation.");
    }

    private void alerte(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    private boolean confirmer(String m) {
        return new Alert(Alert.AlertType.CONFIRMATION, m, ButtonType.YES, ButtonType.NO).showAndWait()
                .get() == ButtonType.YES;
    }
}
