module org.example.ppoolibrary {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;

    opens Biblioteca to javafx.fxml;
    exports Biblioteca;
    exports Biblioteca.model;
    opens Biblioteca.model to javafx.fxml;
    exports Biblioteca.controller;
    opens Biblioteca.controller to javafx.fxml;
}