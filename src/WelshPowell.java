import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class WelshPowell {
    private Graphe g;
    private ArrayList<Sommet> SommetsTries = new ArrayList<>();
    private Map<Sommet, Integer> couleur = new HashMap<>();
    private Map<Sommet, Boolean> colore = new HashMap<>();
    private int nbCouleur = 0;

    public WelshPowell(Graphe g) {
        this.g = g;
    }

    private boolean existeSommetNonColore () {
        for (Boolean b : colore.values()) {
            if (!b) {
                return true;
            }
        }
        return false;
    }

    public int getNbCouleurs() {
        return nbCouleur - 1;
    }

    public void afficherCouleurs(){
        for (Sommet s : g.get_Sommets()){
            System.out.println("Sommet: " + s.id + "est de couleur " + couleur.get(s));
        }
    }

    // Méthode de coloration
    public void coloration () {
        SommetsTries.addAll(g.get_Sommets());
        // Copie des sommets du graphe
        SommetsTries.addAll(g.get_Sommets());
        // On tri les degrés des sommets par décroissance
        Collections.sort(SommetsTries, new Comparator<Sommet>() {
            @Override
            public int compare(Sommet s1, Sommet s2) {

                // degré = nombre d'arêtes sortantes
                int deg1 = s1.aretes.size();
                int deg2 = s2.aretes.size();

                // On trie par ordre décroissant les degrés des sommets
                return Integer.compare(deg2, deg1);
            }
        });
        // Initalisation des sommets en partant du principe qu'ils ne sont pas colorés
        for (Sommet s : SommetsTries) {
            colore.put(s, false);
            couleur.put(s, 0);
        }

        nbCouleur = 1;

        // Boucle principale pour la coloration

        while (existeSommetNonColore()) {
             for(Sommet s : new ArrayList<>(SommetsTries)) {
                 if (!colore.get(s)){
                     boolean conflit = false;

                     // Vérification des voisins
                     for (Arete a : s.aretes){
                         Sommet voisin = a.destination;

                         if (colore.get(voisin) && couleur.get(voisin) ==  nbCouleur){
                             conflit = true ;
                             break;
                         }
                     }

                     // Si pas de conflit d'adjacence de sommet on colorie le sommet sur leqeul l'algorithme passe
                     if (!conflit){
                         couleur.put(s, nbCouleur);
                         colore.put(s, true);
                     }
                 }
             }
             nbCouleur++;
        }

    }
}
