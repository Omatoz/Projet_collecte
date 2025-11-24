import java.util.*;
public class Hierholzer {

    public static List<Sommet> trouverCycleEulerien(Graphe graphe, boolean estOriente) {
        if (graphe == null || graphe.get_Sommets().isEmpty()) {
            throw new RuntimeException("Le graphe est vide ou non initialisé.");
        }

        // Création de la copie temporaire (Map d'adjacence)
        Map<Sommet, List<Sommet>> adjacenceTemp = new HashMap<>();
        for (Sommet s : graphe.get_Sommets()) {
            List<Sommet> voisins = new ArrayList<>();
            for (Arete a : s.aretes) {
                voisins.add(a.destination);
            }
            adjacenceTemp.put(s, voisins);
        }

        // Initialisation
        Stack<Sommet> pile = new Stack<>();
        List<Sommet> cycle = new ArrayList<>();
        Sommet depart = graphe.get_Sommets().iterator().next();
        pile.push(depart);

        // Boucle principale
        while (!pile.isEmpty()) {
            Sommet u = pile.peek();
            List<Sommet> voisinsDeU = adjacenceTemp.get(u);

            if (voisinsDeU != null && !voisinsDeU.isEmpty()) {
                Sommet v = voisinsDeU.get(0);
                pile.push(v);
                voisinsDeU.remove(0); // Suppression de l'arête (u, v) et (v, u)

                // On supprime l'arête inverse si le graphe est non orienté
                if (!estOriente) {
                    List<Sommet> voisinsDeV = adjacenceTemp.get(v);
                    if (voisinsDeV != null) {
                        voisinsDeV.remove(u);
                    }
                }
            } else {
                cycle.add(pile.pop());
            }
        }

        Collections.reverse(cycle);
        return cycle;
    }

    public static void lancer_Hierholzer(Graphe g, boolean estOriente) {
        System.out.println("--> Lancement de l'algorithme de Hierholzer...");
        try {
            List<Sommet> cycle = Hierholzer.trouverCycleEulerien(g, estOriente);

            System.out.println("\n[RÉSULTATS]");
            System.out.println("--> Tournée calculée avec succès !");
            System.out.println("--> Nombre de rues parcourues : " + (cycle.size() - 1));

            StringJoiner sj = new StringJoiner(" -> ");
            for(Sommet s : cycle) { sj.add(s.id); }
            System.out.println("--> Itinéraire du camion : " + sj.toString());

        } catch (Exception e) {
            System.err.println("Une erreur est survenue pendant l'algorithme : " + e.getMessage());
        }
    }
}
