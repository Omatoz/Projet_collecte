import java.util.*;

public class DFS {
    //tableaux car attributs pour chaque sommets
    private int[] marque;
    private String[] predecesseur;
    private int[] distance;
    private List<List<Integer>> sommetsAdjacents; //liste des sommets adjacents de la liste de tous les sommets
    private List<Integer> sommetsGraphe;
    //getters
    public int[] getMarque(){
        return marque;
    }
    public String[] getPredecesseur(){
        return predecesseur;
    }
    public int[] getDistance(){
        return distance;
    }
    public List<List<Integer>> getSommetsAdjacents(){
        return sommetsAdjacents;
    }
    public List<Integer> getSommetsGraphe(){
        return sommetsGraphe;
    }

    public DFS(int nbSommets){ //constructeur
        marque = new int[nbSommets];
        predecesseur = new String[nbSommets];
        distance = new int[nbSommets];
        sommetsAdjacents = new ArrayList<>();
        sommetsGraphe = new ArrayList<>();
        for (int i=0; i<nbSommets; i++){     //pour chaque sommet on creer une liste avec ses sommets adjacents
            sommetsAdjacents.add(new ArrayList<>());
        }
    }

    public void ajouterArete(int sommetDepart, int sommetArrivee){
        sommetsAdjacents.get(sommetDepart).add(sommetArrivee);
        sommetsAdjacents.get(sommetArrivee).add(sommetDepart);
    }

    public void dfsRecursif(int sommet){
        for(int successeur : sommetsAdjacents.get(sommet)){ //on parcours tous les sucesseurs du sommet qu'on explore
            if(marque[successeur] == 0){  //verifie si sommet pas encore marqué
                marque[successeur] = 1;  //on marque le sommet
                predecesseur[successeur] = String.valueOf(sommet);
                distance[successeur] = distance[sommet] + 1;
                dfsRecursif(successeur);   //appel récursif
            }
        }

    }

    public void dfs(int sommetInitiale){
        //attributs avant d'être parcouru
        Arrays.fill(marque, 0);
        Arrays.fill(predecesseur, "-");
        Arrays.fill(distance, 999);  //infini

        //on initialise attributs du sommet qu'on découvre
        marque[sommetInitiale] = 0;
        distance[sommetInitiale] = 0;
        predecesseur[sommetInitiale] = "-";

        dfsRecursif(sommetInitiale);    //appel récursif
    }



    //shortcutting

    public void shortcutRecursif(int sommetDepart, boolean[] cheminShortcutting, List<Integer> chemin){
        if(!cheminShortcutting[sommetDepart]){  //verfie si deja ajouté à liste
            cheminShortcutting[sommetDepart] = true;  //on parcours seulement si pas encore ajouté
            chemin.add(sommetDepart);   //on ajoute
        }
        for(int sucesseur : sommetsAdjacents.get(sommetDepart)){   //on parcours les successeurs du sommet
            if(!cheminShortcutting[sucesseur]){   //verifie si pas encore parcouru
                shortcutRecursif(sucesseur, cheminShortcutting, chemin);  //recursivité
            }
        }
    }


    public List<Integer> shortcutting(int sommetDepart){
        List<Integer> chemin = new ArrayList<>();  //on stocke sommets de parcours du chemin
        boolean[] cheminShortcutting = new boolean[marque.length];    //on marque les sommets déjà parcouru
        shortcutRecursif(sommetDepart, cheminShortcutting, chemin);

        return chemin;
    }

}
