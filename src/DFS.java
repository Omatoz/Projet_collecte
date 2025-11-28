import java.util.*;

public class DFS {
    //tableaux car attributs pour chaque sommets
    private int[] marque;
    private int[] predecesseur;
    private int[] distance;
    private List<List<Integer>> sommetsAdjacents; //liste des sommets adjacents de la liste de tous les sommets

    //getters
    public int[] getMarque(){
        return marque;
    }
    public int[] getPredecesseur(){
        return predecesseur;
    }
    public int[] getDistance(){
        return distance;
    }
    public List<List<Integer>> getSommetsAdjacents(){
        return sommetsAdjacents;
    }

    public DFS(int nbSommets){ //constructeur
        marque = new int[nbSommets];
        predecesseur = new int[nbSommets];
        distance = new int[nbSommets];
        sommetsAdjacents = new ArrayList<>();
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
                predecesseur[successeur] = sommet;
                distance[successeur] = distance[sommet] + 1;
                dfsRecursif(successeur);   //appel récursif
            }
        }

    }

    public void dfs(int sommetInitiale){
        //attributs avant d'être parcouru
        Arrays.fill(marque, 0);
        Arrays.fill(predecesseur, -1);
        Arrays.fill(distance, 999);  //infini

        //on initialise attributs du sommet qu'on découvre
        marque[sommetInitiale] = 0;
        distance[sommetInitiale] = 0;
        predecesseur[sommetInitiale] = -1;

        dfsRecursif(sommetInitiale);    //appel récursif
    }

}
