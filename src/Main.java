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
            // 1. Création du MST à partir des fichiers
            MST mst = new MST("ressources/sommets.txt", "ressources/aretes_ho1.txt");

            System.out.println("\n==============================");
            System.out.println("        GRAPHE CHARGÉ         ");
            System.out.println("==============================");
            for (Sommet s : mst.getGraphe().get_Sommets()) {
                System.out.print("Sommet " + s.id + " (contenance=" + s.contenance + ") -> ");
                for (Arete a : s.aretes) {
                    System.out.print(a.destination.id + "(" + a.poids + ") ");
                }
                System.out.println();
            }

            // 2. Calcul ACM (Prim)
            System.out.println("\n==============================");
            System.out.println("   ARBRE COUVRANT MINIMAL     ");
            System.out.println("==============================");
            mst.calculACM();

            // 3. DFS sur l’ACM
            System.out.println("\n==============================");
            System.out.println("         PARCOURS DFS         ");
            System.out.println("==============================");
            mst.parcoursDFS();
            List<Sommet> parcoursDFS = mst.getParcoursDFS();

            System.out.print("Parcours DFS complet : ");
            for (Sommet s : parcoursDFS) System.out.print(s.id + " ");
            System.out.println("\n");

            // 4. Shortcutting
            System.out.println("==============================");
            System.out.println("         SHORTCUTTING         ");
            System.out.println("==============================");
            mst.parcourShortcutting();
            List<Sommet> parcoursShortcut = mst.getParcoursShortcut();

            System.out.print("Chemin simplifié : ");
            for (Sommet s : parcoursShortcut) System.out.print(s.id + " ");
            System.out.println("\n");

            // 5. Découpage en tournées avec capacité max = 10
            System.out.println("==============================");
            System.out.println("   DÉCOUPAGE EN TOURNÉES      ");
            System.out.println("==============================");
            int capaciteMax = 15;
            mst.decouperTournees(capaciteMax);

        } catch (FileNotFoundException e) {
            System.err.println("Erreur : fichier introuvable !");
        }
    }
}




