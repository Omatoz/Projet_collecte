import java.util.*;

public class Postier {

    public static void lancer(Graphe g, int hypothese) {
        if (hypothese == 1) {
            lancer_non_oriente(g);
        } else if (hypothese == 2) {
            lancer_oriente(g);
        } else if (hypothese == 3) {
            lancer_mixte(g);
        }
    }

    private static void lancer_non_oriente(Graphe g) {
        List<Sommet> sommetsImpairs = Eulerien.Eulerien_non_oriente(g);
        int nbImpairs = sommetsImpairs.size();
        System.out.println("DIAGNOSTIC : " + nbImpairs + " sommet(s) de degré impair trouvé(s).");

        if (nbImpairs == 0) {
            System.out.println("Le graphe est Eulérien (Cas 1).");
            Hierholzer.cycle(g, false);
        } else if (nbImpairs == 2) {
            System.out.println("Le graphe est Semi-Eulérien (Cas 2).");
            Hierholzer.chemin(g, g.getSommet("A"), sommetsImpairs);
        } else {
            System.out.println("Le graphe est un cas pour le Postier Chinois (Cas 3).");
            resoudrePostierNonOriente(g, sommetsImpairs);
        }
    }

    private static void resoudrePostierNonOriente(Graphe g, List<Sommet> sommetsImpairs) {
        System.out.println("Calcul des plus courts chemins entre les sommets impairs...");
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matrice = calculerMatriceDistances(g, sommetsImpairs, sommetsImpairs);

        System.out.println("Recherche d'un couplage (heuristique gloutonne)...");
        List<List<Sommet>> paires = trouverCouplageGloutonNonOriente(sommetsImpairs, matrice);
        System.out.println("Paires trouvées pour réparer le graphe : " + paires);

        Graphe grapheRepare = new Graphe(g);
        int coutReparation = 0;

        for (List<Sommet> paire : paires) {
            Itineraire.Dijkstra chemin = matrice.get(paire.get(0)).get(paire.get(1));
            coutReparation += chemin.getDistance();

            // On duplique le chemin pour équilibrer les sommets impairs
            dupliquerCheminNonOriente(grapheRepare, chemin);
        }

        System.out.println("Coût de la duplication : " + coutReparation);
        System.out.println("Le graphe est maintenant eulérien. Lancement de Hierholzer...");
        Hierholzer.cycle(grapheRepare, false);

        int distanceOriginale = calculerDistanceTotale(g, false);
        System.out.println("Distance totale de la tournée : " + (distanceOriginale + coutReparation));
    }

    private static void dupliquerCheminNonOriente(Graphe graphe, Itineraire.Dijkstra chemin) {
        List<Sommet> cheminADupliquer = chemin.getChemin();

        for (int i = 0; i < cheminADupliquer.size() - 1; i++) {
            Sommet s1 = cheminADupliquer.get(i);
            Sommet s2 = cheminADupliquer.get(i + 1);

            boolean existe = false;
            for (Arete a : s1.aretes) {
                if (a.destination.equals(s2)) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                int poids = 1;
                for (Arete a : s1.aretes) {
                    if (a.destination.equals(s2)) {
                        poids = a.poids;
                        break;
                    }
                }
                graphe.ajouter_Rues(s1.id, s2.id, poids, 1);
                graphe.ajouter_Rues(s2.id, s1.id, poids, 1);
                System.out.println("Arête de réparation ajoutée : " + s1.id + " <-> " + s2.id);
            }
        }
    }


    private static void lancer_oriente(Graphe g) {
        List<Sommet> sommetsNonEquilibres = Eulerien.Eulerien_oriente(g);

        if (sommetsNonEquilibres.isEmpty()) {
            System.out.println("DIAGNOSTIC : Le graphe est Eulérien (équilibré).");
            Hierholzer.cycle(g, true);
        } else {
            System.out.println("DIAGNOSTIC : Le graphe n'est pas Eulérien.");
            resoudrePostierOriente(g);
        }
    }

    private static void resoudrePostierOriente(Graphe g) {
        System.out.println("    Lancement de l'algorithme du Postier Chinois Orienté...");
        Map<Sommet, Integer> differences = calculerDifferencesDegres(g);
        List<Sommet> sources = new ArrayList<>();
        List<Sommet> puits = new ArrayList<>();
        for (Map.Entry<Sommet, Integer> entry : differences.entrySet()) {
            if (entry.getValue() > 0) sources.add(entry.getKey());
            else if (entry.getValue() < 0) puits.add(entry.getKey());
        }
        System.out.println("--> " + sources.size() + " sources et " + puits.size() + " puits trouvés.");

        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matrice = calculerMatriceDistances(g, sources, puits);

        System.out.println("--> Recherche des chemins de réparation (heuristique gloutonne)...");
        List<Itineraire.Dijkstra> cheminsAReparer = trouverReparationsGloutonnesOriente(differences, matrice);

        Graphe grapheRepare = new Graphe(g);
        int coutReparation = 0;
        for (Itineraire.Dijkstra chemin : cheminsAReparer) {
            Sommet sourceChoisie = chemin.getChemin().get(0);
            Sommet puitChoisi = chemin.getChemin().get(chemin.getChemin().size() - 1);

            // On duplique le chemin dans le graphe réparé en inversant si nécessaire
            dupliquerCheminOriente(grapheRepare, chemin, sourceChoisie, puitChoisi);
            coutReparation += chemin.getDistance();
            System.out.println("Chemin ajouté : " + chemin.getChemin() + " (création arêtes si manquantes)");
        }
        System.out.println("Coût total de la réparation : " + coutReparation);

        System.out.println("\nLe graphe est maintenant équilibré. Lancement de Hierholzer...");
        Hierholzer.cycle(grapheRepare, true);

        int distanceOriginale = calculerDistanceTotale(g, true);
        System.out.println("Distance totale de la tournée : " + (distanceOriginale + coutReparation));
    }

