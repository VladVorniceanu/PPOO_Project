package Biblioteca.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Carte implements Serializable {
    private String titlu;
    private String autor;
    private String colectie;
    private LocalDate dataAchizitie;
    private boolean imprumutata;

    public Carte(String titlu, String autor, String colectie, LocalDate dataAchizitie, boolean imprumutata) {
        this.titlu = titlu;
        this.autor = autor;
        this.colectie = colectie;
        this.dataAchizitie = dataAchizitie;
        this.imprumutata = imprumutata;
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

    public String getColectie() {
        return colectie;
    }

    public void setColectie(String colectie) {
        this.colectie = colectie;
    }

    public LocalDate getDataAchizitie() {
        return dataAchizitie;
    }

    public void setDataAchizitie(LocalDate dataAchizitie) {
        this.dataAchizitie = dataAchizitie;
    }

    public boolean isImprumutata() {
        return imprumutata;
    }

    public void setImprumutata(boolean imprumutata) {
        this.imprumutata = imprumutata;
    }
}