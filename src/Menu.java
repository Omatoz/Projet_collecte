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
            int theme = choix(1, 4);

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
                    System.out.println("Au revoir !!!");
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

        int problematique = choix(1, 2);

        if (problematique == 1) {
            executer_p1();
        } else {
            System.out.println("\nProblématique 2 (Postier Chinois) non implémentée pour le moment.");
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
        int hypothese = choix(1, 3);

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
            System.out.println("\nDéfinition de la tournée");
            System.out.println("Sommets disponibles sur la carte : " + g.get_Sommets());

            System.out.print("Veuillez entrer l'ID du sommet de départ (dépôt) : ");
            String depot_Id = scanner.next().toUpperCase();
            depot = g.getSommet(depot_Id);

            if (depot == null) {
                System.out.println("!!! Erreur : Le sommet '" + depot_Id + "' n'existe pas. Veuillez réessayer. !!!");
                scanner.nextLine(); // Nettoie la fin de ligne
                continue;
            }

            System.out.println("Veuillez entrer les IDs des sommets à visiter (10 max), séparés par des espaces :");
            scanner.nextLine();
            String s = scanner.nextLine().toUpperCase();

            String[] liste = s.split("\\s+");

            boolean valide = true;
            points_a_visiter.clear();

            if (liste.length > 10) {
                System.out.println("!!! Erreur !!! Vous ne pouvez pas entrer plus de 10 sommets. !!!");
                valide = false;
            } else {
                for (String id : liste) {
                    if (id.isEmpty()) continue;
                    Sommet sommet = g.getSommet(id);
                    if (sommet == null) {
                        System.out.println("!!! Erreur !!! Le sommet '" + id + "' n'existe pas. !!!");
                        valide = false;
                        break;
                    } else if (sommet.equals(depot)) {
                        System.out.println("Le dépôt '" + id + "' sera ignoré de la liste. !!!");
                    } else {
                        points_a_visiter.add(sommet);
                    }
                }
            }
            if (valide) {
                break;
            }
            else {
                System.out.println("Veuillez réessayer de définir la tournée.");
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

    private int choix(int min, int max) {
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