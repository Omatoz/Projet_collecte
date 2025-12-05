import java.io.*;
import java.util.*;
import java.util.Scanner;

public class Menu {

    private Scanner scanner; // On utilise un seul scanner pour toute l'application

    public Menu() {

        this.scanner = new Scanner(System.in);
    }

    public void lancer() {

        // Boucle principale du programme. Elle continue tant que l'utilisateur ne choisit pas de quitter.
        while (true) {
            afficher_menu();
            int theme = options(1, 4);

            switch (theme) {
                case 1:
                    choix_theme1();
                    break;
                case 2:
                    choix_theme2();
                    break;
                case 3:
                    choix_theme3();
                    break;
                case 4:
                    System.out.println("Au revoir :) !!!");
                    scanner.close(); // On ferme le scanner juste avant de quitter.
                    return; // Termine la méthode lancer() et donc le programme.
            }
        }
    }

    private void afficher_menu() {
        System.out.println("Projet d'Optimisation de Tournée de Collecte de Déchets");
        System.out.println("Veuillez choisir un thème à explorer :");
        System.out.println("  [1] Thème 1 : Ramassage aux pieds des habitations");
        System.out.println("  [2] Thème 2 : Optimiser les ramassages des points de collecte");
        System.out.println("  [3] Thème 3 : Planifier les jours de passage dans les quartiers");
        System.out.println("  [4] Quitter");
        System.out.print("Saisir votre choix : ");
    }

    private void choix_theme1() {
        System.out.println("\nTHÈME 1 : Ramassage aux pieds des habitations");
        System.out.println("Veuillez choisir une problématique :");
        System.out.println("  [1] Problématique 1 : Tournée des encombrants (TSP)");
        System.out.println("  [2] Problématique 2 : Tournée des poubelles (Postier Chinois)");
        System.out.print("Saisir votre choix : ");

        int problematique = options(1, 2);

        System.out.print("\nTHÈME 1 : Ramassage aux pieds des habitations");

        if (problematique == 1) {
            executer_p1("PROBLÉMATIQUE 1 : Tournée des encombrants (TSP)");
        } else {
            executer_p2("PROBLÉMATIQUE 2 : Tournée des poubelles (Postier Chinois)");
        }
        attente();
    }

    private void choix_theme2() {
        System.out.println("\nTHÈME 2 : Optimiser les ramassages des points de collecte");
        System.out.println("Veuillez choisir une méthode de résolution à tester :");
        System.out.println("  [1] Approche 1 : Heuristique du Plus Proche Voisin");
        System.out.println("  [2] Approche 2 : Heuristique de l'Arbre Couvrant Minimal (MST) [Non implémentée]");
        System.out.print("Saisir votre choix : ");

        int approche = options(1, 2);

        System.out.print("\nTHÈME 2 : Optimiser les ramassages des points de collecte");

        if (approche == 1) {
            executer_p1("APPROCHE 1 : Heuristique du Plus Proche Voisin");
        } else {
            System.out.println("APPROCHE 2 Heuristique de l'Arbre Couvrant Minimal (MST)");
        }
        attente();
    }

    private void choix_theme3() {
        System.out.println("\nTHÈME 3 : Planification des jours de passage dans les differents quarties");
        System.out.println("Veuillez choisir une hypothèse :");
        System.out.println("  [1] Hypothèse 1 : Collecte des secteurs (Welsh et Powell)");
        System.out.println("  [2] Hypothèse 2 : Collecte des secteurs sans nuisance et en tenant en compte les quantités et les capacités de collecte");
        System.out.print("Saisir votre choix : ");

        int hypothese = options(1, 2);

        if (hypothese == 1) {
            executer_p4("[1] Hypothèse 1 : Collecte des secteurs (Welsh et Powell)");
        } else {
            executer_p5("[2] Hypothèse 2 : Collecte des secteurs sans nuisance et en tenant en compte les quantités et les capacités de collecte");
        }
        attente();
    }

