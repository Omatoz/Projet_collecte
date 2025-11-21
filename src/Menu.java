import java.io.*;
import java.util.*;

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
                    System.out.println("THÈME 2 : Optimiser les ramassages des points de collecte");
                    System.out.println("Fonctionnalité non implémentée pour le moment.");
                    attente();
                    break;
                case 3:
                    System.out.println("THÈME 3 : Planifier les jours de passage dans les quartiers");
                    System.out.println("Fonctionnalité non implémentée pour le moment.");
                    attente();
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
        System.out.print("\nVotre choix : ");
    }

    private void choix_theme1() {
        System.out.println("\nTHÈME 1 : Ramassage aux pieds des habitations");
        System.out.println("Veuillez choisir une problématique :");
        System.out.println("  [1] Problématique 1 : Tournée des encombrants (TSP)");
        System.out.println("  [2] Problématique 2 : Tournée des poubelles (Postier Chinois)");
        System.out.print("Saisir votre choix : ");

        int problematique = options(1, 2);

        if (problematique == 1) {
            executer_p1();
        } else {
            executer_p2();
        }
        attente();
    }

    private void executer_p1() {
        System.out.println("\nProblématique 1 : Tournée des encombrants (TSP)");
        System.out.println("Veuillez choisir une hypothèse de circulation :");
        System.out.println("  [1] HO1 : Graphe non-orienté");
        System.out.println("  [2] HO2 : Graphe orienté");
        System.out.println("  [3] HO3 : Graphe mixte");
        System.out.print("Saisir votre choix : ");
        int hypothese = options(1, 3);

        Graphe g = null;
        try {
            String fichier_aretes = "ressources/aretes_ho" + hypothese + ".txt";
            String fichier_sommets = "ressources/sommets.txt";
            g = new Graphe(fichier_sommets, fichier_aretes);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("ERREUR !!! Impossible de construire la carte : " + e.getMessage());
            return;
        }

        Sommet depot;
        List<Sommet> points_a_visiter = new ArrayList<>();

        while (true) {
            System.out.println("\nTOURNEE");
            System.out.println("Sommets disponibles : " + g.get_Sommets());

            System.out.print("Veuillez saisir le sommet de départ (dépôt) : ");
            String depot_Id = scanner.next();
            depot = g.getSommet(depot_Id);

            if (depot == null) {
                System.out.println("!!! Erreur ::: Le sommet '" + depot_Id + "' n'existe pas. Veuillez réessayer. !!!");
                scanner.nextLine(); // nettoie fin de ligne
            } else {

                System.out.println("Veuillez entrer les IDs des sommets à visiter (10 max), séparés par des espaces :");
                scanner.nextLine();
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
            System.out.println("--> REUSSITE : Tournée calculée avec succès !!!");
            System.out.println("--> Ordre de visite optimisé : " + resultat.getOrdre());
            System.out.println("--> Distance totale de la tournée : " + resultat.getDistance_totale());
            System.out.println("--> Chemin complet : " + resultat.getChemin_final());
        } else {
            System.out.println("--> ÉCHEC : La tournée est irréalisable !!!");
            System.out.println("--> Tournée effectuée : " + resultat.getOrdre());
        }
    }

    private void executer_p2() {
        System.out.println("\nPROBLÉMATIQUE 2 : Organiser la collecte des poubelles (Cycle Eulérien)");
        System.out.println("L'objectif est de passer par toutes les rues une seule fois et revenir au départ.");

        System.out.println("\nChoisissez le niveau de complexité :");
        System.out.println("  [1] Cas Idéal : Graphe entièrement pair (Algorithme de Hierholzer)");
        System.out.println("  [2] Cas Semi-Eulérien (2 sommets impairs) [À venir]");
        System.out.println("  [3] Cas Général (Postier Chinois) [À venir]");
        System.out.print("Votre choix : ");

        // Pour l'instant, on bloque sur le choix 1 car c'est le seul codé
        int choix = options(1, 1);

        if (choix == 1) {
            System.out.println("\n--- Chargement du Graphe de Test (Cas Idéal) ---");
            System.out.println("Veuillez vous assurer que vos fichiers sont dans le dossier 'ressources/aretes_p2_c1_ho1'.");

            // On demande les noms de fichiers pour te permettre de tester tes graphes "faits main"
            System.out.print("Nom du fichier sommets  : ");
            String f_sommets = "ressources/sommets.txt";

            System.out.print("Nom du fichier arêtes : ");
            String f_aretes = "ressources/aretes_p2_c1_ho1.txt";

            Graphe g = null;
            try {
                g = new Graphe(f_sommets, f_aretes);
                System.out.println("Graphe chargé avec succès (" + g.get_Sommets().size() + " sommets).");
            } catch (Exception e) {
                System.err.println("!!! Erreur de chargement : " + e.getMessage());
                return; // On retourne au menu précédent
            }

            System.out.println("\n--- Vérification des conditions (Théorème d'Euler) ---");

            // Appel à ta classe Eulerien (Statique)
            if (Eulerien.verifierConditionEulerienne(g)) {
                System.out.println("--> SUCCÈS : Le graphe est bien Eulérien (tous les sommets sont de degré pair).");
                System.out.println("--> Lancement de l'algorithme de Hierholzer...");

                try {
                    // Appel à ta classe AlgoHierholzer
                    List<String> cycle = Hierholzer.trouverCycleEulerien(g);

                    System.out.println("\n[RÉSULTATS]");
                    System.out.println("--> Tournée calculée avec succès !");
                    System.out.println("--> Nombre de rues parcourues : " + (cycle.size() - 1));
                    System.out.println("--> Itinéraire du camion :");

                    // Affichage joli du chemin A -> B -> C -> A
                    System.out.println(String.join(" -> ", cycle));

                } catch (Exception e) {
                    System.err.println("Une erreur est survenue pendant l'algorithme : " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                System.out.println("--> ÉCHEC : Le graphe chargé n'est pas un graphe Eulérien.");
                System.out.println("    Raison : Certains sommets ont un nombre impair de rues.");
                System.out.println("    Solution : Chargez un fichier différent pour le 'Cas Idéal' ou attendez l'implémentation du Cas 2.");
            }
        }

        attente();
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