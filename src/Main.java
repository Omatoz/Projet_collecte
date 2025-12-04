import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String fichier = "ressources/aretes_ho1.txt";

        // --- Lecture du fichier et construction du graphe ---
        Map<String, Sommet> mapSommets = new HashMap<>();
        List<Sommet> listeSommets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne = br.readLine(); // sauter l'en-tête

            while ((ligne = br.readLine()) != null) {
                String[] t = ligne.split(";");

                String idSrc = t[0];
                String idDst = t[1];
                int poids = Integer.parseInt(t[2]);
                int type = Integer.parseInt(t[3]);

                Sommet s1 = mapSommets.computeIfAbsent(idSrc, Sommet::new);
                Sommet s2 = mapSommets.computeIfAbsent(idDst, Sommet::new);

                // ajouter les arêtes dans les deux sens
                s1.ajouter_arete(s1, s2, poids, type);
                s2.ajouter_arete(s2, s1, poids, type);
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture fichier : " + e.getMessage());
            return;
        }

        listeSommets.addAll(mapSommets.values());

        // --- Préparer DFS ---
        int nbSommets = listeSommets.size();
        DFS dfs = new DFS(nbSommets);

        // construire la table des adjacences pour DFS (indices)
        Map<String, Integer> indiceSommets = new HashMap<>();
        for (int i = 0; i < nbSommets; i++) {
            indiceSommets.put(listeSommets.get(i).id, i);
        }

        for (Sommet s : listeSommets) {
            int i = indiceSommets.get(s.id);
            for (Arete a : s.aretes) {
                int j = indiceSommets.get(a.destination.id);
                dfs.ajouterArete(i, j);
            }
        }

        // --- Lancer DFS ---
        dfs.dfs(0); // partir du sommet d'indice 0 (A)

        // --- Afficher le parcours complet avec retours ---
        System.out.println("=== Parcours DFS complet avec retours ===");
        for (int idx : dfs.getParcoursComplet()) {
            System.out.print(listeSommets.get(idx).id + " ");
        }
        System.out.println("\n========================================");

        // --- Appliquer shortcutting ---
        List<Sommet> cheminShortcut = Shortcutting.shortcut(
                convertIndicesToSommets(dfs.getParcoursComplet(), listeSommets),
                listeSommets
        );

        // --- Afficher le chemin après shortcutting ---
        System.out.println("=== Parcours après Shortcutting ===");
        for (Sommet s : cheminShortcut) {
            System.out.print(s.id + " ");
        }
        System.out.println("\n==================================");
    }

    // conversion d'une liste d'indices DFS en liste de sommets
    private static List<Sommet> convertIndicesToSommets(List<Integer> indices, List<Sommet> listeSommets) {
        List<Sommet> result = new ArrayList<>();
        for (int idx : indices) {
            result.add(listeSommets.get(idx));
        }
        return result;
    }
}



