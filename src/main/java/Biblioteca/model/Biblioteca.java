package Biblioteca.model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                writer.write(carte.getTitlu() + ";" + carte.getAutor() + ";" + carte.getColectie() + ";" + carte.getDataAchizitie() + ";" + carte.isImprumutata());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incarcaDinFisier(String numeFisier) {
        File fisier = new File(numeFisier);

        // Verificăm dacă fișierul există înainte de a încerca să îl citim
        if (!fisier.exists()) {
            System.out.println("Fișierul " + numeFisier + " nu există. Se va crea automat la salvare.");
            return;
        }

        carti.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fisier))) {
            String linie;
            while ((linie = reader.readLine()) != null) {
                String[] campuri = linie.split(";");
                if (campuri.length == 5) {
                    String titlu = campuri[0];
                    String autor = campuri[1];
                    String colectie = campuri[2];
                    LocalDate dataImprumut = LocalDate.parse(campuri[3]);
                    boolean imprumutata = Boolean.parseBoolean(campuri[4]);

                    Carte carte = new Carte(titlu, autor, colectie, dataImprumut, imprumutata);
                    carti.add(carte);
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
                writer.write(carte.getTitlu() + " - " + carte.getColectie());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generarea unui raport pentru cărțile dintr-o anumită colecție
    public void genereazaRaportColectie(String colectie, String numeFisier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(numeFisier))) {
            for (Carte carte : cautaCartiDupaColectie(colectie)) {
                writer.write(carte.getTitlu() + " - " + carte.getAutor());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Mai poți adăuga și alte metode pentru filtrarea și gestiunea cărților.
}