    private static void lancer_mixte(Graphe g) {
        List<Sommet> sommetsProbleme = Eulerien.trouverSommetsImpairsMixtes(g);

        if (sommetsProbleme.size() == 2) {
            System.out.println("Le graphe mixte est Eulérien.");
            System.out.println("L'algorithme de Hierholzer pour graphe mixte n'est pas implémenté.");
            Hierholzer.cycle(g, true);
            // Hierholzer.cheminMixte(g, g.getSommet("A"), sommetsProbleme); // Appel futur
        } else {
            System.out.println("Le graphe mixte n'est pas Eulérien.");
            System.out.println("Il a " + sommetsProbleme.size() + " sommets à problème : " + sommetsProbleme);
            System.out.println("Lancement de l'algorithme du Postier Chinois Mixte...");
            resoudrePostierMixte(g, sommetsProbleme);
        }
    }
    private static void resoudrePostierMixte(Graphe g, List<Sommet> sommetsProbleme) {
        System.out.println("Calcul des plus courts chemins entre les sommets à problème...");
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matrice = calculerMatriceDistances(g, sommetsProbleme, sommetsProbleme);

        System.out.println("Recherche d'un couplage (heuristique gloutonne)...");
        List<List<Sommet>> paires = trouverCouplageGloutonNonOriente(sommetsProbleme, matrice);
        System.out.println("Paires trouvées pour réparer le graphe : " + paires);

        Graphe grapheRepare = new Graphe(g);
        int coutReparation = 0;
        for (List<Sommet> paire : paires) {
            Itineraire.Dijkstra chemin = matrice.get(paire.get(0)).get(paire.get(1));

            Sommet sourceChoisie = chemin.getChemin().get(0);
            Sommet puitChoisi = chemin.getChemin().get(chemin.getChemin().size() - 1);

            // Duplique le chemin en inversant si nécessaire
            dupliquerCheminOriente(grapheRepare, chemin, sourceChoisie, puitChoisi);
            coutReparation += chemin.getDistance();
        }
        System.out.println("Coût de la duplication : " + coutReparation);

        System.out.println("Le graphe est maintenant équilibré (traité comme orienté). Lancement de Hierholzer...");
        Hierholzer.cycle(grapheRepare, true);

        int distanceOriginale = calculerDistanceTotale(g, true);
        System.out.println("Distance totale de la tournée : " + (distanceOriginale + coutReparation));
    }

    private static void dupliquerCheminOriente(Graphe graphe, Itineraire.Dijkstra chemin, Sommet source, Sommet puit) {
        List<Sommet> cheminADupliquer = new ArrayList<>(chemin.getChemin());

        // Si la première arête existe déjà, on inverse le chemin
        boolean existeSensOriginal = false;
        Sommet s1 = cheminADupliquer.get(0);
        Sommet s2 = cheminADupliquer.get(1);
        for (Arete a : s1.aretes) {
            if (a.destination.equals(s2)) {
                existeSensOriginal = true;
                break;
            }
        }

        // On inverse le chemin si l'arête existe déjà
        if (existeSensOriginal) {
            Collections.reverse(cheminADupliquer);
        }

        // On ajoute maintenant les arêtes du chemin (inversé si nécessaire)
        for (int i = 0; i < cheminADupliquer.size() - 1; i++) {
            s1 = cheminADupliquer.get(i);
            s2 = cheminADupliquer.get(i + 1);

            boolean existe = false;
            for (Arete a : s1.aretes) {
                if (a.destination.equals(s2)) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                int poids = 1;
                graphe.ajouter_Arc(s1.id, s2.id, poids, 2);
                System.out.println("Arête de réparation ajoutée : " + s1.id + " -> " + s2.id);
            }
        }
    }

