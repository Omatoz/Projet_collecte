import java.io.File;
import java.util.*;

public class Collectivite {

    private Scanner scanner;

    public Collectivite(Scanner scanner) {
        this.scanner = scanner;
    }

    public void menu_collectivite() {
        while (true) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("[MENU COLLECTIVITÉ TERRITORIALE]");
            System.out.println("[1] Planification des jours de collecte");
            System.out.println("→ Welsh & Powell (coloration des quartiers)");
            System.out.println(" → Prise en compte des quantités et capacités (Hyp. 2)");
            System.out.println("[2] Retour au menu principal");
            System.out.print("\nSaisir votre choix : ");

            int choix = options(1, 2);

            switch (choix) {
                case 1:
                    choix_theme3();
                    break;
                case 2:
                    return;
            }
        }
    }

    private void choix_theme3() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("[MENU COLLECTIVITÉ TERRITORIALE]");
        System.out.println("→ Planification des jours de passage dans les differents quartiers :");
        System.out.println("[1] Collecte des secteurs (Welsh et Powell)");
        System.out.println("[2] Collecte des secteurs sans nuisance et en tenant en compte les quantités et les capacités de collecte");
        System.out.print("\nSaisir votre choix : ");

        int hypothese = options(1, 2);

        System.out.println();
        System.out.println();
        System.out.println();

        if (hypothese == 1) {
            executer_p4("[1] Collecte des secteurs (Welsh et Powell)");
        } else {
            executer_p5("[2] Collecte des secteurs sans nuisance et en tenant en compte les quantités et les capacités de collecte");
        }
        attente();
    }

    // Execution du theme 3
    // Méthode pour exécuter l'hypothese 1 du Thème 3
    private void executer_p4(String titre) {
        System.out.println(titre);

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
        System.out.println(titre);

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
}
