import java.util.*;

public class Postier {

    public static void lancer(Graphe g) {
        System.out.println("\n--- Lancement de l'algorithme du Postier Chinois Orienté ---");

        // --- ÉTAPE 1 : IDENTIFIER LES DÉSÉQUILIBRES ---
        Map<Sommet, Integer> differences = calculerDifferencesDegres(g);
        List<Sommet> sources = new ArrayList<>();
        List<Sommet> puits = new ArrayList<>();
        for (Map.Entry<Sommet, Integer> entry : differences.entrySet()) {
            if (entry.getValue() > 0) sources.add(entry.getKey());
            else if (entry.getValue() < 0) puits.add(entry.getKey());
        }
        System.out.println("--> " + sources.size() + " sources et " + puits.size() + " puits trouvés.");

        // --- ÉTAPE 2 : CALCULER LES CHEMINS DE RÉPARATION POSSIBLES ---
        System.out.println("--> Calcul des plus courts chemins des sources vers les puits...");
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matriceDistances = calculerMatriceDistances(g, sources, puits);

        // --- ÉTAPE 3 : DÉCIDER QUELS CHEMINS UTILISER (HEURISTIQUE GLOUTONNE) ---
        System.out.println("--> Recherche des chemins de réparation à ajouter (heuristique gloutonne)...");
        List<Itineraire.Dijkstra> cheminsAReparer = trouverReparationsGloutonnes(differences, matriceDistances);

        // --- ÉTAPE 4 : APPLIQUER LES RÉPARATIONS ---
        Graphe grapheRepare = new Graphe(g); // On travaille sur une copie
        int coutReparation = 0;
        System.out.println("--> Application des réparations...");
        for (Itineraire.Dijkstra chemin : cheminsAReparer) {
            System.out.println("    - Ajout du chemin : " + chemin.getChemin());
            coutReparation += chemin.getDistance();
            dupliquerChemin(grapheRepare, chemin);
        }
        System.out.println("--> Coût total de la réparation : " + coutReparation);

        // --- ÉTAPE 5 : LANCER HIERHOLZER ET AFFICHER LE RÉSULTAT ---
        System.out.println("\n--> Le graphe est maintenant équilibré. Lancement de Hierholzer...");
        Hierholzer.cycle(grapheRepare, true);

        int distanceOriginale = calculerDistanceTotale(g);
        System.out.println("--> Distance totale de la tournée : " + (distanceOriginale + coutReparation));
    }

    private static Map<Sommet, Integer> calculerDifferencesDegres(Graphe g) {
        Map<Sommet, Integer> differences = new HashMap<>();
        for (Sommet s : g.get_Sommets()) {
            int sortant = s.aretes.size();
            int entrant = 0;
            for (Sommet autre : g.get_Sommets()) {
                for (Arete a : autre.aretes) {
                    if (a.destination.equals(s)) {
                        entrant++;
                    }
                }
            }
            if (sortant != entrant) {
                differences.put(s, sortant - entrant);
            }
        }
        return differences;
    }

    private static Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> calculerMatriceDistances(Graphe g, List<Sommet> sources, List<Sommet> puits) {
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matrice = new HashMap<>();
        for (Sommet source : sources) {
            matrice.put(source, new HashMap<>());
            for (Sommet puit : puits) {
                matrice.get(source).put(puit, Itineraire.trouver_chemin(g, source, puit));
            }
        }
        return matrice;
    }

    private static List<Itineraire.Dijkstra> trouverReparationsGloutonnes(Map<Sommet, Integer> differences, Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matrice) {
        List<Itineraire.Dijkstra> reparations = new ArrayList<>();

        while (true) {
            Itineraire.Dijkstra meilleureReparation = null;
            int distanceMin = Integer.MAX_VALUE;

            // On cherche la paire (source, puit) la plus proche parmi celles qui ont encore besoin de réparation
            for (Sommet source : differences.keySet()) {
                if (differences.get(source) <= 0) continue; // Ce n'est plus une source active
                for (Sommet puit : differences.keySet()) {
                    if (differences.get(puit) >= 0) continue; // Ce n'est plus un puit actif

                    int distance = matrice.get(source).get(puit).getDistance();
                    if (distance < distanceMin) {
                        distanceMin = distance;
                        meilleureReparation = matrice.get(source).get(puit);
                    }
                }
            }

            if (meilleureReparation == null) {
                break; // Plus aucune réparation possible, on a fini
            }

            reparations.add(meilleureReparation);

            // On met à jour les différences
            Sommet sourceChoisie = meilleureReparation.getChemin().get(0);
            Sommet puitChoisi = meilleureReparation.getChemin().get(meilleureReparation.getChemin().size() - 1);

            differences.put(sourceChoisie, differences.get(sourceChoisie) - 1);
            differences.put(puitChoisi, differences.get(puitChoisi) + 1);
        }
        return reparations;
    }

    private static void dupliquerChemin(Graphe graphe, Itineraire.Dijkstra chemin) {
        List<Sommet> cheminADupliquer = chemin.getChemin();
        for (int i = 0; i < cheminADupliquer.size() - 1; i++) {
            Sommet s1 = cheminADupliquer.get(i);
            Sommet s2 = cheminADupliquer.get(i + 1);
            int poids = 0;
            for(Arete a : s1.aretes){ if(a.destination.equals(s2)){ poids = a.poids; break; } }
            graphe.ajouter_Rues(s1.id, s2.id, poids, 2); // Type 2 pour sens unique
        }
    }

    private static int calculerDistanceTotale(Graphe g) {
        int distance = 0;
        for(Sommet s : g.get_Sommets()){
            for(Arete a : s.aretes){
                distance += a.poids;
            }
        }
        // Pour un graphe orienté, on ne divise pas par 2
        return distance;
    }
}