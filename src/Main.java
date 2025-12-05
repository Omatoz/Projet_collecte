import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        try {
            // -----------------------------
            // 1. CHARGEMENT DU GRAPHE
            // -----------------------------
            MST mst = new MST(
                    "ressources/sommets.txt",
                    "ressources/aretes_ho1.txt"
            );

            // -----------------------------
            // 2. CALCUL DE L’ARBRE COUVRANT MINIMAL (PRIM)
            // -----------------------------
            mst.calculACM();

            // -----------------------------
            // 3. DFS SUR L’ACM
            // -----------------------------
            mst.parcoursDFS();
            List<Sommet> parcours = mst.getParcoursDFS();

            // -----------------------------
            // 4. SHORTCUTTING (suppression des retours inutiles)
            // -----------------------------
            List<Sommet> shortcut = shortcutting(parcours);

            System.out.println("=== Parcours après Shortcutting ===");
            for (Sommet s : shortcut) System.out.print(s.id + " ");
            System.out.println("\n====================================");

            // -----------------------------
            // 5. DÉCOUPE PAR CAPACITÉ CAMION
            // -----------------------------
            int capaciteCamion = 15; // exemple
            Map<String, Integer> contenance = chargerContenances();

            List<List<Sommet>> tournees = decouperParCapacite(shortcut, contenance, capaciteCamion);

            // affichage
            System.out.println("\n--- Tournées finales ---");
            int num = 1;
            for (List<Sommet> t : tournees) {
                System.out.print("T" + num++ + " : ");
                for (Sommet s : t) System.out.print(s.id + " ");
                System.out.println();
            }

        } catch (FileNotFoundException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // =====================================================
    // SHORTCUTTING
    // =====================================================
    private static List<Sommet> shortcutting(List<Sommet> parcours) {
        List<Sommet> res = new ArrayList<>();
        Set<Sommet> dejaVu = new HashSet<>();

        for (Sommet s : parcours) {
            if (!dejaVu.contains(s)) {
                res.add(s);
                dejaVu.add(s);
            }
        }

        // retour au dépôt A
        if (!res.get(res.size() - 1).id.equals("A")) {
            res.add(new Sommet("A"));
        }

        return res;
    }

    // =====================================================
    // CHARGEMENT DES CONTENANCES (exemple)
    // =====================================================
    private static Map<String, Integer> chargerContenances() {
        Map<String, Integer> m = new HashMap<>();
        m.put("A", 0); // dépôt

        // Adapter selon ton fichier sommets.txt
        m.put("B", 3);
        m.put("C", 4);
        m.put("D", 2);
        m.put("E", 1);
        m.put("F", 3);
        m.put("G", 2);
        m.put("H", 3);
        m.put("I", 4);
        m.put("J", 5);

        return m;
    }

    // =====================================================
    // DÉCOUPAGE EN TOURNÉES
    // =====================================================
    public static List<List<Sommet>> decouperParCapacite(
            List<Sommet> parcours,
            Map<String, Integer> contenance,
            int capaciteMax) {

        List<List<Sommet>> tournees = new ArrayList<>();
        List<Sommet> tour = new ArrayList<>();
        int charge = 0;

        for (Sommet s : parcours) {
            int c = contenance.getOrDefault(s.id, 0);

            // si on dépasse la capacité → on ferme la tournée
            if (charge + c > capaciteMax) {
                tour.add(new Sommet("A")); // retour dépôt
                tournees.add(new ArrayList<>(tour));

                tour.clear();
                charge = 0;
            }

            tour.add(s);
            charge += c;
        }

        // dernière tournée
        if (!tour.isEmpty()) {
            if (!tour.get(tour.size() - 1).id.equals("A")) {
                tour.add(new Sommet("A"));
            }
            tournees.add(tour);
        }

        return tournees;
    }
}



