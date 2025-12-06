/*public class Main {
    public static void main(String[] args) {
        Menu m = new Menu(); // cree instance de notre classe qui gère l'interface utilisateur
        m.lancer();
    }
}
*/

import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            // === 1. Création MST avec fichiers ===
            MST mst = new MST("ressources/sommets.txt", "ressources/aretes_ho1.txt");

            System.out.println("\n==============================");
            System.out.println("        GRAPHE CHARGÉ         ");
            System.out.println("==============================");

            for (Sommet s : mst.getGraphe().get_Sommets()) {
                System.out.print("Sommet " + s.id + " -> ");
                for (Arete a : s.aretes) {
                    System.out.print(a.destination.id + "(" + a.poids + ") ");
                }
                System.out.println();
            }

            // === 2. Calcul ACM (Prim) ===
            System.out.println("\n==============================");
            System.out.println("   ARBRE COUVRANT MINIMAL     ");
            System.out.println("==============================");
            mst.calculACM();

            // === 3. DFS sur l’ACM ===
            System.out.println("\n==============================");
            System.out.println("         PARCOURS DFS         ");
            System.out.println("==============================");
            mst.parcoursDFS();

            List<Sommet> parcours = mst.getParcoursDFS();
            System.out.print("Parcours DFS complet : ");
            for (Sommet s : parcours) System.out.print(s.id + " ");
            System.out.println("\n");

            // === 4. Shortcutting ===
            System.out.println("==============================");
            System.out.println("         SHORTCUTTING         ");
            System.out.println("==============================");
            mst.parcourShortcutting();

            List<Sommet> compresse = mst.getParcoursShortcut();
            System.out.print("Chemin simplifié : ");
            for (Sommet s : compresse) System.out.print(s.id + " ");
            System.out.println("\n");

            // === 5. Exemple Dijkstra (facultatif, A → J) ===
            System.out.println("==============================");
            System.out.println("   DIJKSTRA (exemple A → J)   ");
            System.out.println("==============================");

            Sommet A = mst.getGraphe().getSommet("A");
            Sommet J = mst.getGraphe().getSommet("J");

            Itineraire.Dijkstra resultat = Itineraire.trouver_chemin(mst.getGraphe(), A, J);

            System.out.println("Distance : " + resultat.getDistance());
            System.out.print("Chemin : ");
            for (Sommet s : resultat.getChemin()) System.out.print(s.id + " ");
            System.out.println();

        } catch (FileNotFoundException e) {
            System.err.println("Erreur : fichier introuvable !");
        }
    }
}



