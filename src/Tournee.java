import java.util.*;

public class Tournee {

    public static class TSP {
        private List<Sommet> ordre;
        private int distance_totale;
        private List<Sommet> chemin_final;
        private boolean reussite; // si la tournée a réussi

        // constructeur
        public TSP(List<Sommet> ordre, int distance_totale, List<Sommet> chemin_final, boolean reussite) {
            this.ordre = ordre;
            this.distance_totale = distance_totale;
            this.chemin_final = chemin_final;
            this.reussite = reussite;
        }

        // getters
        public List<Sommet> getOrdre() {
            return ordre;
        }
        public int getDistance_totale() {
            return distance_totale;
        }
        public List<Sommet> getChemin_final() {
            return chemin_final;
        }
        public boolean reussite() {
            return reussite;
        }
    }

    public static String format(List<Sommet> chemin) {
        if (chemin == null || chemin.isEmpty()) return "(vide)";

        StringJoiner sj = new StringJoiner(" -> ");
        for (Sommet s : chemin) {
            sj.add(s.id);
        }
        return sj.toString();
    }

    // cas particulier : pas de tournee !
    private static TSP tournee_vide(Sommet depot) {
        System.out.println("Avertissement : Aucun point à visiter fourni.");
        List<Sommet> ordreSimple = List.of(depot, depot);
        return new TSP(ordreSimple, 0, new ArrayList<>(List.of(depot)), true);
    }

    // PHASE 1 :calculs distances DIJKSTRA
    private static Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> calcul_distances(Graphe graphe, List<Sommet> points) {
        // map résultats DIJKSTRA : distances + chemins
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> resultats = new HashMap<>();

        // parcourt chaque point comme départ potentiel
        for (Sommet source : points) {
            resultats.put(source, new HashMap<>());
            // parcourt chaque point comme arrivee potentielle
            for (Sommet destination : points) {
                if (!source.equals(destination)) {
                    // DIJKSTRA
                    Itineraire.Dijkstra resultat = Itineraire.trouver_chemin(graphe, source, destination);
                    // stocke resultats : distance + chemin
                    resultats.get(source).put(destination, resultat);
                    if (resultat.getDistance() == Integer.MAX_VALUE) { // verifie validite distance
                        System.out.printf("Distance de %s à %s = IMPOSSIBLE%n", source.id, destination.id);
                    } else {
                        System.out.printf("Distance de %s à %s = %d%n", source.id, destination.id, resultat.getDistance());
                    }
                }
            }
        }
        return resultats;
    }

    // PHASE 2 : plus proche voisin
    private static List<Sommet> algo_plus_proche_voisin(Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> resultats, Sommet depot, List<Sommet> a_visiter) {
        List<Sommet> ordre_final = new ArrayList<>(); // ordre de visite final
        List<Sommet> restants = new ArrayList<>(a_visiter); // non visités

        Sommet point_actuel = depot; // depart
        ordre_final.add(depot);
        System.out.println("Départ du " + point_actuel.id);

        // tant que ya des points non visités
        while (!restants.isEmpty()) {
            Sommet prochain = null;
            int distance_min = Integer.MAX_VALUE;

            // cherche voisin
            for (Sommet possible : restants) {
                int distance = resultats.get(point_actuel).get(possible).getDistance(); // recuperation de la matrice des distances
                if (distance < distance_min) { // mise a jour si distance plus courte
                    distance_min = distance;
                    prochain = possible;
                }
            }

            // verification chemin possible
            if (prochain != null && distance_min != Integer.MAX_VALUE) {
                System.out.println("Prochain voisin : " + prochain.id + " (distance: " + distance_min + ")");
                ordre_final.add(prochain);
                restants.remove(prochain);
                point_actuel = prochain;
            } else {
                System.out.println("ERREUR CRITIQUE !!! Impossible de trouver un chemin vers les points restants depuis " + point_actuel.id);
                System.out.println("!!! La tournée ne peut pas être complétée !!!");
                break; // SORTIE de la boucle : éviter affichage infini
            }
        }

        return ordre_final;
    }


    private static TSP construction(Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> resultats, List<Sommet> ordre_final, Sommet depot) {

        // retour depot
        Sommet point_actuel = ordre_final.get(ordre_final.size() - 1);
        Itineraire.Dijkstra retour = resultats.get(point_actuel).get(depot);

        // vérification si retour-dépôt possible
        if (retour.getDistance() == Integer.MAX_VALUE) {
            System.out.println("Retour au " + depot.id + " impossible.");
            return new TSP(ordre_final, -1, null, false);
        }

        // ajout depot
        System.out.println("Retour au " + depot.id + " (distance: " + retour.getDistance() + ")");
        ordre_final.add(depot);


        List<Sommet> cheminComplet = new ArrayList<>();
        int distanceTotale = 0;

        // construction chemin final
        for (int i = 0; i < ordre_final.size() - 1; i++) {
            Sommet source = ordre_final.get(i);
            Sommet dest = ordre_final.get(i + 1);
            Itineraire.Dijkstra segment = resultats.get(source).get(dest);
            distanceTotale += segment.getDistance();

            if (cheminComplet.isEmpty()) {
                cheminComplet.addAll(segment.getChemin());
            }
            else if (!segment.getChemin().isEmpty()) {
                cheminComplet.addAll(segment.getChemin().subList(1, segment.getChemin().size()));
            }
        }

        return new TSP(ordre_final, distanceTotale, cheminComplet, true);
    }

    // calcul de la tournée
    public static TSP calculer_tournee(Graphe graphe, Sommet depot, List<Sommet> a_visiter) {
        // cas particulier
        if (a_visiter == null || a_visiter.isEmpty()) {
            return tournee_vide(depot);
        }

        System.out.println("\n[PHASE 1] : DIJKSTRA\n");
        List<Sommet> points = new ArrayList<>(a_visiter); // liste points
        points.add(0, depot); // ajout depot
        // stocke résultats DIJKSTRA : distances + chemins

        // Phase 1 : DIJKSTRA
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> resultats = calcul_distances(graphe, points);

        // Phase 2 : plus proche voisin
        System.out.println("\n[PHASE 2] : PLUS PROCHE VOISIN\n");
        List<Sommet> ordre_final = algo_plus_proche_voisin(resultats, depot, a_visiter); // initialisation ordre de visite final

        if (ordre_final == null) {
            return new TSP(a_visiter, -1, null, false);
        }

        // Phase 3 : construction du chemin final + distance totale
        return construction(resultats, ordre_final, depot);
    }
}