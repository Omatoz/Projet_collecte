import java.util.*;
import java.io.*;

public class Graphe {

    private Map<String, Sommet> sommets = new HashMap<>();

    public Graphe(String fichierSommets, String fichierAretes) throws FileNotFoundException {
        charger_Sommets(fichierSommets);
        charger_Rues(fichierAretes);
    }

    // Constructeur de copie
    public Graphe(Graphe autre) {
        for (Sommet s : autre.get_Sommets()) {
            this.ajouter_Sommet(s.id);
        }
        for (Sommet s : autre.get_Sommets()) {
            Sommet copieSource = this.getSommet(s.id);
            for (Arete a : s.aretes) {
                Sommet depart = this.getSommet(a.depart.id);
                Sommet dest = this.getSommet(a.destination.id);
                copieSource.ajouter_arete(depart, dest, a.poids, a.type);
            }
        }
    }

    private void charger_Sommets(String fichier) throws FileNotFoundException {
        File f = new File(fichier);

        try (Scanner sc = new Scanner(f)) {
            if (sc.hasNextLine()) sc.nextLine(); // en-tête
            while (sc.hasNextLine()) {
                String ligne = sc.nextLine().trim();
                if (!ligne.isEmpty()) {
                    ajouter_Sommet(ligne);
                }
            }
        }
        System.out.println(sommets.size() + " sommets chargés.");
    }

    private void charger_Rues(String fichier) throws FileNotFoundException {
        File f = new File(fichier);
        System.out.println(">>> Lecture : " + f.getAbsolutePath());

        try (Scanner sc = new Scanner(f)) {
            if (sc.hasNextLine()) sc.nextLine();
            while (sc.hasNextLine()) {
                String ligne = sc.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] d = ligne.split(";");
                if (d.length != 4) continue;

                String source = d[0].trim().toUpperCase();
                String dest = d[1].trim().toUpperCase();
                int poids = Integer.parseInt(d[2].trim());
                int type = Integer.parseInt(d[3].trim());

                ajouter_Rues(source, dest, poids, type);
            }
        }
    }

    public void ajouter_Sommet(String id) {
        sommets.putIfAbsent(id.toUpperCase(), new Sommet(id.toUpperCase()));
    }

    public Sommet getSommet(String id) {
        return sommets.get(id.toUpperCase());
    }

    public Collection<Sommet> get_Sommets() {
        return sommets.values();
    }

    public void ajouter_Rues(String depart, String arrivee, int poids, int type) {
        Sommet s = getSommet(depart);
        Sommet d = getSommet(arrivee);

        if (s == null || d == null) {
            System.err.println("Erreur : sommet introuvable !");
            return;
        }

        switch (type) {
            case 1:
            case 3:  // double sens
                s.ajouter_arete(s, d, poids, type);
                d.ajouter_arete(d, s, poids, type);
                break;

            case 2:  // sens unique
                s.ajouter_arete(s, d, poids, type);
                break;
        }
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



