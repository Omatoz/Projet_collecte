import java.io.*;
import java.util.*;

public class MST{
    private List<Prim.Arete> aretes;
    private int nbSommets;
    private int[] poids;   //poids de chaque sommet
    private int capacite;   //capacite du camion

    //getters
    public List<Prim.Arete> getAretes(){
        return aretes;
    }
    public int getNbSommets(){
        return nbSommets;
    }
    public int[] getPoids(){
        return poids;
    }
    public int getCapacite(){
        return capacite;
    }

    public void graphe(String fichier) throws IOException{
        Scanner sc = new Scanner(new File(fichier)); //on lit le fichier
        nbSommets = sc.nextInt();  //lecture de premier element qui est le nb de sommets
        poids = new int[nbSommets];
        for (int i=0; i<nbSommets; i++){   //pour chaque sommet on lit le poids (contenances)
            poids[i] = sc.nextInt();
        }

        aretes = new ArrayList<>();
        while (sc.hasNextLine()){   //on verifie qu'il y a encore une ligne
            int sommetDepart = sc.nextInt();//ATTENTION : les mettre en String
            int sommetArrive = sc.nextInt();
            int poids = sc.nextInt();
            aretes.add(new Prim.Arete(sommetDepart, sommetArrive, poids));   //on appel la méthode de Prim qui permet d'ajouter une arete
        }
        sc.close();
    }

    public MST(String fichier, int capacite) throws IOException {  //constructeur qui
        this.aretes = new ArrayList<>();
        this.capacite = capacite;
        graphe(fichier);
    }

    public List<List<Integer>> tour(){
        List<Prim.Arete> arbreCouvrantMinimal = Prim.arbreCouvrantMinimal(aretes, nbSommets);  //creation arbre couvrant de poids minimum
        DFS dfs = new DFS(nbSommets);  //dfs sur arbre couvrant
        for(Prim.Arete a : arbreCouvrantMinimal){   //on parcourt chaque arete de l'arbre
            dfs.ajouterArete(a.sommetDepart, a.sommetArrivee);
        }
        dfs.dfs(0);  //on part du sommet de depart
        List<Integer> shortcut = dfs.shortcutting(0);

        List<List<Integer>> tours  = new ArrayList<>();
        List<Integer> tour = new ArrayList<>();
        int contenanceActuelle = 0;

        for(int sommet : shortcut){  //on parcours les sommets de la tournee shortcut
            int contenance = poids[sommet];
            if(contenanceActuelle + contenance > capacite){   //condition pour savoir si on peut collecter le sommet suivant
                tour.add(0);
                contenanceActuelle = 0;
            }
            tour.add(sommet);
            contenanceActuelle += contenance;
        }

        if(!tour.isEmpty()){   //si on arrive à la fin du parcours
            tour.add(0);
            tours.add(tour);
        }

        return tours;
    }


    public int poidsTotal(List<List<Integer>> tours, Graphe graphe){
        int poidsTotal = 0;
        for(List<Integer> tour : tours){  //on parcours chaque tour de la somme des tours
            for(int i=0; i<tour.size()-1; i++){
                List<Sommet> sommets = new ArrayList<>(graphe.get_Sommets());   //convertit Collection en ArrayList
                Sommet sommetDepart = sommets.get(tour.get(i));
                Sommet sommetArrivee = sommets.get(tour.get(i+1));
                Itineraire.Dijkstra dijkstra = Itineraire.trouver_chemin(graphe, sommetDepart, sommetArrivee);
                poidsTotal += dijkstra.getDistance();
            }
        }

        return poidsTotal;
    }



}
