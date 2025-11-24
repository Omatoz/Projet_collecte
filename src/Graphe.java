import java.util.*;
import java.io.*;

public class Graphe {
    private Map<String, Sommet> sommets = new HashMap<>();

    // constructeur
    public Graphe(String fichier_sommets, String fichier_aretes) throws FileNotFoundException {
        charger_Sommets(fichier_sommets);
        charger_Rues(fichier_aretes);
    }

    // constructeur copie
    public Graphe(Graphe autre) {
        // Copie des sommets
        for (Sommet s : autre.get_Sommets()) {
            this.ajouter_Sommet(s.id);
        }
        // Copie des arêtes
        for (Sommet s : autre.get_Sommets()) {
            Sommet sourceCopie = this.getSommet(s.id);
            for (Arete a : s.aretes) {
                Sommet destCopie = this.getSommet(a.destination.id);
                sourceCopie.ajouter_arete(destCopie, a.poids);
            }
        }
    }

    // methode : charge sommets graphe
    private void charger_Sommets(String f1) throws FileNotFoundException {
        File fichier = new File(f1); // crée objet du fichier

        try (Scanner scanner = new Scanner(fichier)) { // on teste ouverture fichier

            if (scanner.hasNextLine()) {
                scanner.nextLine(); // lecture + ignore en-tête
            }

            while (scanner.hasNextLine()) { // tant qu'il y a une ligne à lire
                String ligne = scanner.nextLine(); // lecture ligne
                if (!ligne.trim().isEmpty()) { // verification ligne non vide
                    ajouter_Sommet(ligne.trim()); // ajout nouveau sommet
                }
            }
        }
        System.out.println(this.get_Sommets().size() + " sommets chargés depuis " + f1);
    }

    // methode : charge rues graphes
    private void charger_Rues(String nomFichier) throws FileNotFoundException {
        File fichier = new File(nomFichier); // crée objet du fichier

        try (Scanner scanner = new Scanner(fichier)) { // on teste ouverture fichier

            if (scanner.hasNextLine()) {
                scanner.nextLine(); // lecture + ignore en-tête
            }

            while (scanner.hasNextLine()) { // tant qu'il y a une ligne à lire
                String ligne = scanner.nextLine(); // lecture ligne

                if (!ligne.trim().isEmpty()) { // ignorer lignes vides
                    String[] donnees = ligne.split(";"); // séparation données ligne par point-virgule

                    if (donnees.length == 4) { // verification nombre de colonnes
                        String source = donnees[0];
                        String destination = donnees[1];
                        int poids = Integer.parseInt(donnees[2]);
                        int type = Integer.parseInt(donnees[3]);

                        ajouter_Rues(source, destination, poids, type); // on crée les aretes
                    }
                }
            }
        }
        System.out.println("Rues chargées depuis " + nomFichier);
    }

    public void ajouter_Sommet(String id) {
        sommets.putIfAbsent(id.toUpperCase(), new Sommet(id.toUpperCase()));
    }

    //getters
    public Sommet getSommet(String id) {
        return sommets.get(id.toUpperCase());
    }

    public Collection<Sommet> get_Sommets() {
        return sommets.values();
    }

    public void ajouter_Rues(String depart, String arrivee, int poids, int type) {
        Sommet source = getSommet(depart);
        Sommet destination = getSommet(arrivee);

        if (source == null || destination == null) {
            System.err.println("Erreur !!! Source ou Destination inconnue !!!");
            return;
        }

        switch (type) {
            case 1: // une voie deux sens (orienté)
            case 3: // Double voies differentes  (2 orientés)
                source.ajouter_arete(destination, poids); // ajout arete des deux sens
                destination.ajouter_arete(source, poids);
                break;
            case 2: // sens unique  (orienté)
                source.ajouter_arete(destination, poids);
                break;
        }
    }
}
