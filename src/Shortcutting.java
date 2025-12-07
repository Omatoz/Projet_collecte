import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Shortcutting {
    public static List<Sommet> shortcut(List<Sommet> cheminDFS) {
        List<Sommet> chemin = new ArrayList<>();
        Set<Sommet> sommetsMarques = new HashSet<>();

        for (Sommet sommet : cheminDFS) {
            if (!sommetsMarques.contains(sommet)) {
                chemin.add(sommet);
                sommetsMarques.add(sommet);
            }
        }
        //on ajoute le sommet de depart à la fin du chemin pour revenir au centre de traitement
        if(!chemin.isEmpty()){
            chemin.add(chemin.get(0));
        }
        return chemin;
    }
}






