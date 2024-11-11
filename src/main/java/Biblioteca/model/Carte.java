package Biblioteca.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Carte {
    private String titlu;
    private String autor;
    private Categorie categorie;
    private String colectie;
    private LocalDate dataAchizitie;
    private boolean citita;

    public Carte(String titlu, String autor, Categorie categorie, String colectie, LocalDate dataAchizitie, boolean citita) {
        this.titlu = titlu;
        this.autor = autor;
        this.categorie = categorie;
        this.colectie = colectie;
        this.dataAchizitie = dataAchizitie;
        this.citita = citita;
    }

    // Getters și Setters
    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public String getColectie() {
        return colectie;
    }

    public void setColectie(String colectie) {
        this.colectie = colectie;
    }

    public LocalDate getDataAchizitie() {
        return dataAchizitie;
    }

    public boolean isCitita() {
        return citita;
    }

    public void setCitita(boolean citita) {
        this.citita = citita;
    }

    @Override
    public String toString() {
        return getTitlu() + ";" + getAutor() + ";" + getCategorie() + ";" + getColectie() + ";" + getDataAchizitie() + ";" + isCitita();
    }

    public static void validateBookData(String titlu, String autor, String colectie) throws InvalidBookDataException {
        if (titlu == null || titlu.trim().isEmpty()) {
            throw new InvalidBookDataException("Titlul nu poate fi gol.");
        }
        if (autor == null || autor.trim().isEmpty()) {
            throw new InvalidBookDataException("Autorul nu poate fi gol.");
        }
        if (colectie == null || colectie.trim().isEmpty()) {
            throw new InvalidBookDataException("Colecția nu poate fi goală.");
        }
    }
}