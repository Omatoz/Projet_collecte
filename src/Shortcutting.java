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













    /*public void shortcutRecursif(int sommetDepart, boolean[] cheminShortcutting, List<Integer> chemin){
        if(!cheminShortcutting[sommetDepart]){  //verfie si deja ajouté à liste
            cheminShortcutting[sommetDepart] = true;  //on parcours seulement si pas encore ajouté
            chemin.add(sommetDepart);   //on ajoute
        }
        for(int sucesseur : sommetsAdjacents.get(sommetDepart)){   //on parcours les successeurs du sommet
            if(!cheminShortcutting[sucesseur]){   //verifie si pas encore parcouru
                shortcutRecursif(sucesseur, cheminShortcutting, chemin);  //recursivité
            }
        }
    }


    public List<Integer> shortcutting(int sommetDepart){
        List<Integer> chemin = new ArrayList<>();  //on stocke sommets de parcours du chemin
        boolean[] cheminShortcutting = new boolean[marque.length];    //on marque les sommets déjà parcouru
        shortcutRecursif(sommetDepart, cheminShortcutting, chemin);

        return chemin;
    }
    */




