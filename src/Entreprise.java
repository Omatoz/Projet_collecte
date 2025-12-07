import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Entreprise {

    private Scanner scanner;

    public Entreprise(Scanner scanner) {
        this.scanner = scanner;
    }

    public void menu_entreprise() {
        while (true) {
            System.out.println("\n[MENU ENTREPRISE DE COLLECTE]");
            System.out.println("\nVeuillez choisir une fonctionnalité : ");
            System.out.println("  [1] Ramassage au pied des habitations");
            System.out.println("  [2] Optimisation des points de collecte");
            System.out.println("  [3] Retour au menu principal");
            System.out.print("Saisir votre choix : ");

            int theme = options(1, 3);

            switch (theme) {
                case 1:
                    choix_theme1();
                    break;
                case 2:
                    choix_theme2();
                    break;
                case 3:
                    return;
            }
        }
    }

    private void choix_theme1() {
        System.out.println("\n[MENU ENTREPRISE DE COLLECTE]");
        System.out.println("|RAMASSAGE AUX PIEDS DES HABITATIONS|");
        System.out.println("\nVeuillez choisir une méthode : ");
        System.out.println("  [1] Tournée des encombrants (1 ramassage)");
        System.out.println("  [2] Tournée des encombrants (TSP)");
        System.out.println("  [3] Tournée des poubelles (Postier Chinois)");
        System.out.println("  [4] Retour au menu principal");
        System.out.print("Saisir votre choix : ");

        int problematique = options(1, 4);

        switch (problematique) {
            case 1:
                executer_p0("|Tournée des encombrants (1 ramassage)|", 1);
                break;
            case 2:
                executer_p1("|Tournée des encombrants (TSP)|", 1);
                break;
            case 3:
                executer_p2("|Tournée des poubelles (Postier Chinois)|");
                break;
            case 4:
                return;
        }
        attente();
    }

    private void choix_theme2() {
        System.out.println("\n[MENU ENTREPRISE DE COLLECTE]");
        System.out.println("|OPTIMISATION DES POINTS DE COLLECTE|");
        System.out.println("\nVeuillez choisir une méthode : ");
        System.out.println("  [1] Heuristique du Plus Proche Voisin");
        System.out.println("  [2] Heuristique de l'Arbre Couvrant Minimal (MST)");
        System.out.println("  [3] Retour au menu principal");
        System.out.print("Saisir votre choix : ");

        int approche = options(1, 3);

        switch (approche) {
            case 1:
                executer_p1("|Heuristique du Plus Proche Voisin|", 2);
                break;
            case 2:
                System.out.println("|Heuristique de l'Arbre Couvrant Minimal (MST)|");
                executer_MST();
                break;
            case 3:
                return;
        }
        attente();
    }

    // Méthode d'execution de la problematique une du theme 1
    private void executer_p1(String titre, int theme) {
        // appel methodes
        affichage_titre1(theme, titre);
        int hypothese = choix_hypothese();
        int graphe_test = choix_graphe();
        Graphe g = charger_graphe1(hypothese, graphe_test);
        if (g == null) {
            return;
        }

        Sommet depot = null;

        if (graphe_test == 1) {
            depot = g.getSommet("A");
        } else if (graphe_test == 2) {
            System.out.println("Choisir le sommet de dépôt parmi N1 à N100 :");
            Scanner sc = new Scanner(System.in);
            String choix_depot;
            while (true) {
                choix_depot = sc.nextLine().trim();
                if (g.getSommet(choix_depot) != null) {
                    depot = g.getSommet(choix_depot);
                    break;
                } else {
                    System.out.println("Sommet invalide, réessayez :");
                }
            }
        }

        if (depot == null) {
            System.err.println("!!! ERREUR CRITIQUE : Le sommet de dépôt 'A' n'a pas été trouvé dans le graphe.");
            return;
        }

        List<Sommet> points_a_visiter = saisir_parcours(g, depot);
        if (points_a_visiter.isEmpty()) return;

        calculer_TSP(g, depot, points_a_visiter);
    }

    // Méthode execution problématique 2 du theme 1
    private void executer_p2(String titre) {
        afficher_titre2(titre);
        int cas = choix_cas();
        int hypothese = choix_hypothese();

        Graphe g = charger_graphe2(cas, hypothese);
        if (g == null) return;

        List<Sommet> impairsNonOrientes = Eulerien.Eulerien_non_oriente(g);
        List<Sommet> nonEquilibresOrientes = Eulerien.Eulerien_oriente(g);
        boolean mixteEulier = Eulerien.estMixteEulérien(g);
        executer_cas(g, cas, hypothese, impairsNonOrientes, nonEquilibresOrientes, mixteEulier);
    }

    private void affichage_titre1(int theme, String titre) {
        System.out.println("\n[MENU ENTREPRISE DE COLLECTE]");
        if (theme == 1) {
            System.out.println("|RAMASSAGE AUX PIEDS DES HABITATIONS|");
        }
        else if (theme == 2) {
            System.out.println("|OPTIMISATION DES POINTS DE COLLECTE|");
        }
        System.out.println(titre);
    }

    private void afficher_titre2(String titre) {
        System.out.println("\n[MENU ENTREPRISE DE COLLECTE]");
        System.out.println(titre);
        System.out.println("\nVeuillez choisir un cas :");
        System.out.println("  [1] Cas Idéal : Graphe entièrement pair (Algorithme de Hierholzer)");
        System.out.println("  [2] Cas Semi-Eulérien : 2 sommets impairs");
        System.out.println("  [3] Cas Général : Postier Chinois");
    }

    private int choix_cas() {
        System.out.print("Saisir votre choix : ");
        int cas = options(1, 3);
        switch (cas) {
            case 1: System.out.println("CAS IDEAL : Graphe Pair"); break;
            case 2: System.out.println("CAS SEMI-EULERIEN : 2 Sommets Impairs"); break;
            case 3: System.out.println("CAS GENERAL : Postier Chinois"); break;
        }
        return cas;
    }

    private int choix_hypothese() {
        System.out.println("\nVeuillez choisir une hypothèse de circulation : ");
        System.out.println("  [1] HO1 : Graphe non-orienté");
        System.out.println("  [2] HO2 : Graphe orienté");
        System.out.println("  [3] HO3 : Graphe mixte");
        System.out.print("Saisir votre choix : ");
        return options(1, 3);
    }

    private int choix_graphe() {
        System.out.println("[CHOIX DE GRAPHE]");
        System.out.println("\nVeuillez choisir le graphe étudié : ");
        System.out.println("  [1] Graphe simple (fictif)");
        System.out.println("  [2] Graphe complexe (réel)");
        System.out.print("Votre choix : ");
        return options(1,2);
    }

    private Graphe charger_graphe1(int hypothese, int graphe_test) {
        String f_sommets;
        String f_aretes;

        try {
            if (graphe_test == 1) {
                f_sommets = "ressources/sommets.txt";
                f_aretes = "ressources/aretes_ho" + hypothese + ".txt";
            } else if (graphe_test == 2) {
                f_sommets = "ressources/sommets_ville.txt";
                f_aretes = "ressources/ville_ho" + hypothese + ".txt";
            } else {
                System.err.println("ERREUR !!! Type de graphe inconnu : " + graphe_test);
                return null;
            }
            return new Graphe(f_sommets, f_aretes);

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("ERREUR !!! Impossible de construire la carte : " + e.getMessage());
            return null;
        }
    }

    private Graphe charger_graphe2(int cas, int hypothese) {
        String f_sommets = "ressources/sommets.txt";
        String f_aretes = "ressources/aretes_p2_c" + cas + "_ho" + hypothese + ".txt";
        try {
            Graphe g = new Graphe(f_sommets, f_aretes);
            System.out.println("\nGraphe chargé avec succès (" + g.get_Sommets().size() + " sommets).");
            return g;
        } catch (Exception e) {
            System.err.println("!!! Erreur de chargement !!! " + e.getMessage());
            return null;
        }
    }

    private List<Sommet> saisir_parcours(Graphe g, Sommet depot) {
        List<Sommet> points = new ArrayList<>();
        Set<Sommet> doublons = new HashSet<>();
        while (true) {
            System.out.println("\nTOURNEE");
            System.out.println("Sommets disponibles : " + g.get_Sommets());
            System.out.println("Saisir les sommets à visiter (10 max), séparés par des espaces :");
            String[] liste = scanner.nextLine().split(" ");

            boolean valide = true;
            doublons.clear();

            if (liste.length > 10) {
                System.out.println("!!! Erreur !!! Vous ne pouvez pas entrer plus de 10 sommets. !!!");
                valide = false;
            } else {
                for (String id : liste) {
                    if (!id.isEmpty()) {
                        Sommet s = g.getSommet(id);
                        if (s == null) {
                            System.out.println("!!! Erreur !!! Le sommet '" + id + "' n'existe pas. !!!");
                            valide = false;
                            break;
                        } else if (!s.equals(depot)) doublons.add(s);
                    }
                }
            }

            if (valide) {
                points.addAll(doublons);
                break;
            } else {
                System.out.println("Veuillez réessayer de définir la tournée.");
            }
        }
        return points;
    }

    private void calculer_TSP(Graphe g, Sommet depot, List<Sommet> points) {
        System.out.println("\nCalcul de la tournée pour les points : " + points + " en partant du dépôt " + depot);
        Tournee.TSP resultat = Tournee.calculer_tournee(g, depot, points);

        System.out.println("\n[PHASE 3] : RESULTATS\n");
        if (resultat.reussite()) {
            System.out.println("REUSSITE !!! Tournée calculée avec succès !!!");
            System.out.println("Ordre de visite optimisé : " + Tournee.format(resultat.getOrdre()));
            System.out.println("Distance totale : " + resultat.getDistance_totale());
            System.out.println("Chemin complet : " + Tournee.format(resultat.getChemin_final()));
        } else {
            System.out.println("ÉCHEC !!! La tournée est irréalisable !!!");
            System.out.println("Tournée partielle effectuée : " + resultat.getOrdre());
        }
    }

    private void executer_cas(Graphe g, int cas, int hypothese, List<Sommet> impairsNonOrientes, List<Sommet> nonEquilibresOrientes, boolean mixteEulier) {
        switch(cas) {
            case 1:
                executer_cas_ideal(g, hypothese, impairsNonOrientes, nonEquilibresOrientes, mixteEulier);
                break;
            case 2:
                executer_cas_semi_eulerien(g, hypothese, impairsNonOrientes, nonEquilibresOrientes);
                break;
            case 3:
                executer_cas_general(g, hypothese, nonEquilibresOrientes);
                break;
        }
    }

    private void executer_cas_ideal(Graphe g, int hypothese, List<Sommet> impairs, List<Sommet> nonEquilibres, boolean mixte) {
        if (hypothese == 1) {
            if (impairs.isEmpty()) {
                System.out.println("SUCCÈS !!! Le graphe est Eulérien (tous les sommets sont de degré pair)");
                Hierholzer.cycle(g, false);
            } else {
                System.out.println("ÉCHEC !!! Le graphe n'est pas Eulérien (" + impairs.size() + " sommets impairs)");
            }
        } else if (hypothese == 2) {
            if (nonEquilibres.isEmpty()) {
                System.out.println("SUCCÈS !!! Le graphe est Eulérien (tous les sommets sont de degré pair)");
                Hierholzer.cycle(g, true);
            } else {
                System.out.println("ÉCHEC !!! Le graphe n'est pas Eulérien (" + impairs.size() + " sommets impairs)");
            }
        } else if (hypothese == 3) {
            if (mixte) {
                System.out.println("SUCCÈS : Le graphe est Eulérien.");
                Hierholzer.cycle(g, true);
            } else {
                System.out.println("Le graphe n'est pas Eulérien.");
                Hierholzer.cycle(g, true);
            }
        }
    }

    private void executer_cas_semi_eulerien(Graphe g, int hypothese, List<Sommet> impairs, List<Sommet> nonEquilibres) {
        if (hypothese == 1) {
            if (impairs.size() == 2) {
                System.out.println("ÉCHEC !!! Le graphe n'est pas Eulérien (2 sommets impairs)");
                Hierholzer.chemin(g, g.getSommet("A"), impairs);
            } else {
                System.out.println("ÉCHEC !!! Le graphe n'est pas Eulérien (" + impairs.size() + " sommets impairs)");
            }
        } else if ((hypothese == 2) || (hypothese == 3)) {
            if (nonEquilibres.isEmpty()) {
                System.out.println("SUCCÈS !!! Le graphe est Eulérien.");
                Hierholzer.cycle(g, true);
            } else {
                System.out.println("ÉCHEC !!! Le graphe n'est pas Eulérien (" + impairs.size() + " sommets impairs)");
                Postier.lancer(g, 2);
            }
        }
    }

    private void executer_cas_general(Graphe g, int hypothese, List<Sommet> nonEquilibres) {
        if (hypothese == 1 || !nonEquilibres.isEmpty()) {
            System.out.println("ÉCHEC !!! Le graphe n'est pas Eulérien (" + nonEquilibres.size() + " sommets non equilibrés)");
            Postier.lancer(g, hypothese);
        } else {
            System.out.println("SUCCÈS !!! Le graphe est Eulérien.");
            Hierholzer.cycle(g, true);
        }
    }

    private int options(int min, int max) {
        int choix;
        while (true) {
            try {
                choix = scanner.nextInt();
                scanner.nextLine();
                if (choix >= min && choix <= max) return choix;
            } catch (Exception e) {
                scanner.nextLine();
            }
            System.out.print("Choix invalide : ");
        }
    }

    private void attente() {
        System.out.println("\nAppuyez sur [Entrée] pour retourner au menu principal");
        scanner.nextLine();
    }

    //approche 2 thème 2
    private void executer_MST(){
        int hypothse = choix_hypothese();
        int graphe_test = choix_graphe();
        try {
            //charger MST pour avoir sommets et aretes du graphe initial
            MST mst = charger_MST(hypothse, graphe_test);

            Set<String> aretesAffichees = new HashSet<>();
            for (Sommet s : mst.getGraphe().get_Sommets()) {
                for (Arete a : s.aretes) {
                    String key = a.depart.id.compareTo(a.destination.id) < 0
                            ? a.depart.id + "-" + a.destination.id
                            : a.destination.id + "-" + a.depart.id;
                    if (!aretesAffichees.contains(key)) {
                        System.out.println("Arête: " + a.depart.id + "-" + a.destination.id + " (Poids = " + a.poids + ")");
                        aretesAffichees.add(key);
                    }
                }
            }

            //arbre couvrant de poids minimum
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" ");
            System.out.println("2) ARBRE COUVRANT DE POIDS MINIMAL");
            mst.calculACM();
            for (Arete a : mst.getAcm()) {
                System.out.println("Arête : " + a.depart.id + "-" + a.destination.id + " (Poids = " + a.poids + ")");
            }

            //dfs sur arbre couvrant du poids minimum
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" ");
            System.out.println("3) PARCOURS DFS : ordre de visite des points de collecte");
            mst.parcoursDFS();
            List<Sommet> parcoursDFS = mst.getParcoursDFS();

            //shortcutting sur parcours dfs
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" ");
            System.out.println("4) SHORTCUTTING");
            mst.parcourShortcutting();
            List<Sommet> parcoursShortcut = mst.getParcoursShortcut();

            //decoupage tournees par rapport a la capacite du camion
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" ");
            System.out.println("5) DECOUPAGE EN TOURNEES EN FONCTION DE LA CAPACITE ");
            int capaciteMax = 15;   // attribut à modifier
            mst.decouperTournees(capaciteMax);

            List<List<Sommet>> tournees = mst.getTournees();
            System.out.println("--------------------------------------------------------------");
            System.out.println("RÉCAPITULATIF DES TOURNÉES");
            System.out.println("Capacité maximale du camion : " + capaciteMax);
            System.out.println("Nombre total de tournées : " + tournees.size());
            System.out.println("--------------------------------------------------------------");

            int index = 1;
            for (List<Sommet> tournee : tournees) {
                System.out.println("Tournée " + index + " :");
                int chargeTotale = 0;
                int distanceTotale = 0;
                for (int i = 0; i < tournee.size(); i++) {
                    Sommet s = tournee.get(i);
                    System.out.print(s.id);

                    if (i < tournee.size() - 1) {
                        Sommet suivant = tournee.get(i + 1);

                        // Distance réelle via Dijkstra
                        Itineraire.Dijkstra d = Itineraire.trouver_chemin(mst.getGraphe(), s, suivant);
                        distanceTotale += d.getDistance();

                        System.out.print(" -> ");
                    }

                    if (i > 0) {
                        chargeTotale += s.contenance;
                    }
                }

                System.out.println(" ");
                System.out.println("Charge totale collectée : " + chargeTotale);
                System.out.println("Distance totale parcourue : " + distanceTotale);
                System.out.println("--------------------------------------------------------------");
                index++;
            }

        } catch (Exception e) {
            System.err.println("Erreur!");
        }
    }

    private MST charger_MST(int hypothese, int graphe_test) {
        try {
            String fichierSommets;
            String fichierAretes;
            fichierSommets = "ressources/sommets_contenance.txt";

            switch (hypothese) {
                case 1 -> fichierAretes = "ressources/aretes_ho1.txt";
                case 2 -> fichierAretes = "ressources/aretes_ho2.txt";
                case 3 -> fichierAretes = "ressources/aretes_ho3.txt";
                default -> {
                    System.err.println("Hypothèse invalide.");
                    return null;
                }
            }

            return new MST(fichierSommets, fichierAretes);

        } catch (FileNotFoundException e) {
            System.err.println("Erreur : fichier introuvable.");
            return null;
        }
    }





    //hypothèse 1 problématique 1 thème 1
    private void executer_p0(String titre, int theme) {
        affichage_titre1(theme, titre);
        int hypothese = choix_hypothese();
        int graphe_test = choix_graphe();
        Graphe g = charger_graphe1(hypothese, graphe_test);
        if (g == null) {
            return;
        }
        Sommet depot = g.getSommet("A");
        if (depot == null) {
            System.err.println("!!! ERREUR CRITIQUE : Le sommet de dépôt 'A' n'a pas été trouvé dans le graphe.");
            return;
        }
        Scanner sc = new Scanner(System.in);
        Sommet arrivee = null;
        while (arrivee == null) {
            System.out.println("Choisir le sommet d'arrivée :");
            String choix = sc.nextLine().trim();
            if (g.getSommet(choix) != null) {
                arrivee = g.getSommet(choix);
            } else {
                System.out.println("Sommet invalide, réessayez :");
            }
        }

        UnRamassage collecte = new UnRamassage(g);
        UnRamassage.Ramassage resultat = collecte.ramassage(depot, arrivee);

        System.out.println("\n Résultat du ramassage ");
        System.out.println("Aller : " + resultat.chemin(resultat.aller.getChemin()) + " (distance = " + resultat.aller.getDistance() + ")");
        System.out.println("Retour : " + resultat.chemin(resultat.retour.getChemin()) + " (distance = " + resultat.retour.getDistance() + ")");
        System.out.println("Distance totale : " + resultat.distance);
    }

}
