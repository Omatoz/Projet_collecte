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

    // Dans Eulerien.java ou une classe utilitaire
    public static boolean estMixteEulérien(Graphe g) {
        for (Sommet s : g.get_Sommets()) {
            int degreType1 = 0, degreSortantOriente = 0, degreEntrantOriente = 0;

            for (Arete a : s.aretes) {
                if (a.type == 1)
                    degreType1++;
                else if (a.type == 2 || a.type == 3) degreSortantOriente++;
            }

            for (Sommet autre : g.get_Sommets()) {
                for (Arete a : autre.aretes) {
                    if ((a.type == 2 || a.type == 3) && a.destination.equals(s)) degreEntrantOriente++;
                }
            }

            if (degreType1 % 2 != 0 || degreSortantOriente != degreEntrantOriente) {
                return false; // Au moins un sommet ne respecte pas les conditions
            }
        }
        return true; // Tous les sommets respectent les conditions
    }

    public static List<Sommet> trouverSommetsImpairsMixtes(Graphe g) {
        List<Sommet> sommetsProbleme = new ArrayList<>();

        for (Sommet s : g.get_Sommets()) {

            // --- PARTIE 1 : ANALYSE DES RUES "FLEXIBLES" (TYPE 1) ---
            int degreNonOriente = 0;
            for (Arete a : s.aretes) {
                if (a.type == 1) {
                    degreNonOriente++;
                }
            }
            // Le nombre de rues de type 1 connectées à 's' doit être pair.
            boolean estPairNonOriente = (degreNonOriente % 2 == 0);

            // --- PARTIE 2 : ANALYSE DES RUES "RIGIDES" (TYPE 2 et 3) ---
            int degreSortantOriente = 0;
            for (Arete a : s.aretes) {
                if (a.type == 2 || a.type == 3) {
                    degreSortantOriente++;
                }
            }

            int degreEntrantOriente = 0;
            for (Sommet autre : g.get_Sommets()) {
                for (Arete a : autre.aretes) {
                    if (a.destination.equals(s) && (a.type == 2 || a.type == 3)) {
                        degreEntrantOriente++;
                    }
                }
            }
            // Le nombre d'entrées et de sorties rigides doit être égal.
            boolean estEquilibreOriente = (degreSortantOriente == degreEntrantOriente);

            // --- CONCLUSION POUR LE SOMMET 's' ---
            // Le sommet est à problème s'il ne respecte pas L'UNE des deux conditions.
            if (!estPairNonOriente || !estEquilibreOriente) {
                sommetsProbleme.add(s);
                // debug
                /*System.out.println("DEBUG: Sommet " + s.id + " à problème. " +
                        "Équilibre Orienté: " + estEquilibreOriente +
                        " (Entrant: " + degreEntrantOriente + ", Sortant: " + degreSortantOriente + "). " +
                        "Parité Non-Orienté: " + estPairNonOriente +
                        " (Degré: " + degreNonOriente + ").");*/
            }
        }
        return sommetsProbleme;
    }
}