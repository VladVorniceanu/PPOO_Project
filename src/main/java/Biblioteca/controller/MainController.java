package Biblioteca.controller;

import Biblioteca.model.Carte;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class MainController {

    @FXML
    private TableView<Carte> tableView;

    public void handleAddBook() {
        // Logica pentru a adăuga o carte nouă
    }

    public void handleSearchByAuthor() {
        // Logica pentru a căuta cărți după autor
    }
}