    // Méthode d'execution de la problematique une du theme 1
    private void executer_p1(String titre) {
        System.out.println("\n" + titre);
        System.out.println("Veuillez choisir une hypothèse de circulation :");
        System.out.println("  [1] HO1 : Graphe non-orienté");
        System.out.println("  [2] HO2 : Graphe orienté");
        System.out.println("  [3] HO3 : Graphe mixte");
        System.out.print("Saisir votre choix : ");
        int hypothese = options(1, 3);

        Graphe g = null;
        try {
            String f_aretes = "ressources/aretes_ho" + hypothese + ".txt";
            String f_sommets = "ressources/sommets.txt";
            g = new Graphe(f_sommets, f_aretes);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("ERREUR !!! Impossible de construire la carte : " + e.getMessage());
            return;
        }

        Sommet depot = g.getSommet("A");
        if (depot == null) {
            System.err.println("!!! ERREUR CRITIQUE : Le sommet de dépôt 'A' n'a pas été trouvé dans le graphe.");
            return;
        }

        List<Sommet> points_a_visiter = new ArrayList<>();

        while (true) {
            System.out.println("\nTOURNEE");
            System.out.println("Sommets disponibles : " + g.get_Sommets());

            if (depot == null) {
                System.out.println("!!! Erreur ::: Le sommet 'A' n'existe pas. Veuillez réessayer. !!!");
                scanner.nextLine(); // nettoie fin de ligne
            } else {
                System.out.println("Saisir les sommets à visiter (10 max), séparés par des espaces :");
                String s = scanner.nextLine();

                String[] liste = s.split(" ");

                boolean valide = true;
                Set<Sommet> doublons = new HashSet<>(); // pas de doublons !!!!

                if (liste.length > 10) {
                    System.out.println("!!! Erreur !!! Vous ne pouvez pas entrer plus de 10 sommets. !!!");
                    valide = false;
                } else {
                    for (String id : liste) {
                        if (!id.isEmpty()) {
                            Sommet sommet = g.getSommet(id);
                            if (sommet == null) {
                                System.out.println("!!! Erreur !!! Le sommet '" + id + "' n'existe pas. !!!");
                                valide = false;
                                break;
                            } else if (sommet.equals(depot)) {
                                System.out.println("Le dépôt '" + id + "' sera ignoré de la liste. !!!");
                            } else {
                                if (!doublons.add(sommet)) {
                                    System.out.println("!!! Avertissement !!! Le sommet '" + id + "' est un doublon et ne sera visité qu'une fois. !!!");
                                }
                            }
                        }
                    }
                }
                if (valide) {
                    points_a_visiter.addAll(doublons);
                    break;
                } else {
                    System.out.println("Veuillez réessayer de définir la tournée.");
                }
            }
        }

        System.out.println("\nCalcul de la tournée pour les points : " + points_a_visiter + " en partant du dépôt " + depot);
        Tournee.TSP resultat = Tournee.calculer_tournee(g, depot, points_a_visiter);

        System.out.println("\n[PHASE 3] : RESULTATS\n");
        if (resultat.reussite()) {
            System.out.println("REUSSITE !!! Tournée calculée avec succès !!!");
            System.out.println("Ordre de visite optimisé : " + Tournee.formatChemin(resultat.getOrdre()));
            System.out.println("Distance totale : " + resultat.getDistance_totale());
            System.out.println("Chemin complet : " + Tournee.formatChemin(resultat.getChemin_final()));
        } else {
            System.out.println("ÉCHEC !!! La tournée est irréalisable !!!");
            System.out.println("Tournée effectuée : " + resultat.getOrdre());
        }
    }
    // Méthode execution problématique 2 du theme 1
    private void executer_p2(String titre) {
        System.out.println("\n" + titre);
        System.out.println("Veuillez choisir un cas :");
        System.out.println("  [1] Cas Idéal : Graphe entièrement pair (Algorithme de Hierholzer)");
        System.out.println("  [2] Cas Semi-Eulérien : 2 sommets impairs");
        System.out.println("  [3] Cas Général : Postier Chinois");
        System.out.print("Saisir votre choix : ");

        int cas = options(1, 3);

        System.out.println("\nPROBLÉMATIQUE 2 : Organiser la collecte des poubelles (Cycle Eulérien)");
        if (cas == 1) {
            System.out.println("CAS IDEAL : Graphe Pair");
        } else if (cas == 2) {
            System.out.println("CAS SEMI-EULERIEN : 2 Sommets Impairs");
        } else if (cas == 3) {
            System.out.println("CAS GENERAL : Postier Chinois");
        }

        System.out.println("Veuillez choisir une hypothèse de circulation :");
        System.out.println("  [1] HO1 : Graphe non-orienté");
        System.out.println("  [2] HO2 : Graphe orienté");
        System.out.println("  [3] HO3 : Graphe mixte");
        System.out.print("Saisir votre choix : ");

        int hypothese = options(1, 3);

        String f_sommets = "ressources/sommets.txt";
        String f_aretes = "";

        f_aretes = "ressources/aretes_p2_c" + cas + "_ho" + hypothese + ".txt";

        Graphe g = null;
        try {
            g = new Graphe(f_sommets, f_aretes);
            System.out.println("\nGraphe chargé avec succès (" + g.get_Sommets().size() + " sommets).");
        } catch (Exception e) {
            System.err.println("!!! Erreur de chargement !!! " + e.getMessage());
            return;
        }


        List<Sommet> sommetsImpairs = Eulerien.Eulerien_non_oriente(g);
        List<Sommet> sommets_non_equilibres = Eulerien.Eulerien_oriente(g);
        List<Sommet> problemesMixtes = Eulerien.trouverSommetsImpairsMixtes(g);

        boolean test = Eulerien.estMixteEulérien(g);

        if (cas == 1) {
            if (hypothese == 1) {
                if (sommetsImpairs.isEmpty()) {
                    System.out.println("SUCCÈS : Le graphe est Eulérien (tous les sommets sont de degré pair)");
                    Hierholzer.cycle(g, false);
                } else {
                    System.out.println("ÉCHEC !!! Le fichier chargé ne correspond pas à un graphe eulérien.");
                    System.out.println("Sommets impairs trouvés : " + sommetsImpairs);
                }
            } else if (hypothese == 2) {
                if (sommets_non_equilibres.isEmpty()) {
                    System.out.println("SUCCÈS : Le graphe est Eulérien.");
                    Hierholzer.cycle(g, true);
                } else {
                    System.out.println("Le graphe n'est pas Eulérien.");
                    Hierholzer.cycle(g, true);
                }
            } else if (hypothese == 3) {
                if (test) {
                    System.out.println("SUCCÈS : Le graphe est Eulérien.");
                    Hierholzer.cycle(g, true);
                } else {
                    System.out.println("Le graphe n'est pas Eulérien.");
                    Hierholzer.cycle(g, true);
                }
            }
        } else if (cas == 2) {
            if (hypothese == 1) {
                if (sommetsImpairs.size() == 2) {
                    System.out.println("Le graphe n'est pas Eulérien (2 sommets impairs : " + sommetsImpairs + ")");
                    Hierholzer.chemin(g, g.getSommet("A"), sommetsImpairs);
                } else {
                    System.out.println("ÉCHEC !!! Le fichier chargé n'a pas exactement 2 sommets impairs.");
                    System.out.println("Nombre de sommets impairs trouvés : " + sommetsImpairs.size());
                }
            } else if ((hypothese == 2) || (hypothese == 3)) {
                if (sommets_non_equilibres.isEmpty()) {
                    System.out.println("SUCCÈS : Le graphe est Eulérien.");
                    Hierholzer.cycle(g, true);
                } else {
                    System.out.println("Le graphe n'est pas Eulérien.");
                    Postier.lancer(g, 2);
                }
            }
        } else if (cas == 3) {
            if (hypothese == 1) {
                    System.out.println("Le graphe n'est pas Eulérien");
                    Postier.lancer(g, hypothese);
            } else if (hypothese == 2) {
                if (sommets_non_equilibres.isEmpty()) {
                    System.out.println("SUCCÈS : Le graphe est Eulérien.");
                    Hierholzer.cycle(g, true);
                } else {
                    System.out.println("Le graphe n'est pas Eulérien.");
                    Postier.lancer(g, hypothese);
                }
            } /*else if (hypothese == 3) {
                if (sommets_non_equilibres.isEmpty()) {
                    System.out.println("SUCCÈS : Le graphe est Eulérien.");
                    Hierholzer.cycle(g, true);
                } else {
                    System.out.println("Le graphe n'est pas Eulérien.");
                    Postier.lancer(g, hypothese);
                }
            }*/
        }
    }
    // Execution du theme 3
    // Méthode pour exécuter l'hypothese 1 du Thème 3
    private void executer_p4(String titre) {
        System.out.println("\n" + titre);

        // Choix du graphe pour la planification
        System.out.println("Veuillez choisir un graphe pour la planification :");
        System.out.println("  [1] Graphe 1");
        System.out.println("  [2] Graphe 2");
        System.out.print("Saisir votre choix : ");
        int grapheChoisi = options(1, 2);

        // Fichiers de sommets et d'arêtes
        String f_sommets = "ressources/sommets.txt"; // commun aux deux graphes
        String f_aretes = "ressources/aretes_t3_h1_ho1." + grapheChoisi + ".txt";

        // Chargement du graphe
        Graphe g = null;
        try {
            g = new Graphe(f_sommets, f_aretes);
            System.out.println("\nGraphe chargé avec succès (" + g.get_Sommets().size() + " sommets).");
        } catch (Exception e) {
            System.err.println("!!! Erreur de chargement !!! " + e.getMessage());
            return;
        }

        // Création de l'instance WelshPowell et coloration
        WelshPowell wp = new WelshPowell(g);
        System.out.println("\nApplication de l'algorithme Welsh et Powell :");
        wp.coloration();
        wp.afficherCouleurs();

        System.out.println("\nNombre total de créneaux nécessaires : " + wp.getNbCouleurs());
    }

