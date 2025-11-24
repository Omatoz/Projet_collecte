import java.util.*;

public class Eulerien {
    // Vérification du graphe : eulérien ou pas
    public static boolean Eulerien_non_oriente(Graphe g) {
        // On récupère la collection de sommets depuis l'objet Graphe
        for (Sommet s : g.
                get_Sommets()) {
            // On regarde le degré de chaque sommet
            if (s.aretes.size() % 2 != 0) {
                System.out.println("Condition Euler non respectée : Le sommet " + s.id
                        + " est de degré impair (" + s.aretes.size() + ").");
                return false;
            }
        }
        return true;
    }

    public static boolean Eulerien_oriente(Graphe g) {
        Map<Sommet, Integer> degre_entrant = new HashMap<>();

        for (Sommet s : g.get_Sommets()) {
            degre_entrant.put(s, 0);
        }

        for (Sommet s : g.get_Sommets()) {
            for (Arete a : s.aretes) {
                Sommet destination = a.destination;
                degre_entrant.put(destination, degre_entrant.get(destination) + 1);
            }
        }

        for (Sommet s : g.get_Sommets()) {
            int sortant = s.aretes.size();
            int entrant = degre_entrant.get(s);
            if (entrant != sortant) {
                System.out.println("Condition Euler non respectée : ÉCHEC. Le sommet " + s.id
                        + " n'est pas équilibré (entrant: " + entrant + ", sortant: " + sortant + ").");
                return false;
            }
        }
        return true;
    }
}
