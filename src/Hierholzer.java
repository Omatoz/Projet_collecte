import java.util.*;
public class Hierholzer { // Déclaration de la classe

    public static List<Sommet> trouverCycleEulerien(Graphe graphe, boolean estOriente) { // Méthode statique qui renvoie le chemin final
        // Vérification de l'existence du graphe
        if (graphe == null || graphe.get_Sommets().isEmpty()) {
            throw new RuntimeException("Le graphe est vide ou non initialisé.");
        }

        // Création de la copie temporaire du graphe car algorithme de Hierholzer "destructif" (Map d'adjacence)
        Map<Sommet, List<Sommet>> adjacenceTemp = new HashMap<>(); // Création d'un dictionnaire qui associe les sommet à la liste de ses voisins
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

    public static void cycle(Graphe g, boolean estOriente) {
        System.out.println("[HIERHOLZER]");
        try {
            List<Sommet> cycle = Hierholzer.trouverCycleEulerien(g, estOriente);

            System.out.println("\n[RÉSULTATS]");
            System.out.println("Tournée calculée avec succès !");
            System.out.println("Nombre de rues parcourues : " + (cycle.size() - 1));

            StringJoiner sj = new StringJoiner(" -> ");
            for(Sommet s : cycle) {
                sj.add(s.id);
            }
            System.out.println("Itinéraire du camion : " + sj.toString());

        } catch (Exception e) {
            System.err.println("Une erreur est survenue pendant l'algorithme : " + e.getMessage());
        }
    }

    public static void chemin(Graphe g, Sommet depot, List<Sommet> sommetsImpairs) {
        Sommet u = sommetsImpairs.get(0);
        Sommet v = sommetsImpairs.get(1);

        System.out.println("Réparation du graphe en ajoutant un chemin virtuel entre " + u.id + " et " + v.id + "...");

        // On utilise Dijkstra pour trouver le plus court chemin pour "réparer" le graphe.
        Itineraire.Dijkstra cheminReparation = Itineraire.trouver_chemin(g, u, v);

        // On crée une copie du graphe pour y ajouter les arêtes dupliquées.
        Graphe grapheRepare = new Graphe(g);
        for (int i = 0; i < cheminReparation.getChemin().size() - 1; i++) {
            Sommet s1 = cheminReparation.getChemin().get(i);
            Sommet s2 = cheminReparation.getChemin().get(i+1);
            // On cherche le poids de l'arête originale pour la dupliquer
            int poids = 0;
            for(Arete a : s1.aretes){
                if(a.destination.equals(s2)){
                    poids = a.poids;
                    break;
                }
            }
            grapheRepare.ajouter_Rues(s1.id, s2.id, poids, 1); // On duplique
        }
        cycle(grapheRepare, false);
    }

    public static void cheminMixte(Graphe g, Sommet depot, List<Sommet> sommetsProbleme) {
        Sommet u = sommetsProbleme.get(0);
        Sommet v = sommetsProbleme.get(1);

        System.out.println("Réparation du graphe mixte en dupliquant le plus court chemin entre " + u.id + " et " + v.id + "...");

        Itineraire.Dijkstra cheminReparation = Itineraire.trouver_chemin(g, u, v);
        if (cheminReparation.getDistance() == Integer.MAX_VALUE) { /* Erreur */ return; }

        System.out.println("Chemin de réparation trouvé : " + cheminReparation.getChemin() + " (coût: " + cheminReparation.getDistance() + ")");

        Graphe grapheRepare = new Graphe(g);
        List<Sommet> cheminADupliquer = cheminReparation.getChemin();
        for (int i = 0; i < cheminADupliquer.size() - 1; i++) {
            Sommet s1 = cheminADupliquer.get(i);
            Sommet s2 = cheminADupliquer.get(i + 1);
            int poids = 0;
            for (Arete a : s1.aretes) { if (a.destination.equals(s2)) { poids = a.poids; break; } }

            // On duplique avec des arcs orientés car le graphe final sera traité comme orienté
            grapheRepare.ajouter_Arc(s1.id, s2.id, poids, 2);
        }

        System.out.println("Le graphe est maintenant équilibré. Lancement de Hierholzer en mode orienté...");
        cycle(grapheRepare, true); // On lance en mode ORIENTÉ
    }
}