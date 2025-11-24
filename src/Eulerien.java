import java.util.*;
public class Eulerien {
    // Vérification du graphe : eulérien ou pas
    public static List<Sommet> Eulerien_non_oriente(Graphe g) {
        List<Sommet> impairs = new ArrayList<>();
        for (Sommet s : g.get_Sommets()) {
            if (s.aretes.size() % 2 != 0) {
                impairs.add(s);
            }
        }
        return impairs;
    }

    public static List<Sommet> Eulerien_oriente(Graphe g) {
        List<Sommet> sommets_non_equilibres = new ArrayList<>();
        Map<Sommet, Integer> degre_Entrant = new HashMap<>();
        for (Sommet s : g.get_Sommets()) {
            degre_Entrant.put(s, 0);
        }
        for (Sommet s : g.get_Sommets()) {
            for (Arete a : s.aretes) {
                Sommet destination = a.destination;
                degre_Entrant.put(destination, degre_Entrant.get(destination) + 1);
            }
        }
        for (Sommet s : g.get_Sommets()) {
            if (s.aretes.size() != degre_Entrant.get(s)) {
                sommets_non_equilibres.add(s);
            }
        }
        return sommets_non_equilibres;
    }
}