    // Méthode pour l'hypothèse 2 du thème 3
    private void executer_p5(String titre) {
        System.out.println("\n" + titre);

        // Choix du graphe pour la planification
        System.out.println("Veuillez choisir un graphe pour la planification :");
        System.out.println("  [1] Graphe 1");
        System.out.println("  [2] Graphe 2");
        System.out.print("Saisir votre choix : ");
        int grapheChoisi = options(1, 2);

        // Fichiers de sommets et d'arêtes
        String f_sommets = "ressources/sommets.txt"; // commun aux deux graphes
        String f_aretes = "ressources/aretes_t3_h1_ho1." + grapheChoisi + ".txt";
        String f_quantites = "ressources/sommets_t3_h2.txt";
        // Chargement du graphe
        Graphe g = null;
        try {
            g = new Graphe(f_sommets, f_aretes);
            System.out.println("\nGraphe chargé avec succès (" + g.get_Sommets().size() + " sommets).");
        } catch (Exception e) {
            System.err.println("!!! Erreur de chargement !!! " + e.getMessage());
            return;
        }

        // Lecture des quantités depuis le fichier
        Map<Sommet, Integer> quantites = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(f_quantites))) {
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine().trim();
                if (!ligne.isEmpty()) {
                    String[] parts = ligne.split(";");
                    if (parts.length == 2) {
                        String id = parts[0].trim().toUpperCase();
                        int q = Integer.parseInt(parts[1].trim());
                        Sommet s = g.getSommet(id);
                        if (s != null) {
                            quantites.put(s, q);
                        } else {
                            System.err.println("Sommet inconnu dans le graphe : " + id);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lecture fichier quantités : " + e.getMessage());
            return;
        }

        // Demande de la capacité du camion
        System.out.print("Entrez la capacité maximale du camion : ");
        int capaciteCamion = options(1, 1000);

        // Création de l'instance WelshPowellHypothese2
        WPBis wp = new WPBis();

        // Application de l'algorithme et affichage du planning
        System.out.println("\nApplication de l'algorithme Welsh et Powell pour l'Hypothèse 2 :");
        wp.coloration(quantites);
        wp.afficherPlanning(quantites);

        System.out.println("\nNombre total de jours nécessaires : " + wp.getNbJour());

    }



    private int options(int min, int max) {
        int choix = 0;
        while (true) {
            try {
                choix = scanner.nextInt();
                if (choix >= min && choix <= max) {
                    scanner.nextLine();
                    return choix;
                } else {
                    System.out.print("!!! Choix invalide !!! Veuillez entrer un nombre entre " + min + " et " + max + " : ");
                }
            } catch (InputMismatchException e) {
                System.out.print("!!! Entrée invalide !!! Veuillez entrer un nombre : ");
                scanner.nextLine();
            }
        }
    }

    private void attente() {
        System.out.println("\nAppuyez sur [Entrée] pour retourner au menu principal");
        scanner.nextLine();
    }
}