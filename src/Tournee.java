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

    // calcul de la tournée
    public static TSP calculer_tournee(Graphe graphe, Sommet depot, List<Sommet> a_visiter) {
        if (a_visiter == null || a_visiter.isEmpty()) {
            System.out.println("Avertissement : Aucun point à visiter n'a été fourni. La tournée est Dépôt -> Dépôt.");
            List<Sommet> ordreSimple = List.of(depot, depot);
            return new TSP(ordreSimple, 0, new ArrayList<>(List.of(depot)), true);
        }
        System.out.println("\n[PHASE 1] : DIJKSTRA\n");
        List<Sommet> points = new ArrayList<>(a_visiter); // On liste les dépots et adresses (points)
        points.add(0, depot); // ajout depot
        // On stocke résultats de Dijkstra : distances + chemins
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> resultats = new HashMap<>();

        // parcourt chaque point comme un point de départ potentiel
        for (Sommet source : points) {
            resultats.put(source, new HashMap<>());
            // parcourt chaque point comme un point d'arrivee potentiel
            for (Sommet destination : points) {
                if (!source.equals(destination)) {
                    Itineraire.Dijkstra resultat = Itineraire.trouver_chemin(graphe, source, destination);
                    // On stocke resultats : distance + chemin
                    resultats.get(source).put(destination, resultat);
                    if (resultat.getDistance() == Integer.MAX_VALUE) { // verifie validite distance
                        System.out.printf("-> Distance de %s à %s = IMPOSSIBLE%n", source.id, destination.id);
                    } else {
                        System.out.printf("-> Distance de %s à %s = %d%n", source.id, destination.id, resultat.getDistance());
                    }
                }
            }
        }

        System.out.println("\n[PHASE 2] : PLUS PROCHE VOISIN\n");
        List<Sommet> ordre_final = new ArrayList<>(); // initialisation orddre de visite final
        List<Sommet> restants = new ArrayList<>(a_visiter);

        Sommet point_actuel = depot; // point de depart
        ordre_final.add(depot);
        System.out.println("Départ du " + point_actuel.id);

        // tant qu'il y a encore des adresses dans notre liste
        while (!restants.isEmpty()) {
            Sommet prochain = null;
            int distance_min = Integer.MAX_VALUE;

            // on teste tout les points restants
            for (Sommet possible : restants) {
                int distance = resultats.get(point_actuel).get(possible).getDistance(); // recuperation de la matrice des distances
                if (distance < distance_min) { // mise a jour si distance plus courte
                    distance_min = distance;
                    prochain = possible;
                }
            }

            // On vérifie si on a trouvé un chemin possible
            if (prochain != null && distance_min != Integer.MAX_VALUE) {
                System.out.println("Prochain voisin : " + prochain.id + " (distance: " + distance_min + ")");
                ordre_final.add(prochain);
                restants.remove(prochain);
                point_actuel = prochain;
            } else {
                System.out.println("ERREUR CRITIQUE !!! Impossible de trouver un chemin vers les points restants depuis " + point_actuel.id);
                System.out.println("La tournée ne peut pas être complétée !!!");
                // On retourne un résultat indiquant que la tournée a échoué.
                return new TSP(ordre_final, -1, null, false); // fin méthode !!!
            }
        }

        // tous les points ont été visités alors on gère le trajet retour
        Itineraire.Dijkstra retour = resultats.get(point_actuel).get(depot); // récupèration des infos du trajet retour
        int distance_retour = retour.getDistance();

        // On vérifie si retour-dépôt possible
        if (distance_retour == Integer.MAX_VALUE) {
            System.out.println("Retour au " + depot.id + " (distance: IMPOSSIBLE)");
            System.out.println("ERREUR CRITIQUE !!! La tournée peut être effectuée, mais le retour au dépôt est impossible.");
            return new TSP(ordre_final, -1, null, false);
        }

        System.out.println("Retour au " + depot.id + " (distance: " + distance_retour + ")");
        ordre_final.add(depot);

        // Cette partie ne sera exécutée que si la tournée est possible
        List<Sommet> cheminComplet = new ArrayList<>();
        int distanceTotale = 0;

        for (int i = 0; i < ordre_final.size() - 1; i++) {
            Sommet source = ordre_final.get(i);
            Sommet dest = ordre_final.get(i + 1);
            Itineraire.Dijkstra segment = resultats.get(source).get(dest);
            distanceTotale += segment.getDistance();

            if (cheminComplet.isEmpty()) {
                cheminComplet.addAll(segment.getChemin());
            } else {
                // On ne fait le subList que si le chemin n'est pas vide
                if (!segment.getChemin().isEmpty()) {
                    cheminComplet.addAll(segment.getChemin().subList(1, segment.getChemin().size()));
                }
            }
        }

        return new TSP(ordre_final, distanceTotale, cheminComplet, true);
    }
}