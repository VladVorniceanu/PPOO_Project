package Biblioteca.model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Biblioteca.model.Carte.validateBookData;

public class Biblioteca {
    private List<Carte> carti;

    public Biblioteca() {
        this.carti = new ArrayList<>();
    }

    public void adaugaCarte(Carte carte) {
        carti.add(carte);
    }

    public void stergeCarte(Carte carte) {
        carti.remove(carte);
    }

    public List<Carte> cautaCartiDupaAutor(String autor) {
        return carti.stream()
                .filter(c -> c.getAutor().equalsIgnoreCase(autor))
                .collect(Collectors.toList());
    }

    public List<Carte> cautaCartiDupaColectie(String colectie) {
        return carti.stream()
                .filter(c -> c.getColectie().equalsIgnoreCase(colectie))
                .collect(Collectors.toList());
    }

    public List<Carte> getCarti() {
        return carti;
    }

    public void salveazaInFisier(String numeFisier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(numeFisier))) {
            for (Carte carte : carti) {
                writer.write(carte.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incarcaDinFisier(String numeFisier) {
        File fisier = new File(numeFisier);
        if (!fisier.exists()) {
            System.out.println("Fișierul " + numeFisier + " nu există. Se va crea automat la salvare.");
            return;
        }

        carti.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fisier))) {
            String linie;
            while ((linie = reader.readLine()) != null) {
                String[] campuri = linie.split(";");
                if (campuri.length == 6) {
                    String titlu = campuri[0];
                    String autor = campuri[1];
                    String categorie = campuri[2];
                    String colectie = campuri[3];
                    LocalDate dataImprumut = LocalDate.parse(campuri[4]);
                    boolean imprumutata = Boolean.parseBoolean(campuri[5]);

                    try {
                        validateBookData(titlu, autor, colectie);
                        Carte carte = new Carte(titlu, autor, Categorie.valueOf(categorie), colectie, dataImprumut, imprumutata);
                        carti.add(carte);
                    } catch (InvalidBookDataException e) {
                        System.out.println("Eroare la încărcarea cărții: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generarea unui raport pentru cărțile unui anumit autor
    public void genereazaRaportAutor(String autor, String numeFisier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(numeFisier))) {
            for (Carte carte : cautaCartiDupaAutor(autor)) {
                writer.write(carte.getTitlu() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generarea unui raport pentru cărțile dintr-o anumită colecție
    public void genereazaRaportColectie(String colectie, String numeFisier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(numeFisier))) {
            for (Carte carte : cautaCartiDupaColectie(colectie)) {
                writer.write(carte.getTitlu() + " - " + carte.getAutor() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genereazaRaportCartiCitite() {
        List<Carte> cartiCitite = carti.stream()
                .filter(Carte::isCitita)
                .collect(Collectors.toList());

        String dataCurenta = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String numeFisier = "raport_carti_citite_" + dataCurenta + ".txt";

        // Scrierea raportului în fișier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(numeFisier))) {
            for (Carte carte : cartiCitite) {
                writer.write(carte.getTitlu() + " - " + carte.getAutor() + " - " + carte.getCategorie() + "\n");
            }
            System.out.println("Raport generat: " + numeFisier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAutoriUnici() {
        List<String> autoriUnici = this.getCarti().stream()
                .map(Carte::getAutor)
                .distinct()
                .collect(Collectors.toList());
        return autoriUnici;
    }

    public List<String> getColectiiUnice() {
        List<String> colectiiUnice = this.getCarti().stream()
                .map(Carte::getColectie)
                .distinct()
                .collect(Collectors.toList());
        return colectiiUnice;
    }
}