import java.util.*;

public class Hierholzer {
    public static List<String> trouverCycleEulerien(Graphe graphe) {
        Eulerien e = new Eulerien();

        // 1. Appel de la vérification (qui est maintenant dans la même classe)
        if (!e.verifierConditionEulerienne(graphe)) {
            // On retourne null ou une liste vide, ou on lance une exception selon ton choix
            System.err.println("Le graphe ne permet pas de cycle eulérien.");
            return new ArrayList<>();
        }

        // 2. Création de la copie temporaire (Map d'adjacence)
        Map<String, List<String>> adjacenceTemp = new HashMap<>();

        for (Sommet s : graphe.get_Sommets()) {
            List<String> voisins = new ArrayList<>();
            for (Arete a : s.aretes) {
                voisins.add(a.getDestination().id);
            }
            adjacenceTemp.put(s.id, voisins);
        }

        // Initialisation
        Stack<String> pile = new Stack<>();
        List<String> cycle = new ArrayList<>();

        if (adjacenceTemp.isEmpty()) return cycle;

        String depart = adjacenceTemp.keySet().iterator().next();
        pile.push(depart);

        // Boucle principale
        while (!pile.isEmpty()) {
            String u = pile.peek();
            List<String> voisinsDeU = adjacenceTemp.get(u);

            if (voisinsDeU != null && !voisinsDeU.isEmpty()) {
                String v = voisinsDeU.get(0);

                // Suppression de l'arête (u, v) et (v, u)
                voisinsDeU.remove(v);
                List<String> voisinsDeV = adjacenceTemp.get(v);
                if (voisinsDeV != null) {
                    voisinsDeV.remove(u);
                }

                pile.push(v);
            } else {
                cycle.add(pile.pop());
            }
        }

        Collections.reverse(cycle);
        return cycle;
    }
}
}
