import java.io.*;
import java.util.*;

public class Menu {

    private Scanner scanner; // On utilise un seul scanner pour toute l'application
    private Entreprise entreprise;
    private Collectivite collectivite;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.entreprise = new Entreprise(scanner);
        this.collectivite = new Collectivite(scanner);
    }

    public void lancer() {

        // Boucle principale du programme. Elle continue tant que l'utilisateur ne choisit pas de quitter.
        while (true) {
            afficher_menu_principal();
            int theme = options(1, 3);

            switch (theme) {
                case 1:
                    entreprise.menu_entreprise();
                    break;
                case 2:
                    collectivite.menu_collectivite();
                    break;
                case 3:
                    System.out.println("Au revoir :) !!!");
                    scanner.close(); // On ferme le scanner juste avant de quitter.
                    return; // Termine la méthode lancer() et donc le programme.
            }
        }
    }

    private void afficher_menu_principal() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("[SYSTÈME D'AIDE À LA GESTION ET À LA COLLECTE DES DÉCHETS]");
        System.out.println("[1] Entreprise de collecte");
        System.out.println("→ Optimisation des tournées, gestion des camions, organisation des points et circuits de collecte.");
        System.out.println("[2] Collectivité territoriale");
        System.out.println("→ Planification des jours de collecte, gestion des secteurs, équilibrage des charges et nuisances.");
        System.out.println("[3] Quitter");
        System.out.print("Saisir votre rôle : ");
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
}