import java.util.*;

public class Hierholzer {

    // Classe interne pour manipuler une copie légère des arêtes
    private static class CopieArete {
        Sommet destination;
        int type;
        CopieArete(Sommet dest, int t) { this.destination = dest; this.type = t; }

        @Override
        public String toString() { return destination.id + "(t" + type + ")"; }
    }

    public static List<Sommet> trouverCycleEulerien(Graphe graphe, boolean estOriente) {
        if (graphe == null || graphe.get_Sommets().isEmpty()) {
            throw new RuntimeException("Le graphe est vide ou non initialisé.");
        }

        // 1. COPIE DES ARÊTES (Map temporaire)
        Map<String, List<CopieArete>> adjacenceTemp = new HashMap<>();

        // On initialise la map avec les IDs pour être sûr
        for (Sommet s : graphe.get_Sommets()) {
            adjacenceTemp.put(s.id, new ArrayList<>());
        }

        // Remplissage avec toutes les arêtes
        for (Sommet s : graphe.get_Sommets()) {
            for (Arete a : s.aretes) {
                adjacenceTemp.get(s.id).add(new CopieArete(a.destination, a.type));
            }
        }

        // 2. ALGORITHME
        Stack<Sommet> pile = new Stack<>();
        List<Sommet> cycle = new ArrayList<>();

        // Départ arbitraire (Sommet A si possible, sinon le premier)
        Sommet depart = graphe.getSommet("A");
        if (depart == null) depart = graphe.get_Sommets().iterator().next();

        pile.push(depart);

        while (!pile.isEmpty()) {
            Sommet u = pile.peek();
            List<CopieArete> voisinsDeU = adjacenceTemp.get(u.id);

            if (voisinsDeU != null && !voisinsDeU.isEmpty()) {
                // On choisit une arête sortante (la première dispo)
                CopieArete areteChoisie = voisinsDeU.remove(0);
                Sommet v = areteChoisie.destination;

                pile.push(v);

                // --- GESTION SPÉCIALE GRAPHE MIXTE ---
                // Si rue Type 1 (Double sens non-orienté) : On a emprunté U->V,
                // on doit supprimer virtuellement le retour V->U pour ne pas le reprendre immédiatement.
                if (areteChoisie.type == 1) {
                    List<CopieArete> voisinsDeV = adjacenceTemp.get(v.id);
                    if (voisinsDeV != null) {
                        for (int i = 0; i < voisinsDeV.size(); i++) {
                            // Comparaison stricte sur les IDs (Strings)
                            if (voisinsDeV.get(i).destination.id.equals(u.id) && voisinsDeV.get(i).type == 1) {
                                voisinsDeV.remove(i);
                                break; // On ne supprime qu'une seule instance de l'arête
                            }
                        }
                    }
                }
                // Si Type 3 (Double sens orienté) : On NE SUPPRIME PAS l'inverse.
                // On a fait C->G, mais G->C reste valide et nécessaire plus tard.

            } else {
                // Plus de voisins, on dépile vers le cycle final
                cycle.add(pile.pop());
            }
        }

        Collections.reverse(cycle);

        // Vérification de sécurité
        if (cycle.size() > 1 && !cycle.get(0).id.equals(cycle.get(cycle.size()-1).id)) {
            System.err.println("ATTENTION : Le chemin trouvé n'est pas un cycle fermé !");
            System.err.println("Début: " + cycle.get(0).id + ", Fin: " + cycle.get(cycle.size()-1).id);
        }

        return cycle;
    }

    // Affiche le cycle complet
    public static void cycle(Graphe g, boolean estOriente) {
        try {
            List<Sommet> cycle = Hierholzer.trouverCycleEulerien(g, estOriente);

            System.out.println("\n[RÉSULTATS]");
            System.out.println("Tournée calculée avec succès !");
            System.out.println("Nombre de rues parcourues : " + (cycle.size() - 1));

            StringJoiner sj = new StringJoiner(" -> ");
            for (Sommet s : cycle) {
                sj.add(s.id);
            }
            System.out.println("Itinéraire du camion : " + sj.toString());

        } catch (Exception e) {
            System.err.println("Erreur dans Hierholzer : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour le Cas 2 (Chemin Eulérien avec 2 sommets impairs)
    public static void chemin(Graphe g, Sommet depot, List<Sommet> sommetsImpairs) {
        if (sommetsImpairs == null || sommetsImpairs.size() != 2) {
            System.err.println("Erreur interne : la méthode 'chemin' a été appelée sans avoir exactement 2 sommets impairs.");
            return;
        }

        Sommet u = sommetsImpairs.get(0);
        Sommet v = sommetsImpairs.get(1);

        System.out.println("--> Réparation du graphe en dupliquant le plus court chemin entre " + u.id + " et " + v.id + "...");

        // On utilise Dijkstra pour trouver le plus court chemin entre les deux impairs
        Itineraire.Dijkstra cheminReparation = Itineraire.trouver_chemin(g, u, v);

        if (cheminReparation.getDistance() == Integer.MAX_VALUE) {
            System.err.println("ERREUR : Les sommets impairs sont inaccessibles l'un depuis l'autre. Réparation impossible.");
            return;
        }

        System.out.println("Chemin de réparation trouvé (pour fermer le cycle) : ");
        StringJoiner sj = new StringJoiner(" -> ");
        for(Sommet s : cheminReparation.getChemin()) sj.add(s.id);
        System.out.println(sj.toString() + " (coût: " + cheminReparation.getDistance() + ")");

        // On travaille sur une copie ou on modifie le graphe (ici on suppose modification directe ou copie préalable)
        // Note: Dans votre architecture, assurez-vous de passer une COPIE de graphe à cette méthode si vous voulez préserver l'original.
        // Ici, on ajoute les arêtes virtuelles pour rendre le graphe pair.

        List<Sommet> cheminADupliquer = cheminReparation.getChemin();
        for (int i = 0; i < cheminADupliquer.size() - 1; i++) {
            Sommet s1 = cheminADupliquer.get(i);
            Sommet s2 = cheminADupliquer.get(i + 1);

            // Retrouver le poids et le type de l'arête existante pour la dupliquer
            int poids = 0;
            int type = 1; // Par défaut

            for (Arete a : s1.aretes) {
                if (a.destination.id.equals(s2.id)) { // Comparaison par ID ici aussi par sécurité
                    poids = a.poids;
                    type = a.type; // On garde le même type que la rue originale
                    break;
                }
            }

            // On ajoute l'arête "virtuelle" qui permet de repasser par ce chemin
            // Si c'est type 1 (non orienté), ajouter_arete doit être fait dans les deux sens si votre classe Graphe ne le gère pas auto.
            // Ici on suppose que g.ajouter_Arc gère la bidirectionnalité si type=1
            g.ajouter_Arc(s1.id, s2.id, poids, type);
            // Note: Si vous n'avez pas la méthode ajouter_Arc, utilisez s1.ajouter_arete(...) directement
        }

        System.out.println("--> Le graphe est maintenant eulérien (tous pairs). Lancement de Hierholzer...");

        // On lance la méthode de cycle sur le graphe modifié
        cycle(g, false);

        // Optionnel : Affichage du coût total estimé
        // Attention : Le coût affiché par 'cycle' est le nombre de rues, pas la distance kilométrique.
    }
}