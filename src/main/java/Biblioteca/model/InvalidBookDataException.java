package Biblioteca.model;

public class InvalidBookDataException extends Exception {
    public InvalidBookDataException(String message) {
        super(message);
    }
}