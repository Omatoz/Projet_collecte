import java.util.*;

public class  Sommet {
    public String id;
    public List<Arete> aretes = new ArrayList<>();
    public int contenance;

    // constructeur
    public Sommet(String id) {
        this.id = id;
    }

    public Sommet(String id, int contenance){
        this.id = id;
        this.contenance = contenance;
        this.aretes = new ArrayList<>();
    }

    public void ajouter_arete(Sommet depart, Sommet destination, int poids, int type) {
        this.aretes.add(new Arete(depart, destination, poids, type));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sommet sommet = (Sommet) o;
        return Objects.equals(id, sommet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }


}