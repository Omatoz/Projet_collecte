/*import java.io.*;
import java.util.*;

public class MST{
    private List<Prim.Arete> aretes;
    private int nbSommets;
    private int[] poids;   //poids de chaque sommet
    private int capacite;   //capacite du camion
    private Map<Integer, String> lettreSommet;


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
    public Map<Integer, String> getLettreSommet(){
        return lettreSommet;
    }

    public void graphe(String fichier) throws IOException {
        Scanner sc = new Scanner(new File(fichier));
        aretes = new ArrayList<>();
        Set<String> setSommets = new HashSet<>();

        if (sc.hasNextLine()) sc.nextLine();

        List<String[]> lignes = new ArrayList<>();
        while (sc.hasNextLine()) {
            String ligne = sc.nextLine().trim();
            if (ligne.isEmpty()) continue;
            String[] elements = ligne.split(";");
            if (elements.length < 3) continue;
            lignes.add(elements);
            setSommets.add(elements[0].trim());
            setSommets.add(elements[1].trim());
        }
        sc.close();

        List<String> listeSommets = new ArrayList<>(setSommets);
        Collections.sort(listeSommets);
        nbSommets = listeSommets.size();

        Map<String, Integer> mapSommets = new HashMap<>();
        for (int i = 0; i < listeSommets.size(); i++) {
            mapSommets.put(listeSommets.get(i), i);
        }

        poids = new int[nbSommets];
        Arrays.fill(poids, 0);

        for (String[] elements : lignes) {
            String src = elements[0].trim();
            String dest = elements[1].trim();
            int poidsArete = Integer.parseInt(elements[2].trim());
            int srcIndex = mapSommets.get(src);
            int destIndex = mapSommets.get(dest);
            aretes.add(new Prim.Arete(srcIndex, destIndex, poidsArete));
        }

        lettreSommet = new HashMap<>();
        for (Map.Entry<String, Integer> entry : mapSommets.entrySet()) {
            lettreSommet.put(entry.getValue(), entry.getKey());
        }

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
*/