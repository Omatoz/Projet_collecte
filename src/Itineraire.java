import java.util.*;

public class Itineraire {

    public static class Dijkstra {
        private int distance;
        private List<Sommet> chemin; // liste des sommets

        // Constructeur
        public Dijkstra(int distance, List<Sommet> chemin) {
            this.distance = distance;
            this.chemin = chemin;
        }

        // getters
        public int getDistance() {
            return distance;
        }
        public List<Sommet> getChemin() {
            return chemin;
        }
    }

    // Calcule le plus court chemin entre un sommet de départ et d'arrivée dans un graphe
    public static Dijkstra trouver_chemin(Graphe graphe, Sommet depart, Sommet arrivee) {
        Map<Sommet, Integer> distances = new HashMap<>();
        Map<Sommet, Sommet> predecesseurs = new HashMap<>();

        // Définit priorité file d'attente
        Comparator<Sommet> comparateur_distance = new Comparator<Sommet>() {
            @Override
            public int compare(Sommet s1, Sommet s2) {
                // Comparaison entre les sommets : distance stockée dans la map distances
                return Integer.compare(distances.get(s1), distances.get(s2)); // Celui avec la plus petite distance est le plus prioritaire.
            }
        };

        // Instanciation file priorité : stocke les sommets à visiter
        PriorityQueue<Sommet> file = new PriorityQueue<>(comparateur_distance);

        for (Sommet s : graphe.get_Sommets()) {
            distances.put(s, Integer.MAX_VALUE);
        }

        distances.put(depart, 0); // départ a 0
        file.add(depart); // on ajoute le point de départ a la file

        while (!file.isEmpty()) {
            Sommet sommet_actuel = file.poll();
            if (sommet_actuel.equals(arrivee)) break;

            // exploration des arêtes environnantes / voisins
            for (Arete arete : sommet_actuel.aretes) {
                Sommet voisin = arete.destination;
                int distance_arete = distances.get(sommet_actuel) + arete.poids;

                // Si nouveau chemin plus court alors on actualise la distance
                if (distance_arete < distances.get(voisin)) {
                    distances.put(voisin, distance_arete);
                    predecesseurs.put(voisin, sommet_actuel);
                    file.add(voisin); // ajout liste
                }
            }
        }

        List<Sommet> chemin = new ArrayList<>();
        if (distances.get(arrivee) == Integer.MAX_VALUE) {
            return new Dijkstra(Integer.MAX_VALUE, Collections.emptyList());
        }

        // On part de l'arrivée et on remonte le chemin grâce aux predecesseurs
        Sommet etape = arrivee;
        while (etape != null) {
            chemin.add(etape);
            etape = predecesseurs.get(etape); // sommet précédent
        }
        Collections.reverse(chemin);
        // retourne nouvelle instance de la classe
        return new Dijkstra(distances.get(arrivee), chemin);
    }
}