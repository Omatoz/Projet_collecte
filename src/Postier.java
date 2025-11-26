import java.util.*;

public class Postier {
    public static void lancer(Graphe g) {

        // --- ÉTAPE 1 : TROUVER LES SOMMETS IMPAIRS ---
        List<Sommet> sommetsImpairs = Eulerien.Eulerien_non_oriente(g);
        System.out.println("--> " + sommetsImpairs.size() + " sommets de degré impair trouvés : " + sommetsImpairs);

        // --- ÉTAPE 2 : CALCULER LES DISTANCES ENTRE LES SOMMETS IMPAIRS ---
        System.out.println("--> Calcul des plus courts chemins entre les sommets impairs...");
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matriceDistances = new HashMap<>();
        for (Sommet source : sommetsImpairs) {
            matriceDistances.put(source, new HashMap<>());
            for (Sommet dest : sommetsImpairs) {
                if (!source.equals(dest)) {
                    matriceDistances.get(source).put(dest, Itineraire.trouver_chemin(g, source, dest));
                }
            }
        }

        // --- ÉTAPE 3 : TROUVER UN COUPLAGE AVEC UNE HEURISTIQUE GLOUTONNE ---
        System.out.println("--> Recherche d'un couplage optimal (heuristique gloutonne)...");
        List<List<Sommet>> paires = trouverCouplageGlouton(sommetsImpairs, matriceDistances);
        System.out.println("--> Paires trouvées pour réparer le graphe : " + paires);

        // --- ÉTAPE 4 : CRÉER LE GRAPHE "RÉPARÉ" EN DUPLIQUANT LES ARÊTES ---
        Graphe grapheRepare = new Graphe(g); // On travaille sur une copie
        int coutReparation = 0;

        for (List<Sommet> paire : paires) {
            Sommet u = paire.get(0);
            Sommet v = paire.get(1);
            Itineraire.Dijkstra cheminReparation = matriceDistances.get(u).get(v);
            coutReparation += cheminReparation.getDistance();

            List<Sommet> cheminADupliquer = cheminReparation.getChemin();
            for (int i = 0; i < cheminADupliquer.size() - 1; i++) {
                Sommet s1 = cheminADupliquer.get(i);
                Sommet s2 = cheminADupliquer.get(i + 1);

                int poids = 0; // On doit retrouver le poids de l'arête originale
                for(Arete a : s1.aretes){
                    if(a.destination.equals(s2)){
                        poids = a.poids;
                        break;
                    }
                }
                grapheRepare.ajouter_Rues(s1.id, s2.id, poids, 1); // Type 1 pour double sens
            }
        }
        System.out.println("--> Coût de la duplication des chemins : " + coutReparation);

        // --- ÉTAPE 5 : LANCER HIERHOLZER SUR LE GRAPHE RÉPARÉ ---
        System.out.println("--> Le graphe est maintenant eulérien. Lancement de Hierholzer...");
        Hierholzer.cycle(grapheRepare, false); // false = non orienté

        // Calcul de la distance totale
        int distanceOriginale = 0;
        for(Sommet s : g.get_Sommets()){
            for(Arete a : s.aretes){
                distanceOriginale += a.poids;
            }
        }
        distanceOriginale /= 2; // Dans un graphe non orienté, chaque arête est comptée deux fois.

        System.out.println("--> Distance totale de la tournée : " + (distanceOriginale + coutReparation));
    }

    private static List<List<Sommet>> trouverCouplageGlouton(List<Sommet> sommetsImpairs, Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matriceDistances) {
        List<List<Sommet>> paires = new ArrayList<>();
        List<Sommet> sommetsRestants = new ArrayList<>(sommetsImpairs);

        while (!sommetsRestants.isEmpty()) {
            Sommet u = sommetsRestants.get(0);
            Sommet plusProche = null;
            int distanceMin = Integer.MAX_VALUE;

            for (int i = 1; i < sommetsRestants.size(); i++) {
                Sommet v = sommetsRestants.get(i);
                int distance = matriceDistances.get(u).get(v).getDistance();
                if (distance < distanceMin) {
                    distanceMin = distance;
                    plusProche = v;
                }
            }

            if (plusProche != null) {
                paires.add(List.of(u, plusProche));
                sommetsRestants.remove(u);
                sommetsRestants.remove(plusProche);
            } else {
                // Ne devrait pas arriver s'il y a un nombre pair de sommets impairs
                sommetsRestants.remove(u);
            }
        }
        return paires;
    }
}
