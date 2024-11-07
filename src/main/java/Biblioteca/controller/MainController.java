package Biblioteca.controller;

import static Biblioteca.model.Carte.validateBookData;
import Biblioteca.model.Biblioteca;
import Biblioteca.model.Carte;
import Biblioteca.model.Categorie;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import Biblioteca.model.InvalidBookDataException;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private TableView<Carte> tableView;
    @FXML
    private TableColumn<Carte, String> titluColumn;
    @FXML
    private TableColumn<Carte, String> autorColumn;
    @FXML
    private TableColumn<Carte, Categorie> categorieColumn;
    @FXML
    private TableColumn<Carte, String> colectieColumn;
    @FXML
    private TableColumn<Carte, LocalDate> dataAchizitieColumn;
    @FXML
    private TableColumn<Carte, Boolean> cititaColumn;

    private Biblioteca biblioteca = new Biblioteca();
    private ObservableList<Carte> cartiObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        double numarColoane = 6.0;
        titluColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        autorColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        categorieColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        colectieColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        dataAchizitieColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        cititaColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));

        titluColumn.setCellValueFactory(new PropertyValueFactory<>("titlu"));
        autorColumn.setCellValueFactory(new PropertyValueFactory<>("autor"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colectieColumn.setCellValueFactory(new PropertyValueFactory<>("colectie"));
        dataAchizitieColumn.setCellValueFactory(new PropertyValueFactory<>("dataAchizitie"));

        cititaColumn.setCellValueFactory(cellData -> {
            Carte carte = cellData.getValue();
            return new SimpleBooleanProperty(carte.isCitita());
        });

        cititaColumn.setCellFactory(tc -> new CheckBoxTableCell<Carte, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    setGraphic(checkBox);

                    // Actualizare proprietatea `citita`
                    checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        Carte carte = getTableView().getItems().get(getIndex());
                        carte.setCitita(isNowSelected);
                    });
                }
            }
        });

        biblioteca.incarcaDinFisier("src/main/java/Biblioteca/biblioteca.txt");
        cartiObservableList.setAll(biblioteca.getCarti());  // Asigură-te că toate cărțile sunt setate
        tableView.setItems(cartiObservableList);

        // Salvăm datele când aplicația se închide
        Platform.runLater(() -> {
            tableView.getScene().getWindow().setOnCloseRequest(event -> {
                biblioteca.salveazaInFisier("src/main/java/Biblioteca/biblioteca.txt");
            });
        });

        tableView.setRowFactory(tv -> {
            TableRow<Carte> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Carte carte = row.getItem();
                    showEditDialog(carte);
                }
            });
            return row;
        });

    }

    @FXML
    public void handleRefresh() {
        tableView.setItems(FXCollections.observableArrayList(biblioteca.getCarti()));
    }

    public void handleAddBook() {
        // Fereastra noua pentru introducere date
        Stage addBookStage = new Stage();
        addBookStage.setTitle("Adaugă Carte Nouă");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Titlu
        TextField titluField = new TextField();
        titluField.setPromptText("Titlu");

        // Autor
        TextField autorField = new TextField();
        autorField.setPromptText("Autor");

        // Categorie
        ChoiceBox<Categorie> categorieChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Categorie.values()));
        categorieChoiceBox.setValue(Categorie.LITERATURA); // Valoare implicită

        // Colecție
        TextField colectieField = new TextField();
        colectieField.setPromptText("Colecție");

        // Citita - checkbox pentru starea de citit a cărții
        CheckBox cititaCheckBox = new CheckBox("Cartea a fost citită?");

        // Butonul de salvare
        Button saveButton = new Button("Salvează");
        saveButton.setOnAction(e -> {
            String titlu = titluField.getText().trim();
            String autor = autorField.getText().trim();
            String colectie = colectieField.getText().trim();
            Categorie categorie = categorieChoiceBox.getValue();
            boolean citita = cititaCheckBox.isSelected();

            // Validare date
            try {
                validateBookData(titlu, autor, colectie);

                // Creez cartea și o adaug în lista
                Carte carte = new Carte(titlu, autor, categorie, colectie, LocalDate.now(), citita);
                biblioteca.adaugaCarte(carte);
                cartiObservableList.add(carte);

                addBookStage.close(); // inchidere fereastra
            } catch (InvalidBookDataException ex) {
                showError(ex.getMessage()); // Afișăm eroarea dacă datele sunt invalide
            }
        });

        // Adaug elementele în layout
        vbox.getChildren().addAll(
                new Label("Titlu"), titluField,
                new Label("Autor"), autorField,
                new Label("Colecție"), colectieField,
                new Label("Categorie"), categorieChoiceBox,
                cititaCheckBox,
                saveButton
        );

        // Setez scena
        Scene scene = new Scene(vbox);
        addBookStage.setScene(scene);
        addBookStage.initModality(Modality.APPLICATION_MODAL); // Blochează fereastra principală până se închide aceasta
        addBookStage.showAndWait();
    }
    public void handleDeleteBook() {
        Carte carteSelectata = tableView.getSelectionModel().getSelectedItem();
        if (carteSelectata != null) {
            biblioteca.stergeCarte(carteSelectata);
            cartiObservableList.remove(carteSelectata);
        }
    }

    public void handleSearchByAuthor() {
        List<String> autori = biblioteca.getAutoriUnici();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(autori.get(0), autori);
        dialog.setTitle("Căutare Cărți după Autor");
        dialog.setHeaderText("Alege autorul:");

        Optional<String> autorOpt = dialog.showAndWait();
        if (autorOpt.isPresent()) {
            String autor = autorOpt.get();
            tableView.setItems(FXCollections.observableArrayList(biblioteca.cautaCartiDupaAutor(autor)));
        }
    }

    public void handleSearchByCollection() {
        List<String> colectiiUnice = biblioteca.getColectiiUnice();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(colectiiUnice.get(0), colectiiUnice);
        dialog.setTitle("Căutare Cărți după Colecție");
        dialog.setHeaderText("Alege colecția:");

        Optional<String> colectieOpt = dialog.showAndWait();
        if (colectieOpt.isPresent()) {
            String colectie = colectieOpt.get();
            tableView.setItems(FXCollections.observableArrayList(biblioteca.cautaCartiDupaColectie(colectie)));
        }
    }
    public void handleGenerateReportByAuthor() {
        List<String> autoriUnici = biblioteca.getAutoriUnici();
        autoriUnici.add(0, "Introduceți manual");

        // ChoiceDialog pentru selectarea autorului
        ChoiceDialog<String> dialog = new ChoiceDialog<>(autoriUnici.get(0), autoriUnici);
        dialog.setTitle("Generare Raport Autor");
        dialog.setHeaderText("Selectați autorul pentru raport sau alegeți să introduceți manual:");

        Optional<String> autorOpt = dialog.showAndWait();
        autorOpt.ifPresent(autor -> {
            if (autor.equals("Introduceți manual")) {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Introduceți Autorul");
                inputDialog.setHeaderText("Introduceți numele autorului pentru raport:");
                Optional<String> manualAutor = inputDialog.showAndWait();
                manualAutor.ifPresent(manualAutorName -> {
                    String numeFisier = "raport_" + manualAutorName + ".txt";
                    biblioteca.genereazaRaportAutor(manualAutorName, numeFisier);
                    showConfirmationMessage(numeFisier);
                });
            } else {
                String numeFisier = "raport_" + autor + ".txt";
                biblioteca.genereazaRaportAutor(autor, numeFisier);
                showConfirmationMessage(numeFisier);
            }
        });
    }

    public void handleGenerateReportByCollection() {
        List<String> colectiiUnice = biblioteca.getColectiiUnice();
        colectiiUnice.add(0, "Introduceți manual");

        // ChoiceDialog pentru selectarea colecției
        ChoiceDialog<String> dialog = new ChoiceDialog<>(colectiiUnice.get(0), colectiiUnice);
        dialog.setTitle("Generare Raport Colecție");
        dialog.setHeaderText("Selectați colecția pentru raport sau alegeți să introduceți manual:");

        Optional<String> colectieOpt = dialog.showAndWait();
        colectieOpt.ifPresent(colectie -> {
            if (colectie.equals("Introduceți manual")) {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Introduceți Colecția");
                inputDialog.setHeaderText("Introduceți numele colecției pentru raport:");
                Optional<String> manualColectie = inputDialog.showAndWait();
                manualColectie.ifPresent(manualColectieName -> {
                    String numeFisier = "raport_" + manualColectieName + ".txt";
                    biblioteca.genereazaRaportColectie(manualColectieName, numeFisier);
                    showConfirmationMessage(numeFisier); // Afișează mesajul de confirmare
                });
            } else {
                String numeFisier = "raport_" + colectie + ".txt";
                biblioteca.genereazaRaportColectie(colectie, numeFisier);
                showConfirmationMessage(numeFisier); // Afișează mesajul de confirmare
            }
        });
    }

    public void handleGenerateReadBooksReport() {
        biblioteca.genereazaRaportCartiCitite();
        String dataCurenta = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String numeFisier = "raport_carti_citite_" + dataCurenta + ".txt";

        showConfirmationMessage(numeFisier);
    }

    private static void showConfirmationMessage(String numeFisier) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Raport Generat");
        alert.setHeaderText("Raportul a fost generat cu succes!");
        alert.setContentText("Fișierul \"" + numeFisier + "\" a fost creat.");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText("Date invalide");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showEditDialog(Carte carte) {
        Stage editStage = new Stage();
        editStage.setTitle("Editare Carte");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Titlu
        TextField titluField = new TextField(carte.getTitlu());
        titluField.setPromptText("Titlu");

        // Autor
        TextField autorField = new TextField(carte.getAutor());
        autorField.setPromptText("Autor");

        // Categorie
        ChoiceBox<Categorie> categorieChoice = new ChoiceBox<>(FXCollections.observableArrayList(Categorie.values()));
        categorieChoice.setValue(carte.getCategorie());

        // Colecție
        TextField colectieField = new TextField(carte.getColectie());
        colectieField.setPromptText("Colecție");

        // Citita
        CheckBox cititaCheckBox = new CheckBox("Cartea a fost citită?");
        cititaCheckBox.setSelected(carte.isCitita());

        // Butonul de salvare
        Button saveButton = new Button("Salvează");
        saveButton.setOnAction(e -> {
            carte.setTitlu(titluField.getText().trim());
            carte.setAutor(autorField.getText().trim());
            carte.setCategorie(categorieChoice.getValue());
            carte.setColectie(colectieField.getText().trim());
            carte.setCitita(cititaCheckBox.isSelected());

            tableView.refresh(); // Actualizează `TableView`
            editStage.close(); // Închide fereastra de editare
        });

        vbox.getChildren().addAll(
                new Label("Titlu"), titluField,
                new Label("Autor"), autorField,
                new Label("Categorie"), categorieChoice,
                new Label("Colecție"), colectieField,
                cititaCheckBox,
                saveButton
        );

        Scene scene = new Scene(vbox);
        editStage.setScene(scene);
        editStage.initModality(Modality.APPLICATION_MODAL); // Blochează fereastra principală până se închide aceasta
        editStage.showAndWait();
    }
}