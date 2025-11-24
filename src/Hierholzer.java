import java.util.*;
public class Hierholzer {

    public static List<Sommet> trouverCycleEulerien(Graphe graphe, boolean estOriente) {
        if (graphe == null || graphe.get_Sommets().isEmpty()) {
            throw new RuntimeException("Le graphe est vide ou non initialisé.");
        }

        // Création de la copie temporaire (Map d'adjacence)
        Map<Sommet, List<Sommet>> adjacenceTemp = new HashMap<>();
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

    public static void lancer_cas1(Graphe g, boolean estOriente) {
        System.out.println("--> Lancement de l'algorithme de Hierholzer...");
        try {
            List<Sommet> cycle = Hierholzer.trouverCycleEulerien(g, estOriente);

            System.out.println("\n[RÉSULTATS]");
            System.out.println("--> Tournée calculée avec succès !");
            System.out.println("--> Nombre de rues parcourues : " + (cycle.size() - 1));

            StringJoiner sj = new StringJoiner(" -> ");
            for(Sommet s : cycle) { sj.add(s.id); }
            System.out.println("--> Itinéraire du camion : " + sj.toString());

        } catch (Exception e) {
            System.err.println("Une erreur est survenue pendant l'algorithme : " + e.getMessage());
        }
    }

    public static void lancer_cas2(Graphe g, Sommet depot, List<Sommet> sommetsImpairs) {
        Sommet u = sommetsImpairs.get(0);
        Sommet v = sommetsImpairs.get(1);

        System.out.println("--> Réparation du graphe en ajoutant un chemin virtuel entre " + u.id + " et " + v.id + "...");

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
            grapheRepare.ajouter_Rues(s1.id, s2.id, poids, Rues.DOUBLE_SENS_SIMPLE); // On duplique
        }

        System.out.println("--> Le graphe est maintenant eulérien. Lancement de Hierholzer...");
        try {
            // On lance Hierholzer sur le graphe réparé.
            List<Sommet> cycle = trouverCycleEulerien(grapheRepare, false); // false car HO1

            System.out.println("\n[RÉSULTATS]");
            System.out.println("--> Tournée calculée avec succès !");

            // Calcul de la distance totale
            int distanceTotale = 0;
            for(Sommet s : g.get_Sommets()) {
                for(Arete a : s.aretes) {
                    distanceTotale += a.poids;
                }
            }
            distanceTotale = distanceTotale / 2; // Car chaque arête est comptée deux fois
            distanceTotale += cheminReparation.getDistance(); // On ajoute le coût de la réparation

            StringJoiner sj = new StringJoiner(" -> ");
            for(Sommet s : cycle) { sj.add(s.id); }

            System.out.println("--> Distance totale (incluant les rues reparcourues) : " + distanceTotale);
            System.out.println("--> Itinéraire du camion : " + sj.toString());

        } catch (Exception e) {
            System.err.println("Une erreur est survenue pendant l'algorithme : " + e.getMessage());
        }
    }
}
