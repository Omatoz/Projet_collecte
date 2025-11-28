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
                sourceCopie.ajouter_arete(destCopie, a.poids,  a.type);
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
    // Dans Graphe.java
    private void charger_Rues(String nomFichier) throws FileNotFoundException {
        File f = new File(nomFichier);
        System.out.println(">>> LECTURE DU FICHIER : " + f.getAbsolutePath()); // VÉRIFIEZ CE CHEMIN !

        try (Scanner scanner = new Scanner(f)) {
            if (scanner.hasNextLine()) scanner.nextLine();

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                // On ignore les lignes vides
                if (!ligne.trim().isEmpty()) {
                    String[] donnees = ligne.split(";");
                    if (donnees.length == 4) {
                        // LE TRIM() EST OBLIGATOIRE POUR EVITER LES BUGS
                        String source = donnees[0].trim().toUpperCase();
                        String dest = donnees[1].trim().toUpperCase();
                        int poids = Integer.parseInt(donnees[2].trim());
                        int type = Integer.parseInt(donnees[3].trim());

                        ajouter_Rues(source, dest, poids, type);
                    }
                }
            }
        }
        System.out.println("Rues chargées.");
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

    public void ajouter_Arc(String idSource, String idDestination, int poids, int typeOrigine) {
        Sommet source = getSommet(idSource);
        Sommet destination = getSommet(idDestination);
        if (source != null && destination != null) {
            // On ajoute un seul et unique arc
            source.ajouter_arete(destination, poids, typeOrigine);
        }
    }

    public void ajouter_Rues(String depart, String arrivee, int poids, int type) {
        Sommet source = getSommet(depart);
        Sommet destination = getSommet(arrivee);

        if (source == null || destination == null) {
            System.err.println("Erreur !!! Source ou Destination inconnue !!!");
            return;
        }

        switch (type) {
            case 1: // une voie deux sens (non orienté)
            case 3: // Double voies differentes  (2 orientés)
                source.ajouter_arete(destination, poids, type); // ajout arete des deux sens
                destination.ajouter_arete(source, poids, type);
                break;
            case 2: // sens unique  (orienté)
                source.ajouter_arete(destination, poids, type);
                break;
        }

    }

    // stockage en mémoire vive (matrice d'adjacence)
    /*public void afficherGraphe(){
        List<String> listeSommets = new ArrayList<>(sommets.keySet());
        Collections.sort(listeSommets);

        int ordre = listeSommets.size();
        int infini=999;

        int[][] matriceAdjacence = new int[ordre][ordre];

        for (int i = 0; i < ordre; i++) {
            Sommet sommet = sommets.get(listeSommets.get(i));
            for(Arete a : sommet.aretes) {
                int j = listeSommets.indexOf(a.destination.id);
                matriceAdjacence[i][j] = a.poids;
            }
        }

        System.out.println("Matrice d'adjacence du graphe: ");
        System.out.println("         ");

        for (String id : listeSommets){
            System.out.println(id);
        }

        for(int i=0;i<ordre;i++){
            for(int j=0;j<ordre;j++){
                if(matriceAdjacence[i][j]==infini){
                    System.out.println("∞ ");
                }else{
                    System.out.println(matriceAdjacence[i][j]);
                }
            }
        }
    }
     */
}


