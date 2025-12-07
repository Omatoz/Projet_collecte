import java.io.*;
import java.util.*;

public class Menu {

    private Scanner scanner;
    private Entreprise entreprise;
    private Collectivite collectivite;

    public Menu() {
        this.scanner = new Scanner(System.in); // cree scanner
        this.entreprise = new Entreprise(scanner); // cree instance entreprise
        this.collectivite = new Collectivite(scanner); // cree instance collectivite
    }

    public void lancer() {

        // Boucle principale : tant que l'utilisateur ne choisit pas de quitter
        while (true) {
            afficher_menu_principal();
            int theme = options(1, 3);

            switch (theme) {
                case 1:
                    entreprise.menu_entreprise(); // menu entreprise
                    break;
                case 2:
                    collectivite.menu_collectivite(); // menu collectivites
                    break;
                case 3:
                    System.out.println("Au revoir :) !!!");
                    scanner.close(); // ferme scanner
                    return;
            }
        }
    }

    private void afficher_menu_principal() {
        System.out.println("\n[SYSTÈME D'AIDE À LA GESTION ET À LA COLLECTE DES DÉCHETS]");
        System.out.println("BONJOUR ! Veuillez vous identifiez : ");
        System.out.println("  [1] Entreprise de collecte");
        System.out.println("  [2] Collectivité territoriale");
        System.out.println("  [3] Quitter");
        System.out.print("Saisir votre rôle : ");
    }

    // methode pour choix des fonctionnalites
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