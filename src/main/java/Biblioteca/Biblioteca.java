package Biblioteca;

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

    // Mai poți adăuga și alte metode pentru filtrarea și gestiunea cărților.
}