    /*private static void lancer_oriente_simplifie(Graphe g) {
        System.out.println("\n--- Analyse pour graphe ORIENTÉ/MIXTE ---");
        List<Sommet> sommetsNonEquilibres = Eulerien.Eulerien_oriente(g);

        if (sommetsNonEquilibres.isEmpty()) {
            // Le seul cas qui marche : le graphe est déjà parfait.
            System.out.println("--> DIAGNOSTIC : Le graphe est Eulérien (parfaitement équilibré).");
            Hierholzer.cycle(g, true);
        } else {
            // Si le graphe n'est pas parfait, on s'arrête et on explique.
            System.out.println("--> DIAGNOSTIC : Le graphe n'est pas Eulérien.");
            System.out.println("    Il contient " + sommetsNonEquilibres.size() + " sommets non équilibrés, il est donc impossible de trouver un cycle eulérien simple.");
            System.out.println("    Sommets à problème (où 'entrant' != 'sortant') : " + sommetsNonEquilibres);
            System.out.println("    La résolution de ce cas (Postier Chinois Orienté) n'est pas implémentée.");
        }
    }*/

    private static Map<Sommet, Integer> calculerDifferencesDegres(Graphe g) {
        Map<Sommet, Integer> differences = new HashMap<>();
        Map<Sommet, Integer> degreEntrant = new HashMap<>();

        // Initialise les compteurs pour tous les sommets
        for (Sommet s : g.get_Sommets()) {
            degreEntrant.put(s, 0);
            differences.put(s, 0);
        }

        // Calcule tous les degrés entrants et sortants en un seul passage
        for (Sommet s : g.get_Sommets()) {
            int sortant = s.aretes.size();
            differences.put(s, differences.get(s) + sortant); // Ajoute le degré sortant

            for (Arete a : s.aretes) {
                Sommet destination = a.destination;
                degreEntrant.put(destination, degreEntrant.get(destination) + 1);
            }
        }

        // Calcule la différence finale (sortant - entrant)
        for (Sommet s : g.get_Sommets()) {
            differences.put(s, differences.get(s) - degreEntrant.get(s));
        }

        // On ne garde que les sommets qui ont une différence non nulle
        differences.values().removeIf(v -> v == 0);
        return differences;
    }

    private static Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> calculerMatriceDistances(Graphe g, List<Sommet> departs, List<Sommet> arrivees) {
        Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matrice = new HashMap<>();
        for (Sommet source : departs) {
            matrice.put(source, new HashMap<>());
            for (Sommet dest : arrivees) {
                if (!source.equals(dest)) {
                    matrice.get(source).put(dest, Itineraire.trouver_chemin(g, source, dest));
                }
            }
        }
        return matrice;
    }

    private static List<Itineraire.Dijkstra> trouverReparationsGloutonnesOriente(Map<Sommet, Integer> differences, Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matrice) {
        List<Itineraire.Dijkstra> reparations = new ArrayList<>();

        // On continue tant qu'il y a des sommets avec un déficit (sortant > entrant)
        boolean aReparer = true;
        while(aReparer) {
            aReparer = false;
            Itineraire.Dijkstra meilleureReparation = null;
            int distanceMin = Integer.MAX_VALUE;

            // On cherche la meilleure paire (source active, puit actif)
            for (Sommet source : differences.keySet()) {
                if (differences.get(source) <= 0) continue; // Si ce n'est plus une source active

                for (Sommet puit : differences.keySet()) {
                    if (differences.get(puit) >= 0) continue; // Si ce n'est plus un puit actif

                    if (matrice.containsKey(source) && matrice.get(source).containsKey(puit)) {
                        int distance = matrice.get(source).get(puit).getDistance();
                        if (distance < distanceMin) {
                            distanceMin = distance;
                            meilleureReparation = matrice.get(source).get(puit);
                        }
                    }
                }
            }

            if (meilleureReparation != null) {
                reparations.add(meilleureReparation);

                Sommet sourceChoisie = meilleureReparation.getChemin().get(0);
                Sommet puitChoisi = meilleureReparation.getChemin().get(meilleureReparation.getChemin().size() - 1);

                // On met à jour les comptes : la source a une sortie en moins à combler, le puit une entrée en moins à recevoir
                differences.put(sourceChoisie, differences.get(sourceChoisie) - 1);
                differences.put(puitChoisi, differences.get(puitChoisi) + 1);

                aReparer = true; // On signale qu'on a fait une réparation, il faut peut-être continuer
            }
        }
        return reparations;
    }


    // --- Méthode de Couplage pour le cas NON ORIENTÉ ---
    private static List<List<Sommet>> trouverCouplageGloutonNonOriente(List<Sommet> sommetsImpairs, Map<Sommet, Map<Sommet, Itineraire.Dijkstra>> matriceDistances) {
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
                sommetsRestants.remove(u); // Sécurité en cas de nombre impair de sommets
            }
        }
        return paires;
    }

    private static int calculerDistanceTotale(Graphe g, boolean estOriente) {
        int distance = 0;
        for (Sommet s : g.get_Sommets()) {
            for (Arete a : s.aretes) {
                distance += a.poids;
            }
        }
        return estOriente ? distance : distance / 2;
    }
}