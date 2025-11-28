import java.util.*;

public class  Sommet {
    public  String id;
    public List<Arete> aretes = new ArrayList<>();

    // constructeur
    public Sommet(String id) {
        this.id = id;
    }

    public void ajouter_arete(Sommet destination, int poids, int type) {
        this.aretes.add(new Arete(destination, poids, type))
        ;
    }

    @Override
    public String toString() {
        return id;
    }
}