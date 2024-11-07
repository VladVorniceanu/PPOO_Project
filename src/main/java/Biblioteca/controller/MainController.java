package Biblioteca.controller;

import static Biblioteca.model.Carte.validateBookData;
import Biblioteca.model.Biblioteca;
import Biblioteca.model.Carte;
import Biblioteca.model.Categorie;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.Optional;
import Biblioteca.model.InvalidBookDataException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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

    private Biblioteca biblioteca = new Biblioteca();
    private ObservableList<Carte> cartiObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        double numarColoane = 5.0;
        titluColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        autorColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        categorieColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        colectieColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));
        dataAchizitieColumn.prefWidthProperty().bind(tableView.widthProperty().divide(numarColoane));

        titluColumn.setCellValueFactory(new PropertyValueFactory<>("titlu"));
        autorColumn.setCellValueFactory(new PropertyValueFactory<>("autor"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colectieColumn.setCellValueFactory(new PropertyValueFactory<>("colectie"));
        dataAchizitieColumn.setCellValueFactory(new PropertyValueFactory<>("dataAchizitie"));

        biblioteca.incarcaDinFisier("biblioteca.txt");
        cartiObservableList.setAll(biblioteca.getCarti());  // Asigură-te că toate cărțile sunt setate
        tableView.setItems(cartiObservableList);

        // Salvăm datele când aplicația se închide
        Platform.runLater(() -> {
            tableView.getScene().getWindow().setOnCloseRequest(event -> {
                biblioteca.salveazaInFisier("biblioteca.txt");
            });
        });
    }

    @FXML
    public void handleRefresh() {
        tableView.setItems(FXCollections.observableArrayList(biblioteca.getCarti()));
    }

    public void handleAddBook() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Adaugă Carte Nouă");

        // Titlul
        String titlu = null;
        while (titlu == null || titlu.trim().isEmpty()) {
            dialog.setHeaderText("Introduceți titlul cărții:");
            Optional<String> titluOpt = dialog.showAndWait();

            // Dacă utilizatorul apasă "Cancel" în dialog, ieșim din metodă
            if (!titluOpt.isPresent()) return;

            titlu = titluOpt.get().trim();

            // Afișăm un mesaj de eroare dacă titlul este gol
            if (titlu.isEmpty()) {
                showError("Titlul nu poate fi gol!");
            }
        }

        dialog.getEditor().clear();

        // Autorul
        String autor = null;
        while (autor == null || autor.trim().isEmpty()) {
            dialog.setHeaderText("Introduceți autorul cărții:");
            Optional<String> autorOpt = dialog.showAndWait();
            if (!autorOpt.isPresent()) return;

            autor = autorOpt.get().trim();
            if (autor.isEmpty()) {
                showError("Autorul nu poate fi gol!");
            }
        }

        dialog.getEditor().clear();

        // Selectarea categoriei din enum
        ChoiceDialog<Categorie> categorieDialog = new ChoiceDialog<>(Categorie.LITERATURA, Categorie.values());
        categorieDialog.setTitle("Selectare Categorie");
        categorieDialog.setHeaderText("Alegeți categoria cărții:");
        Optional<Categorie> categorieOpt = categorieDialog.showAndWait();
        if (!categorieOpt.isPresent()) return;
        Categorie categorie = categorieOpt.get();

        // Colecția
        String colectie = null;
        while (colectie == null || colectie.trim().isEmpty()) {
            dialog.setHeaderText("Introduceți colecția cărții:");
            Optional<String> colectieOpt = dialog.showAndWait();
            if (!colectieOpt.isPresent()) return;

            colectie = colectieOpt.get().trim();
            if (colectie.isEmpty()) {
                showError("Colecția nu poate fi goală!");
            }
        }

        Carte carte = new Carte(titlu, autor, categorie, colectie, LocalDate.now(), false);
        biblioteca.adaugaCarte(carte);

        // Adăugăm cartea în lista observabilă (care este asociată cu TableView)
        cartiObservableList.add(carte);
    }

    public void handleDeleteBook() {
        Carte carteSelectata = tableView.getSelectionModel().getSelectedItem();
        if (carteSelectata != null) {
            biblioteca.stergeCarte(carteSelectata);
            cartiObservableList.remove(carteSelectata);
        }
    }

    public void handleSearchByAuthor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Căutare Cărți după Autor");
        dialog.setHeaderText("Introduceți autorul:");

        Optional<String> autorOpt = dialog.showAndWait();
        if (autorOpt.isPresent()) {
            String autor = autorOpt.get();
            tableView.setItems(FXCollections.observableArrayList(biblioteca.cautaCartiDupaAutor(autor)));
        }
    }

    public void handleSearchByCollection() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Căutare Cărți după Colecție");
        dialog.setHeaderText("Introduceți colecția:");

        Optional<String> colectieOpt = dialog.showAndWait();
        if (colectieOpt.isPresent()) {
            String colectie = colectieOpt.get();
            tableView.setItems(FXCollections.observableArrayList(biblioteca.cautaCartiDupaColectie(colectie)));
        }
    }
    public void handleGenerateReportByAuthor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Generare Raport");
        dialog.setHeaderText("Introduceți autorul:");

        Optional<String> autorOpt = dialog.showAndWait();
        autorOpt.ifPresent(autor -> {
            biblioteca.genereazaRaportAutor(autor, "raport_" + autor + ".txt");
            System.out.println("Raport generat pentru autorul: " + autor);
        });
    }

    public void handleGenerateReportByCollection() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Generare Raport");
        dialog.setHeaderText("Introduceți colecția:");

        Optional<String> colectieOpt = dialog.showAndWait();
        colectieOpt.ifPresent(colectie -> {
            biblioteca.genereazaRaportColectie(colectie, "raport_" + colectie + ".txt");
            System.out.println("Raport generat pentru colecția: " + colectie);
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText("Date invalide");
        alert.setContentText(message);
        alert.showAndWait();
    }
}