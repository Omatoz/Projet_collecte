import java.util.*;

public class Hierholzer {


    // Classe interne pour manipuler une copie des arêtes sans casser le graphe original
    private static class CopieArete {
        Sommet destination;
        int type;
        CopieArete(Sommet dest, int t) { this.destination = dest; this.type = t; }

        @Override
        public String toString() { return destination.id; }
    }

    public static List<Sommet> trouverCycleEulerien(Graphe graphe) {
        if (graphe == null || graphe.get_Sommets().isEmpty()) {
            throw new RuntimeException("Le graphe est vide.");
        }

        // 1. COPIE DES ARÊTES (Map avec String pour éviter bugs de référence)
        Map<String, List<CopieArete>> adjacenceTemp = new HashMap<>();

        for (Sommet s : graphe.get_Sommets()) {
            adjacenceTemp.put(s.id, new ArrayList<>());
        }

        // Remplissage
        for (Sommet s : graphe.get_Sommets()) {
            for (Arete a : s.aretes) {
                adjacenceTemp.get(s.id).add(new CopieArete(a.destination, a.type));
            }
        }

        // 2. ALGORITHME DE HIERHOLZER
        Stack<Sommet> pile = new Stack<>();
        List<Sommet> cycle = new ArrayList<>();

        // Point de départ : A si possible, sinon le premier dispo
        Sommet depart = graphe.getSommet("A");
        if (depart == null) depart = graphe.get_Sommets().iterator().next();

        pile.push(depart);

        while (!pile.isEmpty()) {
            Sommet u = pile.peek();
            List<CopieArete> voisinsDeU = adjacenceTemp.get(u.id);

            if (voisinsDeU != null && !voisinsDeU.isEmpty()) {
                // On emprunte la première arête disponible
                CopieArete areteChoisie = voisinsDeU.remove(0);
                Sommet v = areteChoisie.destination;

                pile.push(v);

                // --- GESTION GRAPHES MIXTES (HO3) ---

                // CAS 1 : Rue à double sens non-orientée (Type 1)
                // Si je passe de U à V, je "consomme" la rue entière.
                // Je ne peux pas revenir tout de suite de V à U par cette même rue.
                // -> Je dois supprimer l'arête inverse V->U.
                if (areteChoisie.type == 1) {
                    List<CopieArete> voisinsDeV = adjacenceTemp.get(v.id);
                    if (voisinsDeV != null) {
                        for (int i = 0; i < voisinsDeV.size(); i++) {
                            // Comparaison par ID (String) pour être sûr
                            if (voisinsDeV.get(i).destination.id.equals(u.id) && voisinsDeV.get(i).type == 1) {
                                voisinsDeV.remove(i);
                                break; // Suppression unique
                            }
                        }
                    }
                }

                // CAS 2 & 3 : Sens unique (Type 2) ou Double sens orienté (Type 3)
                // Pour le Type 3, la rue a deux voies distinctes.
                // Si je passe U->V (voie de droite), la voie V->U (voie de gauche) est encore libre.
                // -> ON NE SUPPRIME PAS L'INVERSE.

            } else {
                // Plus de voisins, on ajoute au cycle final et on dépile
                cycle.add(pile.pop());
            }
        }

        Collections.reverse(cycle);
        return cycle;
    }

    // --- MÉTHODES D'AFFICHAGE ET D'APPEL ---

    // Appel pour le Cas 1 (Graphe complet) et Cas 3 (Postier Chinois)
    public static void cycle(Graphe g, boolean estOriente) { // Le boolean ne sert plus ici, géré par Type
        try {
            long debut = System.currentTimeMillis();
            List<Sommet> resultat = trouverCycleEulerien(g);
            long fin = System.currentTimeMillis();

            System.out.println("\n[RÉSULTATS DE LA TOURNÉE]");
            System.out.println("------------------------------------------------");
            System.out.println("Statut      : SUCCÈS");
            System.out.println("Temps calcul: " + (fin - debut) + " ms");
            System.out.println("Nombre Rues : " + (resultat.size() - 1));
            System.out.println("------------------------------------------------");

            System.out.println("ITINÉRAIRE DÉTAILLÉ :");
            StringJoiner sj = new StringJoiner(" -> ");
            for (Sommet s : resultat) {
                sj.add(s.id);
            }
            System.out.println(sj.toString());
            System.out.println("------------------------------------------------");

        } catch (Exception e) {
            System.err.println("Erreur calcul cycle : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Appel pour le Cas 2 (Semi-Eulérien)
    public static void chemin(Graphe g, Sommet depot, List<Sommet> sommetsImpairs) {
        if (sommetsImpairs.size() != 2) return;

        Sommet u = sommetsImpairs.get(0);
        Sommet v = sommetsImpairs.get(1);

        System.out.println("--> Réparation du graphe (Chemin virtuel " + u.id + " <-> " + v.id + ")");

        // 1. Calcul du chemin le plus court
        Itineraire.Dijkstra cheminRetour = Itineraire.trouver_chemin(g, u, v);

        // 2. Ajout des arêtes virtuelles pour fermer le cycle
        List<Sommet> path = cheminRetour.getChemin();
        for (int i = 0; i < path.size() - 1; i++) {
            Sommet s1 = path.get(i);
            Sommet s2 = path.get(i+1);
            // On ajoute une arête Type 1 pour simplifier la fermeture
            g.ajouter_Arc(s1.id, s2.id, 0, 1);
        }

        // 3. Calcul du cycle sur le graphe fermé
        List<Sommet> cycleComplet = trouverCycleEulerien(g);

        // 4. Nettoyage de l'affichage (Optionnel : retirer le retour virtuel)
        // Pour l'instant, on affiche tout pour vérifier que ça boucle.

        System.out.println("\n[RÉSULTATS SEMI-EULÉRIEN]");
        System.out.println("Chemin ouvert trouvé (retour au dépôt non inclus ou via chemin virtuel) :");
        StringJoiner sj = new StringJoiner(" -> ");
        for (Sommet s : cycleComplet) sj.add(s.id);
        System.out.println(sj.toString());
    }
}